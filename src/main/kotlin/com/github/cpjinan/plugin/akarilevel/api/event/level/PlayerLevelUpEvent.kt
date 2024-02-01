package com.github.cpjinan.plugin.akarilevel.api.event.level

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerLevelUpEvent(val player: Player, var source: String) : BukkitProxyEvent()