package com.github.cpjinan.plugin.akarilevel.internal.listener.mythicmobs

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent
import io.lumine.xikage.mythicmobs.drops.Drop
import io.lumine.xikage.mythicmobs.drops.DropMetadata
import io.lumine.xikage.mythicmobs.drops.IItemDrop
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MythicMobs4Listener : Listener {
    @EventHandler
    fun onRegisterDrop(event: MythicDropLoadEvent) {
        if (event.dropName.equals(ConfigManager.getMythicMobsExpDropName(), ignoreCase = true)) {
            event.register(MythicMobs4ExpDrop(event.container.configLine, event.config))
        }
    }
}

class MythicMobs4ExpDrop(line: String, config: MythicLineConfig) : Drop(line, config), IItemDrop {
    override fun getDrop(meta: DropMetadata): AbstractItemStack? {
        val amount = this.line.split(' ')[1].toInt()
        AkariLevelAPI.addPlayerExp(Bukkit.getPlayer(meta.trigger.uniqueId)!!, amount, "MYTHICMOBS_DROP_EXP")
        return null
    }
}