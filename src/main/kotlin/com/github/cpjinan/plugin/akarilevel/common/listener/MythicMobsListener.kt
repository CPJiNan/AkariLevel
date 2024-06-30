package com.github.cpjinan.plugin.akarilevel.common.listener

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent
import io.lumine.xikage.mythicmobs.drops.Drop
import io.lumine.xikage.mythicmobs.drops.DropMetadata
import io.lumine.xikage.mythicmobs.drops.IItemDrop
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MythicMobsListener {
    fun registerMythicMobsListener() {
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            when (Bukkit.getPluginManager().getPlugin("MythicMobs")?.description?.version?.get(0)) {
                '4' -> Bukkit.getPluginManager().getPlugin(AkariLevel.plugin.name)
                    ?.let { Bukkit.getPluginManager().registerEvents(LegacyMythicMobsDropListener, it) }

                '5' -> Bukkit.getPluginManager().getPlugin(AkariLevel.plugin.name)
                    ?.let { Bukkit.getPluginManager().registerEvents(MythicMobsDropListener, it) }
            }
        }
    }
}

object LegacyMythicMobsDropListener : Listener {
    @EventHandler
    fun onRegisterDrop(event: MythicDropLoadEvent) {
        LevelAPI.getLevelGroupNames().forEach { levelGroupName ->
            if (event.dropName.equals("AkariExp.$levelGroupName", ignoreCase = true)) {
                event.register(LegacyMythicMobsExpDrop(event.container.configLine, event.config))
            }
        }
    }
}

class LegacyMythicMobsExpDrop(line: String, config: MythicLineConfig) : Drop(line, config), IItemDrop {
    override fun getDrop(meta: DropMetadata): AbstractItemStack? {
        val levelGroupName = this.line.split(' ')[0].split(".")[1]
        val amount = this.line.split(' ')[1].toLong()
        PlayerAPI.addPlayerExp(Bukkit.getPlayer(meta.trigger.uniqueId)!!, levelGroupName, amount, "MYTHICMOBS_DROP_EXP")
        return null
    }
}

object MythicMobsDropListener : Listener {
    @EventHandler
    fun onRegisterDrop(event: io.lumine.mythic.bukkit.events.MythicDropLoadEvent) {
        LevelAPI.getLevelGroupNames().forEach { levelGroupName ->
            if (event.dropName.equals("AkariExp.$levelGroupName", ignoreCase = true)) {
                event.register(MythicMobsExpDrop(event.container.configLine, event.config))
            }
        }
    }
}

class MythicMobsExpDrop(line: String, config: io.lumine.mythic.api.config.MythicLineConfig) :
    io.lumine.mythic.core.drops.Drop(line, config),
    io.lumine.mythic.api.drops.IItemDrop {
    override fun getDrop(
        meta: io.lumine.mythic.api.drops.DropMetadata?,
        p1: Double
    ): io.lumine.mythic.api.adapters.AbstractItemStack? {
        val levelGroupName = this.line.split(' ')[0].split(".")[1]
        val amount = this.line.split(' ')[1].toLong()
        if (meta != null) {
            PlayerAPI.addPlayerExp(
                Bukkit.getPlayer(meta.trigger.uniqueId)!!,
                levelGroupName,
                amount,
                "MYTHICMOBS_DROP_EXP"
            )
        }
        return null
    }
}