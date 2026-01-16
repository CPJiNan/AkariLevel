package top.cpjinan.akarilevel.hook

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import org.bukkit.entity.Player
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import top.cpjinan.akarilevel.event.LegacyMythicMobsDropExpEvent
import top.cpjinan.akarilevel.event.MythicMobsDropExpEvent
import top.cpjinan.akarilevel.level.LevelGroup
import kotlin.random.Random
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent as LegacyMythicMobDeathEvent

/**
 * AkariLevel
 * top.cpjinan.akarilevel.hook
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
                    val mythicEvent = LegacyMythicMobsDropExpEvent(
                        player.name, levelGroup.name, amount,
                        event.killer, event.entity, event.mob, event.mobType, event.mobLevel, event.drops
                    )
                    mythicEvent.call()
                    if (mythicEvent.isCancelled) return
                    levelGroup.addMemberExp(player.name, mythicEvent.expAmount, "MYTHICMOBS_DROP_EXP")
                }
            }
        }
    }

    @Ghost
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
                    val mythicEvent = MythicMobsDropExpEvent(
                        player.name, levelGroup.name, amount,
                        event.killer, event.entity, event.mob, event.mobType, event.mobLevel, event.drops
                    )
                    mythicEvent.call()
                    if (mythicEvent.isCancelled) return
                    levelGroup.addMemberExp(player.name, mythicEvent.expAmount, "MYTHICMOBS_DROP_EXP")
                }
            }
        }
    }
}