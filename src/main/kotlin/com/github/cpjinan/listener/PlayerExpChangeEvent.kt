package com.github.cpjinan.listener

import com.github.cpjinan.manager.ConfigManager
import com.github.cpjinan.manager.LevelManager
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerExpChangeEvent {
    @SubscribeEvent
    fun onPlayerExpChange(event : PlayerExpChangeEvent) {
        event.amount = 0
        LevelManager.refreshPlayerLevel(event.player)
    }

}