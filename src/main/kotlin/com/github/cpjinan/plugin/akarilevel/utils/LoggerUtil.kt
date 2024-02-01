package com.github.cpjinan.plugin.akarilevel.utils

import taboolib.platform.BukkitPlugin
import taboolib.platform.util.*

/**
 * logger util
 * @author CPJiNan
 * @since 2024/01/15
 */
object LoggerUtil {
    /**
     * send message
     * @param [plugin] plugin instance
     * @param [message] message
     */
    fun message(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendMessage(i)
        }
    }

    /**
     * send info
     * @param [plugin] plugin instance
     * @param [message] info
     */
    fun info(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendInfo(i)
        }
    }

    /**
     * send warn
     * @param [plugin] plugin instance
     * @param [message] warn
     */
    fun warn(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendWarn(i)
        }
    }

    /**
     * send error
     * @param [plugin] plugin instance
     * @param [message] error
     */
    fun error(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendError(i)
        }
    }

    /**
     * send info message
     * @param [plugin] plugin instance
     * @param [message] info message
     */
    fun infoMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendInfoMessage(i)
        }
    }

    /**
     * send warn message
     * @param [plugin] plugin instance
     * @param [message] warn message
     */
    fun warnMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendWarnMessage(i)
        }
    }

    /**
     * send error
     * @param [plugin] plugin instance
     * @param [message] error message
     */
    fun errorMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendErrorMessage(i)
        }
    }
}