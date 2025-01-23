package com.github.cpjinan.plugin.akarilevel.util

import com.github.cpjinan.plugin.akarilevel.AkariLevelSettings
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.util
 *
 * @author 季楠
 * @since 2025/1/22 21:53
 */
object DebugUtils {
    fun debug(vararg message: String) {
        if (AkariLevelSettings.debug) {
            message.forEach {
                BukkitPlugin.getInstance().server.consoleSender.sendMessage(it.colored())
            }
        }
    }
}