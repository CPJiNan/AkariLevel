package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.internal.database.types.Player

interface Database {
    fun getPlayerByName(name: String): Player
    fun updatePlayer(name: String, value: Player)
    fun save()
}