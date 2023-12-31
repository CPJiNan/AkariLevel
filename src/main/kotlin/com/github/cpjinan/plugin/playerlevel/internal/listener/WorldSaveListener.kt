package com.github.cpjinan.plugin.playerlevel.internal.listener

import com.github.cpjinan.plugin.playerlevel.internal.manager.RegisterManager
import taboolib.common.platform.event.SubscribeEvent

object WorldSaveListener {
    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onWorldSave(event: org.bukkit.event.world.WorldSaveEvent) {
        RegisterManager.getDatabase().save()
    }
}