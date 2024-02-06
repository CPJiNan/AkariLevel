package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table

class DbSql : Database {
    private val host = ConfigManager.getSqlHost()
    private val dataSource by lazy { host.createDataSource() }
    private val table = Table(ConfigManager.getSqlTable(), host) {
        add { id() }
        add("player") {
            type(ColumnTypeSQL.VARCHAR, 255) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("level") {
            type(ColumnTypeSQL.INT)
        }
        add("exp") {
            type(ColumnTypeSQL.INT)
        }
    }

    init {
        table.createTable(dataSource)
    }

    override fun getPlayerByName(name: String): PlayerData {
        return get(name)
    }

    override fun updatePlayer(name: String, value: PlayerData) {
        set(name, value.level, value.exp)
    }

    override fun save() {}

    private fun add(player: String, level: Int, exp: Int) {
        table.insert(dataSource, "player", "level", "exp") {
            value(player, level, exp)
        }
    }

    private fun delete(player: String) {
        table.delete(dataSource) {
            where { "player" eq player }
        }
    }

    fun set(player: String, level: Int, exp: Int) {
        if (have(player)) table.update(dataSource) {
            set("level", level)
            set("exp", exp)
            where("player" eq player)
        } else add(player, level, exp)
    }

    fun get(player: String): PlayerData {
        return table.select(dataSource) {
            where("player" eq player)
            limit(1)
        }.firstOrNull {
            PlayerData(
                this.getInt("level"),
                this.getInt("exp")
            )
        } ?: PlayerData()
    }

    fun have(player: String): Boolean {
        return table.select(dataSource) {
            where("player" eq player)
            limit(1)
        }.firstOrNull {
            true
        } ?: false
    }
}