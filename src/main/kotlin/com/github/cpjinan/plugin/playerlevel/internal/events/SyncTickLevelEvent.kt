package com.github.cpjinan.plugin.playerlevel.internal.events

import com.github.cpjinan.plugin.playerlevel.internal.abstracts.SyncEvent
import org.bukkit.entity.Player


class SyncTickLevelEvent(val player: Player) : SyncEvent() {
    private var cancelled = false
    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }
}