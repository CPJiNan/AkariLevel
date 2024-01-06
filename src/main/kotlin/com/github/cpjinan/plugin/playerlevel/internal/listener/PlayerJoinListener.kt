package com.github.cpjinan.plugin.playerlevel.internal.listener

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerJoinListener {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        LevelAPI.refreshPlayerLevel(event.player, "PLAYER_JOIN_LISTENER")
    }
}