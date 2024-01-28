package com.github.cpjinan.plugin.playerlevel.internal.database

import com.github.cpjinan.plugin.playerlevel.internal.database.types.Player
import com.github.cpjinan.plugin.playerlevel.internal.manager.ConfigManager
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table

class DbSql : Database {
    private val host = HostSQL(ConfigManager.config.getConfigurationSection("options.database.sql")!!)
    private val table = Table("playerlevel", host) {
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

    private val dataSource = host.createDataSource()

    override fun getPlayerByName(name: String): Player {
        return table.select(dataSource) {
            where { "player" eq name }
        }.firstOrNull {
            Player(
                this.getInt("level"),
                this.getInt("exp")
            )
        } ?: Player()
    }

    override fun updatePlayer(name: String, value: Player) {
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