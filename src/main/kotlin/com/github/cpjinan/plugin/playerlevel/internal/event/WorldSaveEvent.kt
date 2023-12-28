package com.github.cpjinan.plugin.playerlevel.internal.event

import com.github.cpjinan.plugin.playerlevel.internal.manager.RegisterManager
import taboolib.common.platform.event.SubscribeEvent

object WorldSaveEvent {
  @SubscribeEvent
  @Suppress("UNUSED_PARAMETER")
  fun onWorldSave(event: org.bukkit.event.world.WorldSaveEvent) {
    RegisterManager.getDatabase().save()
  }
}