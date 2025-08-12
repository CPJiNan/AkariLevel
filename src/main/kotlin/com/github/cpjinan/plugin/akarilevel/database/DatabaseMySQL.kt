package com.github.cpjinan.plugin.akarilevel.database

import com.github.cpjinan.plugin.akarilevel.cache.CircuitBreakerConfig
import com.github.cpjinan.plugin.akarilevel.cache.EasyCache
import com.github.cpjinan.plugin.akarilevel.cache.MySQLDistributedLock
import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * [Database] 接口的 MySQL 实现。
 *
 * @author 季楠, QwQ-dev
 * @since 2025/8/7 23:08
 */
class DatabaseMySQL() : Database {
    override val type = DatabaseType.MYSQL

    override val dataSource by lazy { DatabaseConfig.hostSQL.createDataSource() }

    private val distributedLock by lazy { MySQLDistributedLock(dataSource) }

    private var enableDistributedLock = false

    override val memberTable = Table("${DatabaseConfig.table}_Member", DatabaseConfig.hostSQL) {
        add("key") {
            type(ColumnTypeSQL.TEXT) {
                options(ColumnOptionSQL.PRIMARY_KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQL.JSON)
        }
    }

    private val memberCache by lazy {
        EasyCache.builder<String, String>()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .expireAfterAccess(Duration.ofMinutes(10))
            .circuitBreaker(
                CircuitBreakerConfig(
                    failureThreshold = 10,
                    timeoutMs = 30_000,
                    sampleSize = 20
                )
            )
            .loader { key ->
                getFromDatabase(memberTable, key)
            }
            .build()
    }

    init {
        memberTable.createTable(dataSource)

        try {
            enableDistributedLock = distributedLock.tryLock("test", 1)
            if (enableDistributedLock) distributedLock.unlock("test")
            warmUpMemberCache()
        } catch (_: Exception) {
            enableDistributedLock = false
        }
    }

    override fun getKeys(table: Table<*, *>): Set<String> {
        return table.select(dataSource) {
            rows("key")
        }.map {
            getString("key")
        }.toSet()
    }

    override fun getValues(table: Table<*, *>): Map<String, String?> {
        return table.select(dataSource) {
            rows("key", "value")
        }.map {
            getString("key") to getString("value")
        }.toMap(ConcurrentHashMap())
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
            memberTable -> {
                if (enableDistributedLock) withMemberDataLock(path) { memberCache[path] }
                else memberCache[path]
            }

            else -> getFromDatabase(table, path)
        }
    }

    override fun set(table: Table<*, *>, path: String, value: String?) {
        when (table) {
            memberTable -> {
                if (enableDistributedLock) withMemberDataLock(path) { setMemberData(path, value) }
                else setMemberData(path, value)
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

    private fun setToDatabase(table: Table<*, *>, path: String, value: String?) {
        if (value == null) {
            table.delete(dataSource) {
                where { "key" eq path }
            }
            return
        }
        if (contains(table, path)) {
            table.update(dataSource) {
                set("value", value)
                where("key" eq path)
            }
        } else {
            table.insert(dataSource, "key", "value") {
                value(path, value)
            }
        }
    }

    private fun setMemberData(path: String, value: String?) {
        if (value != null) {
            setToDatabase(memberTable, path, value)
            memberCache[path] = value
        } else {
            setToDatabase(memberTable, path, null)
            memberCache.invalidate(path)
        }
    }

    private fun <T> withMemberDataLock(member: String, block: () -> T): T? {
        val lockKey = "member:$member"
        return if (distributedLock.tryLock(lockKey, 5)) {
            try {
                block()
            } finally {
                distributedLock.unlock(lockKey)
            }
        } else null
    }

    private fun warmUpMemberCache() {
        val warmUpData = memberTable.select(dataSource) {
            rows("key", "value")
            limit(1000)
        }.map {
            getString("key") to (getString("value") ?: "")
        }.toMap()

        memberCache.setAll(warmUpData)
    }
}