package com.github.cpjinan.plugin.akarilevel.utils

import com.github.cpjinan.plugin.akarilevel.utils.LoggerUtil.message
import com.github.cpjinan.plugin.akarilevel.utils.VersionUtil.toSemanticVersion
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin
import java.net.HttpURLConnection
import java.net.URL

object UpdateUtil {
    fun getPluginNotice() {
        Thread {
            val urlConnection =
                URL("https://cpjinan.github.io/Pages/AkariLevel/notice.html").openConnection() as HttpURLConnection
            try {
                val message = urlConnection.inputStream.bufferedReader().readText()
                if (message.isNotBlank()) message(message.colored())
            } catch (_: java.net.ConnectException) {
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
                val latestVersion = urlConnection.inputStream.bufferedReader().readText().toSemanticVersion()!!
                val currentVersion = BukkitPlugin.getInstance().description.version.toSemanticVersion()!!
                if (latestVersion > currentVersion) {
                    message(
                        "&r===============[&e&lUpdate&r]==============".colored(),
                        "&r| &rPlugin &6AkariLevel &7=>".colored(),
                        "&r| &b◈ &r发现了一个新的插件版本！".colored(),
                        "&r| &b◈ &r最新版本: $latestVersion".colored(),
                        "&r| &b◈ &r当前版本: $currentVersion".colored(),
                        "&r| &b◈ &r请加QQ群704109949以获取插件最新版本...".colored(),
                        "&r===============[&e&lUpdate&r]==============".colored()
                    )
                }
            } catch (_: java.net.ConnectException) {
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }
}