package com.github.cpjinan.plugin.akarilevel.hook

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import org.bukkit.entity.Player
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent as LegacyMythicMobDeathEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.hook
 *
 * MythicMobs 挂钩。
 *
 * @author 季楠
 * @since 2025/8/23 11:13
 */
object MythicMobsHook {
    @Ghost
    @SubscribeEvent
    fun onLegacyMythicMobDeath(event: LegacyMythicMobDeathEvent) {
        if (event.killer is Player) {
            val player = event.killer as Player
            val drops = event.mobType.config.getStringList("Exp-Drop") +
                    event.mobType.config.getStringList("Exp-Drops")
        }
    }

    @Ghost
    @SubscribeEvent
    fun onMythicMobDeath(event: MythicMobDeathEvent) {
        if (event.killer is Player) {
            val player = event.killer as Player
            val drops = event.mobType.config.getStringList("Exp-Drop") +
                    event.mobType.config.getStringList("Exp-Drops")
        }
    }
}