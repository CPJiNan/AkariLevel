package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.api.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.api.event.level.PlayerLevelChangeEvent
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

object AkariLevelAPI {
    // region function
    fun getPlayerLevel(player: Player): Int = getLevel(player)

    fun getPlayerExp(player: Player): Int = getExp(player)

    // region basic function
    private fun getLevel(player: Player): Int {
        return DatabaseManager.getDatabase().getPlayerByName(player.name).level
    }

    private fun getExp(player: Player): Int {
        return DatabaseManager.getDatabase().getPlayerByName(player.name).exp
    }

    private fun setLevel(player: Player, level: Int, source: String) {
        callEvent(PlayerLevelChangeEvent(player, level, source)) {
            val db = DatabaseManager.getDatabase()
            val data = db.getPlayerByName(player.name)
            data.level = this.level
            db.updatePlayer(player.name, data)
        }
    }

    private fun setExp(player: Player, exp: Int, source: String) {
        callEvent(PlayerExpChangeEvent(player, exp, source)) {
            val db = DatabaseManager.getDatabase()
            val data = db.getPlayerByName(player.name)
            data.exp = this.exp
            db.updatePlayer(player.name, data)
        }
    }

    private inline fun <reified T : BukkitProxyEvent> callEvent(event: T, action: T.() -> Unit) {
        event.call()
        if (event.isCancelled) return
        action(event)
    }
}