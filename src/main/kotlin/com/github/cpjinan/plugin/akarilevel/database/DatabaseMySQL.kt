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

    private val easyCache by lazy {
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

    private val distributedLock by lazy {
        MySQLDistributedLock(dataSource)
    }

    private var enableDistributedLock = false

    init {
        memberTable.createTable(dataSource)
        initializeEasyFeatures()
    }

    private fun initializeEasyFeatures() {
        try {
            enableDistributedLock = distributedLock.tryLock("test_lock", 1)
            if (enableDistributedLock) {
                distributedLock.unlock("test_lock")
            }

            warmUpCache()
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
        if (table == memberTable) {
            return if (enableDistributedLock) {
                withMemberDataLock(path) {
                    easyCache[path]
                }
            } else {
                easyCache[path]
            }
        }

        return getFromDatabase(table, path)
    }

    override fun set(table: Table<*, *>, path: String, value: String?) {
        if (table == memberTable) {
            if (enableDistributedLock) {
                withMemberDataLock(path) {
                    setMemberData(path, value)
                }
            } else {
                setMemberData(path, value)
            }
        } else {
            setToDatabase(table, path, value)
        }
    }

    private fun <T> withMemberDataLock(memberKey: String, operation: () -> T): T? {
        val lockKey = "member_data:$memberKey"
        return if (distributedLock.tryLock(lockKey, 5)) {
            try {
                operation()
            } finally {
                distributedLock.unlock(lockKey)
            }
        } else {
            null
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

    private fun setMemberData(path: String, value: String?) {
        if (value == null) {
            setToDatabase(memberTable, path, null)
            easyCache.invalidate(path)
        } else {
            setToDatabase(memberTable, path, value)
            easyCache[path] = value
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

    private fun warmUpCache() {
        val warmUpData = memberTable.select(dataSource) {
            rows("key", "value")
            limit(1000)
        }.map {
            getString("key") to (getString("value") ?: "")
        }.toMap()

        easyCache.setAll(warmUpData)
    }

    fun performCacheCleanup() {
        easyCache.cleanup()
    }

    fun shutdown() {
        // 关闭缓存资源。
        easyCache.cleanup()
    }
}