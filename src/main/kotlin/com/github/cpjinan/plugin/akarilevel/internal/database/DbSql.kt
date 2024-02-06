package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table

class DbSql : Database {
    private val host = HostSQL(ConfigManager.getSqlSection())
    private val table = Table(ConfigManager.getSqlSection().getString("table")!!, host) {
        add("player") {
            type(ColumnTypeSQL.VARCHAR, 255) {
                options(ColumnOptionSQL.PRIMARY_KEY)
            }
        }
        add("level") {
            type(ColumnTypeSQL.INT)
        }
        add("exp") {
            type(ColumnTypeSQL.INT)
        }
    }

    private val dataSource by lazy { host.createDataSource() }

    override fun getPlayerByName(name: String): PlayerData {
        return table.select(dataSource) {
            where { "player" eq name }
        }.firstOrNull {
            PlayerData(
                this.getInt("level"),
                this.getInt("exp")
            )
        } ?: PlayerData()
    }

    override fun updatePlayer(name: String, value: PlayerData) {
        if (!table.find(dataSource) { where { "player" eq name } }) {
            table.insert(
                dataSource,
                "player",
                "level",
                "exp"
            ) {
                value(name, 0, 0)
            }
        }
        table.update(dataSource) {
            where { "player" eq name }
            set("level", value.level)
            set("exp", value.exp)
        }
    }

    override fun save() {}

    init {
        table.workspace(dataSource) { createTable(true) }.run()
    }

    companion object {
        val INSTANCE = DbSql()
    }
}