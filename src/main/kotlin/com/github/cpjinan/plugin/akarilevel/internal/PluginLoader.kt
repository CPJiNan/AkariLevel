package com.github.cpjinan.plugin.akarilevel.internal

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.common.listener.MythicMobsListener
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.LanguageManager
import com.github.cpjinan.plugin.akarilevel.utils.LoggerUtil
import com.github.cpjinan.plugin.akarilevel.utils.UpdateUtil
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.command.simpleCommand
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.lang.sendMessage
import taboolib.module.metrics.Metrics

object PluginLoader {
    @Awake(LifeCycle.LOAD)
    fun load() {
        LanguageManager.saveDefaultResource()
        console().sendLang("Plugin-Loading", plugin.description.version)
        if (ConfigManager.isEnabledSendMetrics()) Metrics(18992, plugin.description.version, Platform.BUKKIT)
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
        if (ConfigManager.isEnabledCheckUpdate()) UpdateUtil.getPluginUpdate()
        UpdateUtil.getPluginNotice()
        UpdateUtil.getConfigUpdate()
        simpleCommand("test"){ sender, args ->
            sender.sendMessage(org.serverct.ersha.jd.AttributeAPI.getAttrData(sender.cast())
                .getAttributeValue(args[0]).toString())
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun disable() {
        console().sendLang("Plugin-Disable")
    }

}