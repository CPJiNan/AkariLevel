package com.github.cpjinan.plugin.akarilevel.database

import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * @author 季楠
 * @since 2025/7/27 18:29
 */
class DatabaseSQLite() : Database {
    override val type: DatabaseType = DatabaseType.SQLITE

    override val dataSource by lazy { DatabaseConfig.hostSQLite.createDataSource() }

    override val table = Table(DatabaseConfig.table, DatabaseConfig.hostSQLite) {
        add("key") {
            type(ColumnTypeSQLite.TEXT) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQLite.TEXT)
        }
    }

    init {
        table.createTable(dataSource)
    }

    override fun getKeys(): Set<String> {
        return table.select(dataSource) {
            rows("key")
        }.map {
            getString("key")
        }.toSet()
    }

    override fun getValues(): Map<String, Any?> {
        return table.select(dataSource) {
            rows("key", "value")
        }.map {
            getString("key") to getString("value")
        }.toMap()
    }

    override fun toMap(): Map<String, Any?> {
        return getValues()
    }

    override operator fun contains(path: String): Boolean {
        return table.select(dataSource) {
            rows("key")
            where("key" eq path)
            limit(1)
        }.find()
    }

    override fun isSet(path: String): Boolean {
        return contains(path)
    }

    override operator fun get(path: String): Any? {
        return get(path, null)
    }

    override operator fun get(path: String, def: Any?): Any? {
        return table.select(dataSource) {
            rows("key", "value")
            where("key" eq path)
            limit(1)
        }.firstOrNull {
            getString("value")
        } ?: def
    }

    override operator fun set(path: String, value: Any?) {
        if (value == null) {
            table.delete(dataSource) {
                where { "key" eq path }
            }
            return
        }
        if (contains(path)) table.update(dataSource) {
            set("value", value)
            where("key" eq path)
        } else {
            table.insert(dataSource, "key", "value") {
                value(path, value)
            }
        }
    }

    override fun save() {}

    override fun reload() {}

    override fun clear() {
        table.delete(dataSource) { }
    }
}