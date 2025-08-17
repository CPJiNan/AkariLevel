package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.manager.CacheManager.forcePersist
import com.github.cpjinan.plugin.akarilevel.manager.CacheManager.isDirty
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.listener
 *
 * 玩家监听器。
 *
 * @author 季楠
 * @since 2025/8/12 21:40
 */
object PlayerListener {
    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerName = event.player.name
        if (!isDirty(playerName)) return
        submit(async = true) {
            forcePersist(playerName)
        }
    }
}