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

    override val memberTable = Table("${DatabaseConfig.table}_Member", DatabaseConfig.hostSQLite) {
        add("player_uuid") {
            type(ColumnTypeSQLite.TEXT) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQLite.TEXT)
        }
    }

    init {
        memberTable.createTable(dataSource)
    }

    override fun contains(table: Table<*, *>, path: String): Boolean {
        return table.select(dataSource) {
            rows("player_uuid")
            where("player_uuid" eq path)
            limit(1)
        }.find()
    }

    override fun get(table: Table<*, *>, path: String): String? {
        return table.select(dataSource) {
            rows("player_uuid", "value")
            where("player_uuid" eq path)
            limit(1)
        }.firstOrNull {
            getString("value")
        }
    }

    override fun set(table: Table<*, *>, path: String, value: String?) {
        if (value == null) {
            table.delete(dataSource) {
                where { "player_uuid" eq path }
            }
            return
        }
        if (contains(table, path)) table.update(dataSource) {
            set("value", value)
            where("player_uuid" eq path)
        } else {
            table.insert(dataSource, "player_uuid", "value") {
                value(path, value)
            }
        }
    }
}