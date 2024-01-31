package com.github.cpjinan.plugin.akarilevel.internal.manager

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilib.utils.LoggerUtil.message
import com.github.cpjinan.plugin.akarilib.utils.VersionUtil.toSemanticVersion
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin
import java.net.HttpURLConnection
import java.net.URL

object UpdateManager {

    fun checkUpdate() {
        getPluginNotice()
        getPluginUpdate()
    }

    private fun getPluginNotice() {
        Thread {
            val urlConnection =
                URL("https://cpjinan.github.io/Pages/PlayerLevel/notice.html").openConnection() as HttpURLConnection
            try {
                val message = urlConnection.inputStream.bufferedReader().readText()
                if (message.isNotBlank()) message(AkariLevel.instance, message.colored())
            } catch (_: java.net.ConnectException) {
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }

    private fun getPluginUpdate() {
        if (ConfigManager.isEnabledCheckUpdate()) {
            Thread {
                val urlConnection =
                    URL("https://cpjinan.github.io/Pages/PlayerLevel/version.html").openConnection() as HttpURLConnection
                try {
                    val latestVersion = urlConnection.inputStream.bufferedReader().readText().toSemanticVersion()!!
                    val currentVersion = BukkitPlugin.getInstance().description.version.toSemanticVersion()!!
                    if (latestVersion > currentVersion) {
                        message(
                            AkariLevel.instance,
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
}