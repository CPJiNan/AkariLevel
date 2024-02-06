package com.github.cpjinan.plugin.akarilevel.utils

import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.utils.LoggerUtil.message
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin

/**
 * debug util
 * @author CPJiNan
 * @since 2024/01/15
 */
object DebugUtil {
    /**
     * print debug args
     * @param [args] name and value of args
     */
    fun printArgs(vararg args: Pair<String, Any?>) {
        if (!ConfigManager.isEnabledDebug()) return
        message(
            "&r===============[&c&lDebug&r]==============".colored(),
            "&r| &rPlugin &6${BukkitPlugin.getInstance().name} &7=>".colored(),
        )
        for ((name, value) in args) {
            message(
                "&r| &b◈ &r$name &7= &r$value".colored(),
            )
        }
        message(
            "&r| &a✓ &rPrint ${args.size} &rargs in total.".colored(),
            "&r===============[&c&lDebug&r]==============".colored()
        )
    }
}