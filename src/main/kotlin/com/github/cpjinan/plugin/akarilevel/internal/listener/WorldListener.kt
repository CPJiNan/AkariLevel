package com.github.cpjinan.plugin.akarilevel.internal.listener

import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import taboolib.common.platform.event.SubscribeEvent

object WorldListener {
    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onWorldSave(event: org.bukkit.event.world.WorldSaveEvent) {
        DatabaseManager.getCache().playerData.forEach { (name, playerData) ->
            if (ConfigManager.isEnabledRedis()) DatabaseManager.getRedis().updatePlayer(name, playerData)
            DatabaseManager.getDatabase().updatePlayer(name, playerData)
        }
        if (ConfigManager.isEnabledRedis()) DatabaseManager.getRedis().save()
        DatabaseManager.getDatabase().save()
    }
}