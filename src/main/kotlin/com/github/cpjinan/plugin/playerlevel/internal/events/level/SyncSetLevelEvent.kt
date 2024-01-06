package com.github.cpjinan.plugin.playerlevel.internal.events.level

import com.github.cpjinan.plugin.playerlevel.internal.abstracts.SyncEvent
import org.bukkit.entity.Player


class SyncSetLevelEvent(val player: Player, var level: Int, var source: String) : SyncEvent() {
    private var cancelled = false
    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }
}