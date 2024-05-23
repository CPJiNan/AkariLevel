package com.github.cpjinan.plugin.akarilevel.common.event.level

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerRefreshLevelEvent(val player: Player, var source: String) : BukkitProxyEvent()