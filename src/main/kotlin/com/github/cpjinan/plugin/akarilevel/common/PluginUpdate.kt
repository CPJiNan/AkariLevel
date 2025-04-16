package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil.message
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
                URL("https://raw.githubusercontent.com/CPJiNan/AkariLevel/refs/heads/master/src/main/kotlin/com/github/cpjinan/plugin/akarilevel/common/PluginNotice").openConnection() as HttpURLConnection
            try {
                val message = urlConnection.inputStream.bufferedReader().readText()
                if (message.isNotBlank()) message(message.colored())
            } catch (_: Exception) {
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }

    fun getPluginUpdate() {
        Thread {
            val urlConnection =
                URL("https://raw.githubusercontent.com/CPJiNan/AkariLevel/refs/heads/master/src/main/kotlin/com/github/cpjinan/plugin/akarilevel/common/PluginVersion").openConnection() as HttpURLConnection
            try {
                val latestVersion = urlConnection.inputStream.bufferedReader().readText()
                val currentVersion = BukkitPlugin.getInstance().description.version
                if (latestVersion != currentVersion) {
                    console().asLangTextList("Plugin-Update")
                        .replace(Pair("%latestVersion%", latestVersion), Pair("%currentVersion%", currentVersion))
                        .forEach {
                            message(it.colored())
                        }
                }
            } catch (_: Exception) {
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }

    fun sendPlayerUpdateNotify(player: Player) {
        Thread {
            val urlConnection =
                URL("https://raw.githubusercontent.com/CPJiNan/AkariLevel/refs/heads/master/src/main/kotlin/com/github/cpjinan/plugin/akarilevel/common/PluginVersion").openConnection() as HttpURLConnection
            try {
                val latestVersion = urlConnection.inputStream.bufferedReader().readText()
                val currentVersion = BukkitPlugin.getInstance().description.version
                if (latestVersion != currentVersion) {
                    player.asLangTextList("Plugin-Update")
                        .replace("%latestVersion%" to latestVersion, "%currentVersion%" to currentVersion)
                        .forEach {
                            player.sendMessage(it.colored())
                        }
                }
            } catch (_: Exception) {
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
                .replace("%latestVersion%" to latestVersion, "%currentVersion%" to currentVersion)
                .forEach {
                    message(it.colored())
                }
        }
    }
}