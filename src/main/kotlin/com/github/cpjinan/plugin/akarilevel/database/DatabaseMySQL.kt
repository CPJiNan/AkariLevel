package com.github.cpjinan.plugin.akarilevel.database

import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * [Database] 接口的 MySQL 实现。
 *
 * @author 季楠
 * @since 2025/8/7 23:08
 */
class DatabaseMySQL() : Database {
    override val type = DatabaseType.MYSQL

    override val dataSource by lazy { DatabaseConfig.hostSQL.createDataSource() }

    override val table = Table(DatabaseConfig.table, DatabaseConfig.hostSQL) {
        add("member") {
            type(ColumnTypeSQL.TEXT) {
                options(ColumnOptionSQL.PRIMARY_KEY)
            }
        }
        add("data") {
            type(ColumnTypeSQL.JSON)
        }
    }

    init {
        table.createTable(dataSource)
    }

    override fun getKeys(): Set<String> {
        return table.select(dataSource) {
            rows("member")
        }.map {
            getString("member")
        }.toSet()
    }

    override fun getValues(): Map<String, String?> {
        return table.select(dataSource) {
            rows("member", "data")
        }.map {
            getString("member") to getString("data")
        }.toMap(ConcurrentHashMap())
    }

    override operator fun contains(path: String): Boolean {
        return table.select(dataSource) {
            rows("member")
            where("member" eq path)
            limit(1)
        }.find()
    }

    override operator fun get(path: String): String? {
        return table.select(dataSource) {
            rows("member", "data")
            where("member" eq path)
            limit(1)
        }.firstOrNull {
            getString("data")
        }
    }

    override operator fun set(path: String, value: String?) {
        if (value == null) {
            table.delete(dataSource) {
                where { "member" eq path }
            }
            return
        }
        if (contains(path)) table.update(dataSource) {
            set("data", value)
            where("member" eq path)
        } else {
            table.insert(dataSource, "member", "data") {
                value(path, value)
            }
        }
    }
}