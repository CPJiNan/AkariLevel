package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.manager.SmartPersistenceManager
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

/**
 * @author QwQ-dev
 * @since 2025/8/12 16:55
 */
object PersistenceEventListener {
    fun initialize() {
        submitAsync(period = 20L * 60 * 5) {  // 20 ticks * 60 = 1min, * 5 = 5min
            SmartPersistenceManager.createCheckpoint()
        }
    }

    fun shutdown() {
        SmartPersistenceManager.onServerShutdown()
    }

    @SubscribeEvent
    @Suppress("unused")
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerName = event.player.name

        // Async
        SmartPersistenceManager.onPlayerQuit(playerName)
    }
}