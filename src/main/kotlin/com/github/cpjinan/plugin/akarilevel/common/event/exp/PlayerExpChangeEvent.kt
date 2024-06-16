package com.github.cpjinan.plugin.akarilevel.common.event.exp

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerExpChangeEvent(val player: Player, var levelGroup: String, var expAmount: Int, var source: String) :
    BukkitProxyEvent()