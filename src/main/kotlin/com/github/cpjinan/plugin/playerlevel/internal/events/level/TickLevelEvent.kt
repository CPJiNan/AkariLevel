package com.github.cpjinan.plugin.playerlevel.internal.events.level

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent


class TickLevelEvent(val player: Player, var source: String) : BukkitProxyEvent()