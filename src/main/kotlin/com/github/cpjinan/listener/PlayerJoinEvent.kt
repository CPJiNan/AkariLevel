package com.github.cpjinan.listener

import com.github.cpjinan.api.LevelAPI
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerJoinEvent {
  @SubscribeEvent
  fun onPlayerJoin(event: PlayerJoinEvent) {
    LevelAPI.refreshPlayerLevel(event.player)
  }
}