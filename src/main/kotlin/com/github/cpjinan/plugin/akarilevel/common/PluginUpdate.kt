package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil.message
import com.github.cpjinan.plugin.akarilevel.utils.core.VersionUtil.toVersion
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.common5.util.replace
import taboolib.module.chat.colored
import taboolib.module.lang.asLangTextList
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.asLangTextList
import java.net.HttpURLConnection
import java.net.URL

object PluginUpdate {
    fun getPluginNotice() {
        Thread {
            val urlConnection =
                URL("https://cpjinan.github.io/Pages/AkariLevel/notice.html").openConnection() as HttpURLConnection
            try {
                val message = urlConnection.inputStream.bufferedReader().readText()
                if (message.isNotBlank()) message(message.colored())
            } catch (_: java.net.ConnectException) {
            } catch (_: java.net.SocketException) {
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }

    fun getPluginUpdate() {
        Thread {
            val urlConnection =
                URL("https://cpjinan.github.io/Pages/AkariLevel/version.html").openConnection() as HttpURLConnection
            try {
                val latestVersion = urlConnection.inputStream.bufferedReader().readText().toVersion()!!
                val currentVersion = BukkitPlugin.getInstance().description.version.toVersion()!!
                if (latestVersion > currentVersion) {
                    console().asLangTextList("Plugin-Update")
                        .replace(Pair("%latestVersion%", latestVersion), Pair("%currentVersion%", currentVersion))
                        .forEach {
                            message(it.colored())
                        }
                }
            } catch (_: java.net.ConnectException) {
            } catch (_: java.net.SocketException) {
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }

    fun sendPlayerUpdateNotify(player: Player) {
        Thread {
            val urlConnection =
                URL("https://cpjinan.github.io/Pages/AkariLevel/version.html").openConnection() as HttpURLConnection
            try {
                val latestVersion = urlConnection.inputStream.bufferedReader().readText().toVersion()!!
                val currentVersion = BukkitPlugin.getInstance().description.version.toVersion()!!
                if (latestVersion > currentVersion) {
                    player.asLangTextList("Plugin-Update")
                        .replace(Pair("%latestVersion%", latestVersion), Pair("%currentVersion%", currentVersion))
                        .forEach {
                            player.sendMessage(it.colored())
                        }
                }
            } catch (_: java.net.ConnectException) {
            } catch (_: java.net.SocketException) {
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }

    fun getConfigUpdate() {
        val latestVersion = PluginConfig.VERSION
        val currentVersion = PluginConfig.getConfigVersion()
        if (currentVersion < latestVersion) {
            console().asLangTextList("Config-Update")
                .replace(Pair("%latestVersion%", latestVersion), Pair("%currentVersion%", currentVersion))
                .forEach {
                    message(it.colored())
                }
        }
    }
}