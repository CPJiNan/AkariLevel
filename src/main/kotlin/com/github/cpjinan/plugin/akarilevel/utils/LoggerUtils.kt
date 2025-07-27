package com.github.cpjinan.plugin.akarilevel.utils

import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.*

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.utils
 *
 * @author 季楠
 * @since 2025/6/21 19:45
 */
object LoggerUtils {
    @JvmStatic
    fun message(vararg message: String) {
        message.forEach {
            BukkitPlugin.getInstance().server.consoleSender.sendMessage(it)
        }
    }

    @JvmStatic
    fun info(vararg message: String) {
        message.forEach {
            BukkitPlugin.getInstance().server.consoleSender.sendInfo(it)
        }
    }

    @JvmStatic
    fun warn(vararg message: String) {
        message.forEach {
            BukkitPlugin.getInstance().server.consoleSender.sendWarn(it)
        }
    }

    @JvmStatic
    fun error(vararg message: String) {
        message.forEach {
            BukkitPlugin.getInstance().server.consoleSender.sendError(it)
        }
    }

    @JvmStatic
    fun infoMessage(vararg message: String) {
        message.forEach {
            BukkitPlugin.getInstance().server.consoleSender.sendInfoMessage(it)
        }
    }

    @JvmStatic
    fun warnMessage(vararg message: String) {
        message.forEach {
            BukkitPlugin.getInstance().server.consoleSender.sendWarnMessage(it)
        }
    }

    @JvmStatic
    fun errorMessage(vararg message: String) {
        message.forEach {
            BukkitPlugin.getInstance().server.consoleSender.sendErrorMessage(it)
        }
    }

    @JvmStatic
    fun debug(vararg message: String) {
        if (SettingsConfig.debug) {
            message.forEach {
                BukkitPlugin.getInstance().server.consoleSender.sendMessage(it.colored())
            }
        }
    }
}