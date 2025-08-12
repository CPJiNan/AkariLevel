package com.github.cpjinan.plugin.akarilevel.database.lock

import com.github.cpjinan.plugin.akarilevel.database.DatabaseType
import kotlinx.coroutines.runBlocking
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.sql.DataSource

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database.lock
 *
 * @author QwQ-dev
 * @since 2025/8/12 04:43
 */
object LockManager {
    private val isInitialized = AtomicBoolean(false)
    private val isShutdown = AtomicBoolean(false)

    @Volatile
    private var distributedLock: DistributedLock? = null

    @Volatile
    private var lockDataSource: DataSource? = null

    private val lockOperations = AtomicLong(0)
    private val lockFailures = AtomicLong(0)
    private val averageAcquisitionTime = AtomicLong(0)

    private val activeLocks = ConcurrentHashMap<String, LockMonitorInfo>()

    fun initialize(dataSource: DataSource) {
        if (isInitialized.compareAndSet(false, true)) {
            try {
                // Only support MySQL for distributed locks
                if (DatabaseType.INSTANCE != DatabaseType.MYSQL) {
                    warning("Distributed locks only supported for MySQL database")
                    return
                }

                lockDataSource = dataSource

                val lockConfig = SimpleLockConfig(
                    defaultTimeoutSeconds = 10,
                    maxRetries = 3
                )

                distributedLock = OptimizedMySQLLock(dataSource, lockConfig)

                // Register shutdown hook
                Runtime.getRuntime().addShutdownHook(Thread {
                    shutdown()
                })

                info("Distributed lock system initialized successfully")

            } catch (e: Exception) {
                warning("Failed to initialize distributed lock system", e)
                isInitialized.set(false)
            }
        }
    }

    fun <T> withMemberDataLock(memberKey: String, action: () -> T): T {
        if (!isInitialized.get() || isShutdown.get()) {
            // Fallback to direct execution if locks are not available
            return action()
        }

        val lockKey = generateMemberLockKey(memberKey)

        return try {
            checkConnectionPoolHealth()

            val startTime = System.currentTimeMillis()
            activeLocks[lockKey] = LockMonitorInfo(lockKey, startTime, Thread.currentThread().name)

            val result = distributedLock?.withLock(lockKey, 10, action)

            val duration = System.currentTimeMillis() - startTime
            updateAverageAcquisitionTime(duration)
            lockOperations.incrementAndGet()

            when (result) {
                is LockResult.Success -> result.value
                is LockResult.LockFailed -> {
                    lockFailures.incrementAndGet()
                    warning("Failed to acquire lock for member: $memberKey - ${result.reason}")
                    action() // Fallback execution
                }

                is LockResult.ExecutionFailed -> {
                    lockFailures.incrementAndGet()
                    throw result.exception
                }

                else -> {
                    lockFailures.incrementAndGet()
                    action() // Fallback execution
                }
            }
        } catch (e: Exception) {
            lockFailures.incrementAndGet()

            // Check for connection pool issues
            val errorMessage = e.message?.lowercase() ?: ""
            if (errorMessage.contains("connection not available") ||
                errorMessage.contains("timeout") ||
                errorMessage.contains("sqlexception")
            ) {

                warning("Connection pool issue detected, attempting recovery")
                attemptConnectionPoolRecovery()
            }

            // Fallback to direct execution
            warning("Lock operation failed for member: $memberKey, executing without lock", e)
            action()
        } finally {
            activeLocks.remove(lockKey)
        }
    }

    suspend fun <T> withMemberDataLockAsync(memberKey: String, action: suspend () -> T): T {
        if (!isInitialized.get() || isShutdown.get()) {
            return action()
        }

        val lockKey = generateMemberLockKey(memberKey)

        return try {
            val lock = distributedLock
            if (lock is OptimizedMySQLLock) {
                val result = lock.withLockAsync(lockKey, 10, action)
                when (result) {
                    is LockResult.Success -> result.value
                    is LockResult.LockFailed -> {
                        lockFailures.incrementAndGet()
                        action()
                    }

                    is LockResult.ExecutionFailed -> throw result.exception
                }
            } else {
                runBlocking {
                    withMemberDataLock(memberKey) { runBlocking { action() } }
                }
            }
        } catch (e: Exception) {
            lockFailures.incrementAndGet()
            warning("Async lock operation failed for member: $memberKey", e)
            action()
        }
    }

    private fun generateMemberLockKey(memberKey: String): String {
        return "member_$memberKey"
    }

    private fun updateAverageAcquisitionTime(duration: Long) {
        val currentAvg = averageAcquisitionTime.get()
        val operations = lockOperations.get()

        if (operations == 1L) {
            averageAcquisitionTime.set(duration)
        } else {
            val newAvg = if (operations > 0) {
                (currentAvg * (operations - 1) + duration) / operations
            } else {
                0L
            }
            averageAcquisitionTime.set(newAvg)
        }
    }

    private fun checkConnectionPoolHealth() {
        try {
            lockDataSource?.connection?.use { connection ->
                if (!connection.isValid(2)) {
                    warning("Database connection validation failed")
                }
            }
        } catch (e: Exception) {
            warning("Connection pool health check failed", e)
        }
    }

    private fun attemptConnectionPoolRecovery() {
        try {
            lockDataSource?.connection?.use { connection ->
                if (connection.isValid(5)) {
                    info("Connection pool recovery successful")
                } else {
                    warning("Connection pool recovery failed - connection still invalid")
                }
            }
        } catch (e: Exception) {
            warning("Connection pool recovery attempt failed", e)
        }
    }

    fun shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            try {
                distributedLock?.let {
                    if (it is OptimizedMySQLLock) {
                        it.shutdown()
                    }
                }
                activeLocks.clear()
                distributedLock = null
                lockDataSource = null

                info("Distributed lock system shutdown completed")
            } catch (e: Exception) {
                warning("Error during lock system shutdown", e)
            }
        }
    }
}

data class LockMonitorInfo(
    val lockKey: String,
    val acquiredAt: Long,
    val threadName: String
)