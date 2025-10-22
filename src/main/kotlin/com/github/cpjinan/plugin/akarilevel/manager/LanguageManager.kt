package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.manager
 *
 * 语言管理器。
 *
 * @author 季楠
 * @since 2025/8/7 21:50
 */
object LanguageManager {
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