package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import com.github.cpjinan.plugin.akarilevel.level.ConfigLevelGroup
import com.github.cpjinan.plugin.akarilevel.script.classLoader
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.metrics.Metrics
import taboolib.platform.util.bukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * 插件主类。
 *
 * @author 季楠
 * @since 2025/8/7 22:04
 */
object AkariLevel : Plugin() {
    /**
     * 插件加载事件。
     */
    override fun onLoad() {
        console().sendLang("PluginLoading", bukkitPlugin.description.version)
        // bStats 统计。
        if (SettingsConfig.sendMetrics) Metrics(
            18992,
            bukkitPlugin.description.version,
            Platform.BUKKIT
        )
    }

    /**
     * 插件启用事件。
     */
    override fun onEnable() {
        console().sendMessage("")
        console().sendMessage("&o     _    _              _ _                   _  ".colored())
        console().sendMessage("&o    / \\  | | ____ _ _ __(_) |    _____   _____| | ".colored())
        console().sendMessage("&o   / _ \\ | |/ / _` | '__| | |   / _ \\ \\ / / _ \\ | ".colored())
        console().sendMessage("&o  / ___ \\|   < (_| | |  | | |__|  __/\\ V /  __/ | ".colored())
        console().sendMessage("&o /_/   \\_\\_|\\_\\__,_|_|  |_|_____\\___| \\_/ \\___|_| ".colored())
        console().sendMessage("")

        // 为脚本引擎提供类加载器。
        classLoader = this.javaClass.classLoader

        // 从配置文件加载等级组。
        ConfigLevelGroup.reloadConfigLevelGroups()

        console().sendLang("PluginEnabled")
    }

    /**
     * 插件卸载事件。
     */
    override fun onDisable() {
        console().sendLang("PluginDisable")
    }
}