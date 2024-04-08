package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData
import com.github.cpjinan.plugin.akarilevel.internal.hook.MythicMobs
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.UpdateManager
import com.github.cpjinan.plugin.akarilevel.utils.LoggerUtil
import com.github.cpjinan.plugin.akarilevel.utils.MetricsUtil
import taboolib.common.platform.Plugin
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin

object AkariLevel : Plugin() {

    val instance by lazy { BukkitPlugin.getInstance() }

    override fun onEnable() {
        LoggerUtil.message(
            "",
            "&o     _    _              _ _                   _  ".colored(),
            "&o    / \\  | | ____ _ _ __(_) |    _____   _____| | ".colored(),
            "&o   / _ \\ | |/ / _` | '__| | |   / _ \\ \\ / / _ \\ | ".colored(),
            "&o  / ___ \\|   < (_| | |  | | |__|  __/\\ V /  __/ | ".colored(),
            "&o /_/   \\_\\_|\\_\\__,_|_|  |_|_____\\___| \\_/ \\___|_| ".colored(),
            "",
            "&7正在加载 &3Akari&b&lLevel&7... &8${instance.description.version}".colored()
        )
        if (ConfigManager.isEnabledSendMetrics()) MetricsUtil.registerBukkitMetrics(18992)
        if (ConfigManager.isEnabledCheckUpdate()) UpdateManager.checkUpdate()
        MythicMobs.registerMythicMobsListener()
    }

    override fun onDisable() {
        DatabaseManager.getDatabase().save()
    }
}