package com.github.cpjinan.plugin.akarilevel.database

import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table

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
    override val type: DatabaseType = DatabaseType.MYSQL

    override val dataSource by lazy { DatabaseConfig.hostSQL.createDataSource() }

    override val table = Table(DatabaseConfig.table, DatabaseConfig.hostSQL) {
        add("player") {
            type(ColumnTypeSQL.TEXT) {
                options(ColumnOptionSQL.PRIMARY_KEY)
            }
        }
        add("data") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    init {
        table.createTable(dataSource)
    }

    override fun getKeys(): Set<String> {
        return table.select(dataSource) {
            rows("player")
        }.map {
            getString("player")
        }.toSet()
    }

    override fun getValues(): Map<String, String?> {
        return table.select(dataSource) {
            rows("player", "data")
        }.map {
            getString("player") to getString("data")
        }.toMap()
    }

    override fun toMap(): Map<String, String?> {
        return getValues()
    }

    override operator fun contains(path: String): Boolean {
        return table.select(dataSource) {
            rows("player")
            where("player" eq path)
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
            rows("player", "data")
            where("player" eq path)
            limit(1)
        }.firstOrNull {
            getString("data")
        } ?: def
    }

    override operator fun set(path: String, value: String?) {
        if (value == null) {
            table.delete(dataSource) {
                where { "player" eq path }
            }
            return
        }
        if (contains(path)) table.update(dataSource) {
            set("data", value)
            where("player" eq path)
        } else {
            table.insert(dataSource, "player", "data") {
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