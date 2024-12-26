package com.github.cpjinan.plugin.akarilevel.common.hook

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import io.lumine.mythic.api.adapters.AbstractItemStack
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.drops.DropMetadata
import io.lumine.mythic.api.drops.IItemDrop
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent
import io.lumine.mythic.core.drops.Drop
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import kotlin.random.Random
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack as LegacyAbstractItemStack
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter as LegacyBukkitAdapter
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent as LegacyMythicDropLoadEvent
import io.lumine.xikage.mythicmobs.drops.Drop as LegacyDrop
import io.lumine.xikage.mythicmobs.drops.DropMetadata as LegacyDropMetadata
import io.lumine.xikage.mythicmobs.drops.IItemDrop as LegacyIItemDrop
import io.lumine.xikage.mythicmobs.io.MythicLineConfig as LegacyMythicLineConfig

object MythicMobs {
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
    fun onRegisterDrop(event: LegacyMythicDropLoadEvent) {
        LevelAPI.getLevelGroupNames().forEach { levelGroupName ->
            if (event.dropName.equals("AkariExp.$levelGroupName", ignoreCase = true)) {
                event.register(LegacyMythicMobsExpDrop(event.container.configLine, event.config))
            }
        }
    }
}

class LegacyMythicMobsExpDrop(line: String, config: LegacyMythicLineConfig) : LegacyDrop(line, config),
    LegacyIItemDrop {
    override fun getDrop(meta: LegacyDropMetadata): LegacyAbstractItemStack? {
        val levelGroupName = this.line.split(' ')[0].split(".")[1]
        val amount = this.line.split(' ')[1].toAmount()
        PlayerAPI.addPlayerExp(Bukkit.getPlayer(meta.trigger.uniqueId)!!, levelGroupName, amount, "MYTHICMOBS_DROP_EXP")
        return LegacyBukkitAdapter.adapt(buildItem(XMaterial.AIR))
    }
}

object MythicMobsDropListener : Listener {
    @EventHandler
    fun onRegisterDrop(event: MythicDropLoadEvent) {
        LevelAPI.getLevelGroupNames().forEach { levelGroupName ->
            if (event.dropName.equals("AkariExp.$levelGroupName", ignoreCase = true)) {
                event.register(MythicMobsExpDrop(event.container.configLine, event.config))
            }
        }
    }
}

class MythicMobsExpDrop(line: String, config: MythicLineConfig) :
    Drop(line, config),
    IItemDrop {
    override fun getDrop(
        meta: DropMetadata?,
        p1: Double
    ): AbstractItemStack? {
        val levelGroupName = this.line.split(' ')[0].split(".")[1]
        val amount = this.line.split(' ')[1].toAmount()
        if (meta != null) {
            PlayerAPI.addPlayerExp(
                Bukkit.getPlayer(meta.trigger.uniqueId)!!,
                levelGroupName,
                amount,
                "MYTHICMOBS_DROP_EXP"
            )
        }
        return BukkitAdapter.adapt(buildItem(XMaterial.AIR))
    }
}

fun String.toAmount(): Long {
    return if (this.contains("~")) {
        val range = this.split("~", limit = 2)
        val (start, end) = range.map { it.toLong() }
        return Random.nextLong(start, end + 1)
    } else {
        this.toLong()
    }
}