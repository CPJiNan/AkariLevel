package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.internal.hook.MythicMobs
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.UpdateManager
import com.github.cpjinan.plugin.akarilevel.utils.DebugUtil
import com.github.cpjinan.plugin.akarilevel.utils.MetricsUtil
import taboolib.common.platform.Plugin
import taboolib.platform.BukkitPlugin

object AkariLevel : Plugin() {

    val instance by lazy { BukkitPlugin.getInstance() }

    override fun onEnable() {
        DebugUtil.printLogo()
        DatabaseManager.getDatabase().save()
        MythicMobs.registerMythicMobsListener()
        if (ConfigManager.isEnabledSendMetrics()) MetricsUtil.registerBukkitMetrics(18992)
        UpdateManager.checkUpdate()
    }

    override fun onDisable() {
        DatabaseManager.getDatabase().save()
    }
}