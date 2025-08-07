package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import taboolib.module.lang.registerLanguage
import taboolib.platform.util.bukkitPlugin
import java.io.File

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
     * 重载语言文件。
     */
    fun reload() {
        Language.reload()
    }

    /**
     * 玩家获取语言文本事件。
     */
    @SubscribeEvent
    fun onPlayerSelectLocale(event: PlayerSelectLocaleEvent) {
        val lang = SettingsConfig.language
        val isLangExist = lang in Language.languageCode
        val langFile = File(bukkitPlugin.dataFolder, "lang${File.separator}$lang.yml")

        // 从配置文件获取语言选项。
        if (langFile.exists()) {
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            // 如果指定语言不存在，默认使用 zh_CN 。
            event.locale = "zh_CN"
        }
    }

    /**
     * 系统获取语言文本事件。
     */
    @SubscribeEvent
    fun onSystemSelectLocale(event: SystemSelectLocaleEvent) {
        val lang = SettingsConfig.language
        val isLangExist = lang in Language.languageCode
        val langFile = File(bukkitPlugin.dataFolder, "lang${File.separator}$lang.yml")

        // 从配置文件获取语言选项。
        if (langFile.exists()) {
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            // 如果指定语言不存在，默认使用 zh_CN 。
            event.locale = "zh_CN"
        }
    }
}