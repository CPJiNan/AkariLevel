package com.github.cpjinan.plugin.akarilevel.utils

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.utils.LoggerUtil.message
import org.bukkit.plugin.Plugin
import taboolib.module.chat.colored

/**
 * debug util
 * @author CPJiNan
 * @since 2024/01/15
 */
object DebugUtil {
    fun printLogo() {
        message(
            AkariLevel.instance,
            "&b     _    _              _ _                   _  ".colored(),
            "&b    / \\  | | ____ _ _ __(_) |    _____   _____| | ".colored(),
            "&b   / _ \\ | |/ / _` | '__| | |   / _ \\ \\ / / _ \\ | ".colored(),
            "&b  / ___ \\|   < (_| | |  | | |__|  __/\\ V /  __/ | ".colored(),
            "&b /_/   \\_\\_|\\_\\__,_|_|  |_|_____\\___| \\_/ \\___|_| ".colored()
        )
    }

    /**
     * print debug args
     * @param [plugin] plugin instance
     * @param [args] name and value of args
     */
    fun printArgs(plugin: Plugin, vararg args: Pair<String, Any?>) {
        LoggerUtil.message(
            plugin,
            "&r===============[&c&lDebug&r]==============".colored(),
            "&r| &rPlugin &6${plugin.name} &7=>".colored(),
        )
        for ((name, value) in args) {
            LoggerUtil.message(
                plugin,
                "&r| &b◈ &r$name &7= &r$value".colored(),
            )
        }
        LoggerUtil.message(
            plugin,
            "&r| &a✓ &rPrint ${args.size} &rargs in total.".colored(),
            "&r===============[&c&lDebug&8]==============".colored()
        )
    }

    /**
     * print debug info
     * @param [plugin] plugin instance
     * @param [info] information
     */
    fun printInfo(plugin: Plugin, vararg info: String) {
        LoggerUtil.message(
            plugin,
            "&r===============[&c&lDebug&r]==============".colored(),
            "&r| &rPlugin &6${plugin.name} &7=>".colored(),
        )
        for (i in info) {
            LoggerUtil.message(
                plugin,
                "&r| &b◈ &r$i".colored(),
            )
        }
        LoggerUtil.message(
            plugin,
            "&r| &a✓ &rPrint ${info.size} &rlines of information in total.".colored(),
            "&r===============[&c&lDebug&8]==============".colored()
        )
    }
}