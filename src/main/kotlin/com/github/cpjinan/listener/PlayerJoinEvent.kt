package com.github.cpjinan.listener

import com.github.cpjinan.manager.ConfigManager
import com.github.cpjinan.manager.LevelManager
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerJoinEvent {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        LevelManager.refreshPlayerLevel(event.player)
    }
}