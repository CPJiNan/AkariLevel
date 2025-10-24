package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache
import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.listener
 *
 * 玩家监听器。
 *
 * @author 季楠, QwQ-dev
 * @since 2025/8/12 21:40
 */
object PlayerListener {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val playerName = event.player.name
        MemberCache.memberCache.invalidate(playerName)
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerName = event.player.name
        MemberCache.memberCache.invalidate(playerName)
    }

    /**
     * 玩家获取语言文本事件。
     */
    @SubscribeEvent
    fun onPlayerSelectLocale(event: PlayerSelectLocaleEvent) {
        event.locale = SettingsConfig.language
    }

    /**
     * 系统获取语言文本事件。
     */
    @SubscribeEvent
    fun onSystemSelectLocale(event: SystemSelectLocaleEvent) {
        event.locale = SettingsConfig.language
    }
}