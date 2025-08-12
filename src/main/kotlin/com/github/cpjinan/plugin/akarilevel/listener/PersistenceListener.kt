package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.manager.PersistenceManager
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.listener
 *
 * @author QwQ-dev
 * @since 2025/8/12 16:55
 */
object PersistenceListener {
    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PersistenceManager.onPlayerQuit("Player:${event.player.name}")
    }

    fun initialize() {
        submitAsync(period = 20 * 60 * 5) {  // 20 ticks * 60 * 5 = 5 min
            PersistenceManager.createCheckpoint()
        }
    }

    fun shutdown() {
        PersistenceManager.onServerShutdown()
    }
}