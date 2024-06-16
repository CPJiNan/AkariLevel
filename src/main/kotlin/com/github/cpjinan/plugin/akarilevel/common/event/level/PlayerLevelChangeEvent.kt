package com.github.cpjinan.plugin.akarilevel.common.event.level

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerLevelChangeEvent(
    val player: Player,
    var levelGroup: String,
    var oldLevel: Int,
    var newLevel: Int,
    var source: String
) :
    BukkitProxyEvent()