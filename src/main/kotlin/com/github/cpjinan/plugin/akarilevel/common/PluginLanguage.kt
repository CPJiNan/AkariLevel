package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil.debug
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import taboolib.module.lang.registerLanguage
import java.io.File
import java.util.*

object PluginLanguage {
    fun saveDefaultResource() {
        fun getLocale(): String {
            val locale = Locale.getDefault()
            val language = locale.language.lowercase()
            val country = if (locale.country.isNotEmpty()) locale.country.uppercase() else language.uppercase()
            return "${language}_$country"
        }
        plugin.saveDefaultResource(
            "level/Example_${
                if (FileUtil.getFileOrNull("lang/${getLocale()}.yml") != null) getLocale()
                else "en_US"
            }.yml",
            File(getDataFolder(), "level/Example.yml")
        )
        plugin.saveDefaultResource(
            "settings_${
                if (FileUtil.getFileOrNull("lang/${getLocale()}.yml") != null) getLocale()
                else "en_US"
            }.yml",
            File(getDataFolder(), "settings.yml")
        )
    }

    @SubscribeEvent
    fun onPlayerSelectLocale(event: PlayerSelectLocaleEvent) {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#5 &8| &6触发 SystemSelectLocaleEvent 事件，正在展示处理逻辑。",
        )

        val start = System.currentTimeMillis()
        val lang = PluginConfig.getLanguage()
        val isLangExist = lang in Language.languageCode
        val langFile = FileUtil.getFileOrNull("lang/$lang.yml")

        if (langFile != null) {
            debug("&r| &b◈ &r#5 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，且已在 lang 文件夹中发现，正在应用。" else "不存在于语言代码列表中，但已在 lang 文件夹中发现，正在注册并应用。"}")
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            debug("&r| &b◈ &r#5 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，但" else "不存在于语言代码列表中，且"}未在 lang 文件夹中发现，正在应用默认语言。")
            event.locale = "en_US"
        }

        debug(
            "&r| &a◈ &r#5 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    @SubscribeEvent
    fun onSystemSelectLocale(event: SystemSelectLocaleEvent) {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#6 &8| &6触发 SystemSelectLocaleEvent 事件，正在展示处理逻辑。",
        )

        val start = System.currentTimeMillis()
        val lang = PluginConfig.getLanguage()
        val isLangExist = lang in Language.languageCode
        val langFile = FileUtil.getFileOrNull("lang/$lang.yml")

        if (langFile != null) {
            debug("&r| &b◈ &r#6 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，且已在 lang 文件夹中发现，正在应用。" else "不存在于语言代码列表中，但已在 lang 文件夹中发现，正在注册并应用。"}")
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            debug("&r| &b◈ &r#6 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，但" else "不存在于语言代码列表中，且"}未在 lang 文件夹中发现，正在应用默认语言。")
            event.locale = "en_US"
        }

        debug(
            "&r| &a◈ &r#6 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }
}