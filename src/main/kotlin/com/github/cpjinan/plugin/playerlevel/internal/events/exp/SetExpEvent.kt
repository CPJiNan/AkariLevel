package com.github.cpjinan.plugin.playerlevel.internal.events.exp

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class SetExpEvent(val player: Player, var exp: Int, var source: String) : BukkitProxyEvent()