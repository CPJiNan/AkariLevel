package com.github.cpjinan.plugin.akarilevel.common.hook

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.common.listener.mythicmobs.MythicMobs4Listener
import com.github.cpjinan.plugin.akarilevel.common.listener.mythicmobs.MythicMobs5Listener
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import org.bukkit.Bukkit

object MythicMobs {
    fun registerMythicMobsListener() {
        if (ConfigManager.isEnabledMythicMobs() && Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            when (Bukkit.getPluginManager().getPlugin("MythicMobs")?.description?.version?.get(0)) {
                '4' -> Bukkit.getPluginManager().getPlugin(AkariLevel.plugin.name)
                    ?.let { Bukkit.getPluginManager().registerEvents(MythicMobs4Listener, it) }

                '5' -> Bukkit.getPluginManager().getPlugin(AkariLevel.plugin.name)
                    ?.let { Bukkit.getPluginManager().registerEvents(MythicMobs5Listener, it) }
            }
        }
    }
}