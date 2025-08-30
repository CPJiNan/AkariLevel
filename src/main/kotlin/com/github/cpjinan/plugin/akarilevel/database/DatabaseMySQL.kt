package com.github.cpjinan.plugin.akarilevel.database

import com.github.cpjinan.plugin.akarilevel.cache.MySQLDistributedLock
import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig
import org.bukkit.Bukkit
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.chat.colored
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.lang.asLangText
import java.sql.SQLException

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * [Database] 接口的 MySQL 实现。
 *
 * @author 季楠, QwQ-dev
 * @since 2025/8/7 23:08
 */
@Suppress("DEPRECATION", "SqlNoDataSourceInspection", "SqlSourceToSinkFlow")
class DatabaseMySQL() : Database {
    override val type = DatabaseType.MYSQL

    override val dataSource by lazy { DatabaseConfig.hostSQL.createDataSource() }

    private val distributedLock by lazy { MySQLDistributedLock(dataSource) }

    private var enableDistributedLock = false

    override val memberTable = Table("${DatabaseConfig.table}_Member", DatabaseConfig.hostSQL) {
        add("key") {
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.PRIMARY_KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    init {
        memberTable.createTable(dataSource)

        try {
            enableDistributedLock = distributedLock.tryLock("test", 1)
            if (enableDistributedLock) distributedLock.unlock("test")
        } catch (e: Exception) {
            e.printStackTrace()
            enableDistributedLock = false
        }
    }

    override fun contains(table: Table<*, *>, path: String): Boolean {
        return table.select(dataSource) {
            rows("key")
            where("key" eq path)
            limit(1)
        }.find()
    }

    override fun get(table: Table<*, *>, path: String): String? {
        return when (table) {
            memberTable -> getFromDatabaseWithLock(memberTable, path)
            else -> getFromDatabase(table, path)
        }
    }

    override fun set(table: Table<*, *>, path: String, value: String?) {
        when (table) {
            memberTable -> {
                val block = {
                    setToDatabase(memberTable, path, value)
                }

                if (enableDistributedLock) {
                    var retryCount = 0
                    val maxRetries = 3
                    while (retryCount < maxRetries) {
                        val result = withLock("member:$path") {
                            block()
                            true
                        }
                        if (result != null) break
                        retryCount++
                        if (retryCount < maxRetries) Thread.sleep(100L * retryCount)
                    }
                    if (retryCount >= maxRetries) block()
                } else block()
            }

            else -> setToDatabase(table, path, value)
        }
    }

    private fun getFromDatabase(table: Table<*, *>, path: String): String? {
        return table.select(dataSource) {
            rows("key", "value")
            where("key" eq path)
            limit(1)
        }.firstOrNull {
            getString("value")
        }
    }

    private fun getFromDatabaseWithLock(table: Table<*, *>, path: String): String? {
        // 未启用分布式锁时，直接读取。
        if (!enableDistributedLock) {
            return getFromDatabase(table, path)
        }

        return try {
            dataSource.connection.use { connection ->
                connection.prepareStatement("SET innodb_lock_wait_timeout = 2").execute()

                val sql = "SELECT `value` FROM `${table.name}` WHERE `key` = ? LOCK IN SHARE MODE"
                connection.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, path)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        rs.getString("value")
                    } else {
                        null
                    }
                }
            }
        } catch (e: SQLException) {
            // 在严重锁竞争或数据不一致时踢出玩家以保护数据完整性。
            if (e.message?.contains("Lock wait timeout") == true) {
                submit {
                    val offlinePlayer = Bukkit.getOfflinePlayer(path)
                    if (offlinePlayer.isOnline) {
                        offlinePlayer.player.kickPlayer(console().asLangText("PlayerLoadDataTimeout").colored())
                    }
                }
                null
            } else {
                // 回退普通读取。
                e.printStackTrace()
                getFromDatabase(table, path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getFromDatabase(table, path)
        }
    }

    private fun setToDatabase(table: Table<*, *>, path: String, value: String?) {
        if (value == null) {
            table.delete(dataSource) {
                where { "key" eq path }
            }
            return
        }

        dataSource.connection.use { connection ->
            connection.autoCommit = false
            try {
                val updateSql = "UPDATE `${table.name}` SET `value` = ? WHERE `key` = ?"
                val updateCount = connection.prepareStatement(updateSql).use { stmt ->
                    stmt.setString(1, value)
                    stmt.setString(2, path)
                    stmt.executeUpdate()
                }

                if (updateCount == 0) {
                    val insertSql = "INSERT INTO `${table.name}` (`key`, `value`) VALUES (?, ?)"
                    connection.prepareStatement(insertSql).use { stmt ->
                        stmt.setString(1, path)
                        stmt.setString(2, value)
                        stmt.executeUpdate()
                    }
                }

                connection.commit()
            } catch (e: Exception) {
                connection.rollback()
                throw e
            }
        }
    }

    private fun <T> withLock(lockKey: String, block: () -> T): T? {
        return if (distributedLock.tryLock(lockKey, 5)) {
            try {
                block()
            } finally {
                distributedLock.unlock(lockKey)
            }
        } else {
            val playerName = lockKey.removePrefix("member:")
            submit {
                val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
                if (offlinePlayer.isOnline) {
                    offlinePlayer.player.kickPlayer(console().asLangText("PlayerLoadDataTimeout").colored())
                }
            }
            null
        }
    }
}