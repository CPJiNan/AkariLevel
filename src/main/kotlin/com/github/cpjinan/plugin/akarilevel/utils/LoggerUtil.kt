package com.github.cpjinan.plugin.akarilevel.utils

import org.bukkit.plugin.Plugin
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
    fun message(plugin: Plugin, vararg message: String) {
        for (i in message) {
            plugin.server.consoleSender.sendMessage(i)
        }
    }

    /**
     * send info
     * @param [plugin] plugin instance
     * @param [message] info
     */
    fun info(plugin: Plugin, vararg message: String) {
        for (i in message) {
            plugin.server.consoleSender.sendInfo(i)
        }
    }

    /**
     * send warn
     * @param [plugin] plugin instance
     * @param [message] warn
     */
    fun warn(plugin: Plugin, vararg message: String) {
        for (i in message) {
            plugin.server.consoleSender.sendWarn(i)
        }
    }

    /**
     * send error
     * @param [plugin] plugin instance
     * @param [message] error
     */
    fun error(plugin: Plugin, vararg message: String) {
        for (i in message) {
            plugin.server.consoleSender.sendError(i)
        }
    }

    /**
     * send info message
     * @param [plugin] plugin instance
     * @param [message] info message
     */
    fun infoMessage(plugin: Plugin, vararg message: String) {
        for (i in message) {
            plugin.server.consoleSender.sendInfoMessage(i)
        }
    }

    /**
     * send warn message
     * @param [plugin] plugin instance
     * @param [message] warn message
     */
    fun warnMessage(plugin: Plugin, vararg message: String) {
        for (i in message) {
            plugin.server.consoleSender.sendWarnMessage(i)
        }
    }

    /**
     * send error
     * @param [plugin] plugin instance
     * @param [message] error message
     */
    fun errorMessage(plugin: Plugin, vararg message: String) {
        for (i in message) {
            plugin.server.consoleSender.sendErrorMessage(i)
        }
    }
}