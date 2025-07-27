package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import com.github.cpjinan.plugin.akarilevel.utils.FileUtils
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import taboolib.module.lang.registerLanguage
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.asLangTextOrNull
import taboolib.platform.util.sendLang
import java.util.*

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/6/21 19:40
 */
object DefaultAkariLevelLanguage : AkariLevelLanguage {
    override fun sendLang(sender: CommandSender, key: String, vararg args: Any) {
        sender.sendLang(key, *args)
    }

    override fun getLang(sender: CommandSender, key: String, vararg args: Any): String? {
        return sender.asLangTextOrNull(key, *args)
    }

    override fun getLangList(sender: CommandSender, key: String, vararg args: Any): List<String> {
        return sender.asLangTextList(key, *args)
    }

    override fun releaseResource() {
        fun getLocale(): String {
            val locale = Locale.getDefault()
            val language = locale.language.lowercase()
            val country = if (locale.country.isNotEmpty()) locale.country.uppercase() else language.uppercase()
            return "${language}_$country"
        }
    }

    override fun reload() {
        Language.reload()
    }

    @SubscribeEvent
    fun onPlayerSelectLocale(event: PlayerSelectLocaleEvent) {
        val lang = SettingsConfig.language
        val isLangExist = lang in Language.languageCode
        val langFile = FileUtils.getFileOrNull("lang/$lang.yml")

        if (langFile != null) {
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            event.locale = "zh_CN"
        }
    }

    @SubscribeEvent
    fun onSystemSelectLocale(event: SystemSelectLocaleEvent) {
        val lang = SettingsConfig.language
        val isLangExist = lang in Language.languageCode
        val langFile = FileUtils.getFileOrNull("lang/$lang.yml")

        if (langFile != null) {
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            event.locale = "zh_CN"
        }
    }

    @Awake(LifeCycle.CONST)
    fun onConst() {
        PlatformFactory.registerAPI<AkariLevelLanguage>(DefaultAkariLevelLanguage)
    }
}