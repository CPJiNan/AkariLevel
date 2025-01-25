package com.github.cpjinan.plugin.akarilevel

import taboolib.common.platform.ProxyCommandSender

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author Adyeshach, 季楠
 * @since 2025/1/22 22:23
 */
interface AkariLevelLanguage {
    /** 发送语言文本 **/
    fun sendLang(sender: ProxyCommandSender, key: String, vararg args: Any)

    /** 获取语言文本 **/
    fun getLang(sender: ProxyCommandSender, key: String, vararg args: Any): String?

    /** 获取语言文本 **/
    fun getLangList(sender: ProxyCommandSender, key: String, vararg args: Any): List<String>
}