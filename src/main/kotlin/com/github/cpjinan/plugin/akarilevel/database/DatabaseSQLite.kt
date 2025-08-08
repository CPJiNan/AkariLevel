package com.github.cpjinan.plugin.akarilevel.database

import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * [Database] 接口的 SQLite 实现。
 *
 * @author 季楠
 * @since 2025/8/7 23:08
 */
class DatabaseSQLite() : Database {
    override val type = DatabaseType.SQLITE

    override val dataSource by lazy { DatabaseConfig.hostSQLite.createDataSource() }

    override val table = Table(DatabaseConfig.table, DatabaseConfig.hostSQLite) {
        add("member") {
            type(ColumnTypeSQLite.TEXT) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }
        add("data") {
            type(ColumnTypeSQLite.TEXT)
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
        }.toMap()
    }

    override fun toMap(): Map<String, String?> {
        return getValues()
    }

    override operator fun contains(path: String): Boolean {
        return table.select(dataSource) {
            rows("member")
            where("member" eq path)
            limit(1)
        }.find()
    }

    override fun isSet(path: String): Boolean {
        return contains(path)
    }

    override operator fun get(path: String): String? {
        return get(path, null)
    }

    override operator fun get(path: String, def: String?): String? {
        return table.select(dataSource) {
            rows("member", "data")
            where("member" eq path)
            limit(1)
        }.firstOrNull {
            getString("data")
        } ?: def
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

    override fun clear() {
        table.delete(dataSource) { }
    }
}