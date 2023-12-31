package com.github.cpjinan.plugin.playerlevel.internal.events

import com.github.cpjinan.plugin.playerlevel.internal.abstracts.SyncEvent
import org.bukkit.entity.Player


class SyncLevelUpEvent(val player: Player, var fromTickLvl: Boolean) : SyncEvent() {
    private var cancelled = false
    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }
}