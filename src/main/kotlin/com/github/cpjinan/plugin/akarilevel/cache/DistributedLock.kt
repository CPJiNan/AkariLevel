package com.github.cpjinan.plugin.akarilevel.cache

import javax.sql.DataSource

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * 分布式锁接口。
 *
 * @author QwQ-dev
 * @since 2025/8/12 17:50
 */
interface DistributedLock {
    fun tryLock(lockKey: String, timeoutSeconds: Int = 5): Boolean
    fun unlock(lockKey: String): Boolean
    fun isLocked(lockKey: String): Boolean
    fun extendLock(lockKey: String, extensionSeconds: Int): Boolean
}

/**
 * 分布式锁配置。
 */
data class LockConfig(
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 50,
    val defaultTimeoutSeconds: Int = 5,
    val maxTimeoutSeconds: Int = 300
)

/**
 * [DistributedLock] 接口的 MySQL 实现。
 */
class MySQLDistributedLock(
    private val dataSource: DataSource,
    private val config: LockConfig = LockConfig()
) : DistributedLock {
    override fun tryLock(lockKey: String, timeoutSeconds: Int): Boolean {
        val actualTimeout = timeoutSeconds.coerceIn(1, config.maxTimeoutSeconds)

        repeat(config.maxRetries) { attempt ->
            try {
                dataSource.connection.use { connection ->
                    @Suppress("SqlNoDataSourceInspection")
                    connection.prepareStatement("SELECT GET_LOCK(?, ?)").use { stmt ->
                        stmt.setString(1, lockKey)
                        stmt.setInt(2, actualTimeout)

                        val result = stmt.executeQuery()
                        if (result.next()) {
                            return result.getInt(1) == 1
                        }
                    }
                }
            } catch (_: Exception) {
                Thread.sleep(config.retryDelayMs * (attempt + 1))
            }
        }
        return false
    }

    override fun unlock(lockKey: String): Boolean {
        return try {
            dataSource.connection.use { connection ->
                @Suppress("SqlNoDataSourceInspection")
                connection.prepareStatement("SELECT RELEASE_LOCK(?)").use { stmt ->
                    stmt.setString(1, lockKey)

                    val result = stmt.executeQuery()
                    if (result.next()) {
                        result.getInt(1) == 1
                    } else false
                }
            }
        } catch (_: Exception) {
            false
        }
    }

    override fun isLocked(lockKey: String): Boolean {
        return try {
            dataSource.connection.use { connection ->
                @Suppress("SqlNoDataSourceInspection")
                connection.prepareStatement("SELECT IS_USED_LOCK(?)").use { stmt ->
                    stmt.setString(1, lockKey)

                    val result = stmt.executeQuery()
                    if (result.next()) {
                        result.getLong(1) > 0
                    } else false
                }
            }
        } catch (_: Exception) {
            false
        }
    }

    override fun extendLock(lockKey: String, extensionSeconds: Int): Boolean {
        return if (isLocked(lockKey)) {
            unlock(lockKey) && tryLock(lockKey, extensionSeconds)
        } else {
            false
        }
    }
}
