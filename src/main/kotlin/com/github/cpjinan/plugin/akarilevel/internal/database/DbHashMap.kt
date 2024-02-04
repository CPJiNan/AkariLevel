package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData

object DbHashMap : Database {
    val playerData: HashMap<String, PlayerData> = hashMapOf()

    override fun getPlayerByName(name: String): PlayerData = playerData[name] ?: PlayerData()

    override fun updatePlayer(name: String, value: PlayerData) {
        playerData[name] = value
    }

    override fun save() {}
}