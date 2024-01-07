package com.github.cpjinan.plugin.playerlevel.internal.listener

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import io.lumine.mythic.api.adapters.AbstractItemStack
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.drops.DropMetadata
import io.lumine.mythic.api.drops.IItemDrop
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent
import io.lumine.mythic.core.drops.Drop
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object Mythic5Listener : Listener {
    @EventHandler
    fun onRegisterDrop(event: MythicDropLoadEvent) {
        if (event.dropName.equals("PlayerExp", ignoreCase = true)) {
            event.register(Mythic5ExpDrop(event.container.configLine, event.config))
        }
    }
}

class Mythic5ExpDrop(line: String, config: MythicLineConfig) : Drop(line, config), IItemDrop {
    override fun getDrop(meta: DropMetadata?, p1: Double): AbstractItemStack? {
        val amount = this.line.split(' ')[1].toInt()
        if (meta != null) {
            LevelAPI.addPlayerExp(Bukkit.getPlayer(meta.trigger.uniqueId)!!, amount, "MYTHIC_LISTENER")
        }
        return null
    }
}