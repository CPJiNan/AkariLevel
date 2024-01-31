package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.UpdateManager
import taboolib.common.platform.Plugin
import taboolib.platform.BukkitPlugin

object AkariLevel : Plugin() {

    val instance by lazy { BukkitPlugin.getInstance() }

    override fun onEnable() {
        DatabaseManager.getDatabase().save()
        UpdateManager.checkUpdate()
    }

    override fun onDisable() {
        DatabaseManager.getDatabase().save()
    }
}
