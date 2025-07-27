package com.github.cpjinan.plugin.akarilevel

import org.bukkit.command.CommandSender

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/6/21 19:39
 */
interface AkariLevelLanguage {
    /** 发送语言文本 **/
    fun sendLang(sender: CommandSender, key: String, vararg args: Any)

    /** 获取语言文本 **/
    fun getLang(sender: CommandSender, key: String, vararg args: Any): String?

    /** 获取语言文本 **/
    fun getLangList(sender: CommandSender, key: String, vararg args: Any): List<String>

    /** 释放 i18n 资源 **/
    fun releaseResource()

    /** 重载语言文件 **/
    fun reload()
}