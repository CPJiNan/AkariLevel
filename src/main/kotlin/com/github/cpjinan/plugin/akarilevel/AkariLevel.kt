package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.internal.hook.MythicMobs
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.UpdateManager
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin

object AkariLevel : Plugin() {

    val instance by lazy { BukkitPlugin.getInstance() }

    override fun onEnable() {
        DatabaseManager.getDatabase().save()
        MythicMobs.registerMythicMobsListener()
        if (ConfigManager.isEnabledSendMetrics()) Metrics(18992, instance.description.version, Platform.BUKKIT)
        UpdateManager.checkUpdate()
    }

    override fun onDisable() {
        DatabaseManager.getDatabase().save()
    }
}
