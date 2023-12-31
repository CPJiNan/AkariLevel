package com.github.cpjinan.plugin.playerlevel.internal.listener

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent
import io.lumine.xikage.mythicmobs.drops.Drop
import io.lumine.xikage.mythicmobs.drops.DropMetadata
import io.lumine.xikage.mythicmobs.drops.IItemDrop
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MythicListener : Listener {
    @EventHandler
    fun onRegisterDrop(event: MythicDropLoadEvent) {
        if (event.dropName.equals("PlayerExp", ignoreCase = true)) {
            event.register(MythicExpDrop(event.container.configLine, event.config))
        }
    }
}

class MythicExpDrop(line: String, config: MythicLineConfig) : Drop(line, config), IItemDrop {
    override fun getDrop(meta: DropMetadata): AbstractItemStack? {
        val amount = this.line.split(' ')[1].toInt()
        LevelAPI.addPlayerExp(Bukkit.getPlayer(meta.trigger.uniqueId)!!, amount)
        return null
    }
}