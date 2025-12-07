package top.cpjinan.akarilevel.booster

import taboolib.platform.type.BukkitProxyEvent
import top.cpjinan.akarilevel.event.MemberExpChangeEvent
import java.util.*

/**
 * AkariLevel
 * top.cpjinan.akarilevel.booster
 *
 * 经验加成器事件。
 *
 * @author 季楠
 * @since 2025/12/6 21:14
 */
class BoosterEvent(
    val memberExpChangeEvent: MemberExpChangeEvent,
    val boosters: Map<UUID, BoosterData>,
    var multiplier: Double
) : BukkitProxyEvent()