package com.github.cpjinan.plugin.playerlevel.internal.events.level

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class SetLevelEvent(val player: Player, var level: Int, var source: String) : BukkitProxyEvent()