package top.cpjinan.akarilevel.event

import io.lumine.mythic.api.mobs.MythicMob
import io.lumine.mythic.core.mobs.ActiveMob
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * top.cpjinan.akarilevel.event
 *
 * MythicMobs 5.x 经验掉落事件。
 *
 * @author 季楠
 * @since 2026/1/16 21:58
 */
class MythicMobsDropExpEvent(
    val member: String,
    val levelGroup: String,
    var expAmount: Long,
    val killer: LivingEntity,
    val entity: Entity,
    val mob: ActiveMob,
    val mobType: MythicMob,
    val mobLevel: Double,
    val drops: List<ItemStack>
) : BukkitProxyEvent()