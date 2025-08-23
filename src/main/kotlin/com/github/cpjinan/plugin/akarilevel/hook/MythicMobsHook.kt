package com.github.cpjinan.plugin.akarilevel.hook

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import kotlin.random.Random
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
    @SubscribeEvent
    fun onLegacyMythicMobDeath(event: LegacyMythicMobDeathEvent) {
        if (event.killer is Player) {
            val player = event.killer as Player
            val drops = event.mobType.config.getStringList("Exp-Drop") +
                    event.mobType.config.getStringList("Exp-Drops")
            drops.forEach { drop ->
                val args = drop.split(" ")
                val levelGroup = LevelGroup.getLevelGroups()[args[0]] ?: return@forEach
                val amount = args[1].run {
                    if (contains("-")) {
                        val (origin, bound) = split("-").map { it.toLong() }
                        (origin..bound).random()
                    } else toLong()
                }
                if (Random.nextDouble() < args[2].toDouble()) {
                    levelGroup.addMemberExp(player.name, amount, "MYTHICMOBS_DROP_EXP")
                }
            }
        }
    }

    @SubscribeEvent
    fun onMythicMobDeath(event: MythicMobDeathEvent) {
        if (event.killer is Player) {
            val player = event.killer as Player
            val drops = event.mobType.config.getStringList("Exp-Drop") +
                    event.mobType.config.getStringList("Exp-Drops")
            drops.forEach { drop ->
                val args = drop.split(" ")
                val levelGroup = LevelGroup.getLevelGroups()[args[0]] ?: return@forEach
                val amount = args[1].run {
                    if (contains("-")) {
                        val (origin, bound) = split("-").map { it.toLong() }
                        (origin..bound).random()
                    } else toLong()
                }
                if (Random.nextDouble() < args[2].toDouble()) {
                    levelGroup.addMemberExp(player.name, amount, "MYTHICMOBS_DROP_EXP")
                }
            }
        }
    }
}