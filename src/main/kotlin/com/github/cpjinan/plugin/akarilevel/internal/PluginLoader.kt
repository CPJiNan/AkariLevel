package com.github.cpjinan.plugin.akarilevel.internal

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.github.cpjinan.plugin.akarilevel.common.PluginLanguage
import com.github.cpjinan.plugin.akarilevel.common.PluginUpdate
import com.github.cpjinan.plugin.akarilevel.internal.listener.MythicMobsListener
import com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import taboolib.module.metrics.Metrics

object PluginLoader {
    @Awake(LifeCycle.LOAD)
    fun load() {
        Language.enableSimpleComponent = true
        PluginLanguage.saveDefaultResource()
        console().sendLang("Plugin-Loading", plugin.description.version)
        if (PluginConfig.isEnabledSendMetrics()) Metrics(18992, plugin.description.version, Platform.BUKKIT)
    }

    @Awake(LifeCycle.ENABLE)
    fun enable() {
        LoggerUtil.message(
            "",
            "&o     _    _              _ _                   _  ".colored(),
            "&o    / \\  | | ____ _ _ __(_) |    _____   _____| | ".colored(),
            "&o   / _ \\ | |/ / _` | '__| | |   / _ \\ \\ / / _ \\ | ".colored(),
            "&o  / ___ \\|   < (_| | |  | | |__|  __/\\ V /  __/ | ".colored(),
            "&o /_/   \\_\\_|\\_\\__,_|_|  |_|_____\\___| \\_/ \\___|_| ".colored(),
            ""
        )
        MythicMobsListener.registerMythicMobsListener()
        console().sendLang("Plugin-Enabled")
        if (PluginConfig.isEnabledCheckUpdate()) PluginUpdate.getPluginUpdate()
        PluginUpdate.getPluginNotice()
        PluginUpdate.getConfigUpdate()
    }

    @Awake(LifeCycle.DISABLE)
    fun disable() {
        console().sendLang("Plugin-Disable")
    }

}