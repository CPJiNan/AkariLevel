package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData

interface Database {
    fun getPlayerByName(name: String): PlayerData
    fun updatePlayer(name: String, value: PlayerData)
    fun save()
}