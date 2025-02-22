package com.github.cpjinan.plugin.akarilevel.utils.core

import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.*

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.utils.core
 *
 * @author 季楠
 * @since 2025/1/22 18:56
 */
object LoggerUtil {
    /**
     * send message
     * @param [message] message
     */
    @JvmStatic
    fun message(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendMessage(i)
        }
    }

    /**
     * send info
     * @param [message] info
     */
    @JvmStatic
    fun info(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendInfo(i)
        }
    }

    /**
     * send warn
     * @param [message] warn
     */
    @JvmStatic
    fun warn(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendWarn(i)
        }
    }

    /**
     * send error
     * @param [message] error
     */
    @JvmStatic
    fun error(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendError(i)
        }
    }

    /**
     * send info message
     * @param [message] info message
     */
    @JvmStatic
    fun infoMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendInfoMessage(i)
        }
    }

    /**
     * send warn message
     * @param [message] warn message
     */
    @JvmStatic
    fun warnMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendWarnMessage(i)
        }
    }

    /**
     * send error
     * @param [message] error message
     */
    @JvmStatic
    fun errorMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendErrorMessage(i)
        }
    }

    @JvmStatic
    fun debug(vararg message: String) {
        if (PluginConfig.isEnabledDebug()) {
            message.forEach {
                BukkitPlugin.getInstance().server.consoleSender.sendMessage(it.colored())
            }
        }
    }
}