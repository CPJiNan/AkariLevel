package com.github.cpjinan.plugin.akarilevel.database.lock

import kotlinx.coroutines.*
import taboolib.common.platform.function.warning
import java.sql.Connection
import java.sql.SQLException
import java.sql.SQLTransientException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.sql.DataSource
import kotlin.math.min

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database.lock
 *
 * @author QwQ-dev
 * @since 2025/8/12 04:43
 */
class OptimizedMySQLLock(
    private val dataSource: DataSource,
    private val config: SimpleLockConfig
) : DistributedLock {
    private val isShutdown = AtomicBoolean(false)
    private val lockConnections = ConcurrentHashMap<String, Connection>()

    private val lockScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.IO +
                CoroutineName("AkariLevel-Lock-Scope")
    )

    private val connectionErrors = AtomicInteger(0)
    private val timeoutErrors = AtomicInteger(0)
    private val lockFailures = AtomicInteger(0)
    private val lastErrorTime = AtomicLong(0)
    private val errorReportingThreshold = 60_000L

    @Suppress("SqlNoDataSourceInspection")
    companion object {
        private const val GET_LOCK_SQL = "SELECT GET_LOCK(?, ?)"
        private const val RELEASE_LOCK_SQL = "SELECT RELEASE_LOCK(?)"
        private const val IS_FREE_LOCK_SQL = "SELECT IS_FREE_LOCK(?)"
    }

    override fun tryLock(lockKey: String, timeoutSeconds: Int): Boolean {
        if (isShutdown.get()) {
            return false
        }

        val effectiveTimeout = if (timeoutSeconds > 0) timeoutSeconds else config.defaultTimeoutSeconds

        return try {
            val connection = getConnection()
            val acquired = executeGetLock(connection, lockKey, effectiveTimeout)

            if (acquired) {
                lockConnections[lockKey] = connection
            } else {
                // Close connection if lock was not acquired
                closeConnection(connection)
            }

            acquired
        } catch (e: Exception) {
            return handleLockError(lockKey, e, effectiveTimeout)
        }
    }

    private fun handleLockError(lockKey: String, error: Throwable, timeoutSeconds: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        lastErrorTime.set(currentTime)

        when (error) {
            is SQLException -> {
                when {
                    error.sqlState?.startsWith("08") == true -> {
                        connectionErrors.incrementAndGet()
                        return retryWithBackoff(lockKey, timeoutSeconds, 3, 100L)
                    }

                    error is SQLTransientException || error.message?.contains("timeout", true) == true -> {
                        timeoutErrors.incrementAndGet()
                        return tryLock(lockKey, minOf(timeoutSeconds + 2, 30))
                    }

                    error.message?.contains("lock", true) == true -> {
                        lockFailures.incrementAndGet()
                        if (shouldReportError(currentTime)) {
                            warning("Lock system error for $lockKey: ${error.message}")
                        }
                        return false
                    }

                    else -> {
                        if (shouldReportError(currentTime)) {
                            warning("SQL error for $lockKey: ${error.sqlState} - ${error.message}", error)
                        }
                        return false
                    }
                }
            }

            is InterruptedException -> {
                Thread.currentThread().interrupt()
                return false
            }

            is IllegalStateException -> {
                return if (error.message?.contains("shutdown", true) == true) {
                    false
                } else {
                    retryWithBackoff(lockKey, timeoutSeconds, 2, 50L)
                }
            }

            else -> {
                return retryWithBackoff(lockKey, timeoutSeconds, 1, 100L)
            }
        }
    }

    private fun retryWithBackoff(lockKey: String, timeoutSeconds: Int, maxRetries: Int, baseDelayMs: Long): Boolean {
        repeat(maxRetries) { attempt ->
            try {
                val connection = getConnection()
                val acquired = executeGetLock(connection, lockKey, timeoutSeconds)

                if (acquired) {
                    lockConnections[lockKey] = connection
                    return true
                } else {
                    closeConnection(connection)
                    return false
                }
            } catch (_: Exception) {
                if (attempt < maxRetries - 1) {
                    val delay = baseDelayMs * (1L shl attempt)
                    try {
                        Thread.sleep(delay)
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        return false
                    }
                }
            }
        }
        return false
    }

    private fun shouldReportError(currentTime: Long): Boolean {
        return currentTime - lastErrorTime.get() > errorReportingThreshold
    }

    override fun unlock(lockKey: String): Boolean {
        val connection = lockConnections.remove(lockKey)

        return if (connection != null) {
            try {
                val released = executeReleaseLock(connection, lockKey)
                closeConnection(connection)
                released
            } catch (e: Exception) {
                warning("Failed to release lock: $lockKey", e)
                closeConnection(connection)
                false
            }
        } else {
            false
        }
    }

    override fun isLocked(lockKey: String): Boolean {
        if (isShutdown.get()) return false

        if (lockConnections.containsKey(lockKey)) {
            return true
        }

        // Query MySQL to check lock status
        return try {
            getConnection().use { connection ->
                executeIsFreeLock(connection, lockKey).not()
            }
        } catch (e: Exception) {
            warning("Failed to check lock status: $lockKey", e)
            false
        }
    }

    override fun <T> withLock(
        lockKey: String,
        timeoutSeconds: Int,
        action: () -> T
    ): LockResult<T> {
        return executeWithLock(lockKey, timeoutSeconds) { action() }
    }

    private fun tryLockWithRetry(lockKey: String): Boolean {
        val maxRetries = config.maxRetries

        repeat(maxRetries + 1) { attempt ->
            if (tryLock(lockKey, 1)) { // 1-second timeout per attempt
                return true
            }

            if (attempt < maxRetries) {
                try {
                    // Exponential backoff with jitter
                    val baseDelay = 50L * (1 shl attempt) // 50ms, 100ms, 200ms, 400ms...
                    val jitter = (Math.random() * 50).toLong() // Add randomness
                    val delay = min(baseDelay + jitter, 2000L) // Cap at 2 seconds

                    Thread.sleep(delay)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return false
                }
            }
        }

        return false
    }

    private fun getConnection(): Connection {
        if (isShutdown.get()) {
            throw IllegalStateException("Lock system is shutdown")
        }

        val connection = dataSource.connection
        connection.autoCommit = true
        return connection
    }

    private fun closeConnection(connection: Connection?) {
        connection?.let {
            try {
                if (!it.isClosed) {
                    it.close()
                }
            } catch (e: Exception) {
                warning("Error closing connection", e)
            }
        }
    }

    private fun <T> executeWithLock(
        lockKey: String,
        timeoutSeconds: Int,
        action: () -> T
    ): LockResult<T> {
        return if (tryLockWithRetry(lockKey)) {
            try {
                val result = action()
                LockResult.Success(result)
            } catch (e: Exception) {
                LockResult.ExecutionFailed(e)
            } finally {
                unlock(lockKey)
            }
        } else {
            LockResult.LockFailed("Failed to acquire lock within ${timeoutSeconds}s: $lockKey")
        }
    }

    private fun executeQuery(
        connection: Connection,
        sql: String,
        lockKey: String,
        timeoutSeconds: Int? = null
    ): Boolean {
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, lockKey)
            timeoutSeconds?.let { statement.setInt(2, it) }

            statement.executeQuery().use { resultSet ->
                return if (resultSet.next()) {
                    resultSet.getInt(1) == 1
                } else {
                    false
                }
            }
        }
    }

    private fun executeGetLock(connection: Connection, lockKey: String, timeoutSeconds: Int): Boolean {
        return executeQuery(connection, GET_LOCK_SQL, lockKey, timeoutSeconds)
    }

    private fun executeReleaseLock(connection: Connection, lockKey: String): Boolean {
        return executeQuery(connection, RELEASE_LOCK_SQL, lockKey)
    }

    private fun executeIsFreeLock(connection: Connection, lockKey: String): Boolean {
        return try {
            executeQuery(connection, IS_FREE_LOCK_SQL, lockKey)
        } catch (_: Exception) {
            true // Default to free if query fails
        }
    }

    suspend fun tryLockAsync(lockKey: String, timeoutSeconds: Int): Boolean = withContext(Dispatchers.IO) {
        tryLock(lockKey, timeoutSeconds)
    }

    suspend fun <T> withLockAsync(
        lockKey: String,
        timeoutSeconds: Int,
        action: suspend () -> T
    ): LockResult<T> = withContext(Dispatchers.IO) {
        executeWithLock(lockKey, timeoutSeconds) { runBlocking { action() } }
    }

    fun shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            runBlocking {
                lockScope.cancel()

                try {
                    lockScope.coroutineContext[Job]?.join()
                } catch (_: Exception) {
                    // Ignore cancellation exceptions
                }
            }

            // Release all active locks
            val activeLockKeys = lockConnections.keys.toList()

            activeLockKeys.forEach { lockKey ->
                try {
                    unlock(lockKey)
                } catch (e: Exception) {
                    warning("Failed to release lock during shutdown: $lockKey", e)
                }
            }

            lockConnections.clear()
        }
    }
}

data class SimpleLockConfig(
    val defaultTimeoutSeconds: Int = 10,
    val maxRetries: Int = 3
)