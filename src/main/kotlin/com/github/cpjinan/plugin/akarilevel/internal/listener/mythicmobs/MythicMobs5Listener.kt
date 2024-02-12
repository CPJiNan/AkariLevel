package com.github.cpjinan.plugin.akarilevel.internal.listener.mythicmobs

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import io.lumine.mythic.api.adapters.AbstractItemStack
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.drops.DropMetadata
import io.lumine.mythic.api.drops.IItemDrop
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent
import io.lumine.mythic.core.drops.Drop
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MythicMobs5Listener : Listener {
    @EventHandler
    fun onRegisterDrop(event: MythicDropLoadEvent) {
        if (event.dropName.equals(ConfigManager.getMythicMobsExpDropName(), ignoreCase = true)) {
            event.register(MythicMobs5ExpDrop(event.container.configLine, event.config))
        }
    }
}

class MythicMobs5ExpDrop(line: String, config: MythicLineConfig) : Drop(line, config), IItemDrop {
    override fun getDrop(meta: DropMetadata?, p1: Double): AbstractItemStack? {
        val amount = this.line.split(' ')[1].toInt()
        if (meta != null) {
            AkariLevelAPI.addPlayerExp(Bukkit.getPlayer(meta.trigger.uniqueId)!!, amount, "MYTHICMOBS_DROP_EXP")
        }
        return null
    }
}