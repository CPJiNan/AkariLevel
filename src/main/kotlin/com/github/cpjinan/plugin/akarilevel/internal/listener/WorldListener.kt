package com.github.cpjinan.plugin.akarilevel.internal.listener

import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import taboolib.common.platform.event.SubscribeEvent

object WorldListener {
    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onWorldSave(event: org.bukkit.event.world.WorldSaveEvent) {
        DatabaseManager.getDatabase().save()
    }
}