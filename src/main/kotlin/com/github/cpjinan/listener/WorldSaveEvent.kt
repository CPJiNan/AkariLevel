package com.github.cpjinan.listener

import com.github.cpjinan.manager.RegisterManager
import taboolib.common.platform.event.SubscribeEvent

object WorldSaveEvent {
  @SubscribeEvent
  @Suppress("UNUSED_PARAMETER")
  fun onWorldSave(event: org.bukkit.event.world.WorldSaveEvent) {
    RegisterManager.getDatabase().save()
  }
}