package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.manager.PersistenceManager.forcePersist
import com.github.cpjinan.plugin.akarilevel.manager.PersistenceManager.isDirty
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
        val member = "player:${event.player.name}"
        if (!isDirty(member)) return
        submit(async = true) {
            forcePersist(member)
        }
    }
}