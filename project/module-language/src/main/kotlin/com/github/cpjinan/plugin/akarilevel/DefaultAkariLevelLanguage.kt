package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.util.DebugUtils.debug
import com.github.cpjinan.plugin.akarilevel.util.FileUtils
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.*
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author Adyeshach, 季楠
 * @since 2025/1/25 16:05
 */
object DefaultAkariLevelLanguage : AkariLevelLanguage {
    override fun sendLang(sender: ProxyCommandSender, key: String, vararg args: Any) {
        sender.sendLang(key, *args)
    }

    override fun getLang(sender: ProxyCommandSender, key: String, vararg args: Any): String? {
        return sender.asLangTextOrNull(key, *args)
    }

    override fun getLangList(sender: ProxyCommandSender, key: String, vararg args: Any): List<String> {
        return sender.asLangTextList(key, *args)
    }

    @SubscribeEvent
    fun onPlayerSelectLocale(event: PlayerSelectLocaleEvent) {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#1 &8| &6触发 SystemSelectLocaleEvent 事件，正在展示处理逻辑。",
        )

        val start = System.currentTimeMillis()
        val lang = AkariLevelSettings.language
        val isLangExist = lang in Language.languageCode
        val langFile = FileUtils.getFileOrNull("lang/$lang.yml")

        if (langFile != null) {
            debug("&r| &b◈ &r#1 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，且已在 lang 文件夹中发现，正在应用。" else "不存在于语言代码列表中，但已在 lang 文件夹中发现，正在注册并应用。"}")
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            debug("&r| &b◈ &r#1 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，但" else "不存在于语言代码列表中，且"}未在 lang 文件夹中发现，正在应用默认语言。")
        }

        debug(
            "&r| &a◈ &r#1 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    @SubscribeEvent
    fun onSystemSelectLocale(event: SystemSelectLocaleEvent) {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#2 &8| &6触发 SystemSelectLocaleEvent 事件，正在展示处理逻辑。",
        )

        val start = System.currentTimeMillis()
        val lang = AkariLevelSettings.language
        val isLangExist = lang in Language.languageCode
        val langFile = FileUtils.getFileOrNull("lang/$lang.yml")

        if (langFile != null) {
            debug("&r| &b◈ &r#2 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，且已在 lang 文件夹中发现，正在应用。" else "不存在于语言代码列表中，但已在 lang 文件夹中发现，正在注册并应用。"}")
            if (!isLangExist) {
                registerLanguage(lang)
            }
            event.locale = lang
        } else {
            debug("&r| &b◈ &r#2 配置文件设置的语言为 $lang，${if (isLangExist) "存在于语言代码列表中，但" else "不存在于语言代码列表中，且"}未在 lang 文件夹中发现，正在应用默认语言。")
        }

        debug(
            "&r| &a◈ &r#2 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    @Awake(LifeCycle.CONST)
    fun onConst() {
        // 注册服务
        PlatformFactory.registerAPI<AkariLevelLanguage>(DefaultAkariLevelLanguage)
    }
}