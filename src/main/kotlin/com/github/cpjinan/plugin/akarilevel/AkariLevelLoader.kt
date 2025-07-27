package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import com.github.cpjinan.plugin.akarilevel.utils.LoggerUtils
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.function.console
import taboolib.common.util.unsafeLazy
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/6/21 19:40
 */
object AkariLevelLoader {
    val api by unsafeLazy { DefaultAkariLevelAPI() }

    fun startup() {
        AkariLevel.register(api)
    }

    @Awake(LifeCycle.LOAD)
    fun onLoad() {
        console().sendLang("Plugin-Loading", plugin.description.version)
        if (SettingsConfig.sendMetrics) Metrics(
            18992,
            BukkitPlugin.getInstance().description.version,
            Platform.BUKKIT
        )
    }

    @Awake(LifeCycle.ENABLE)
    fun onEnable() {
        LoggerUtils.message(
            "",
            "&o  __     ___ _         ____                ".colored(),
            "&o  \\ \\   / (_) |_ __ _ / ___| ___ _ __ ___  ".colored(),
            "&o   \\ \\ / /| | __/ _` | |  _ / _ \\ '_ ` _ \\ ".colored(),
            "&o    \\ V / | | || (_| | |_| |  __/ | | | | |".colored(),
            "&o     \\_/  |_|\\__\\__,_|\\____|\\___|_| |_| |_|".colored(),
            ""
        )
        console().sendLang("Plugin-Enabled")
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        console().sendLang("Plugin-Disable")
    }
}