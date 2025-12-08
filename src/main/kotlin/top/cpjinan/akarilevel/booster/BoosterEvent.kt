package top.cpjinan.akarilevel.booster

import taboolib.platform.type.BukkitProxyEvent

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
    val member: String,
    val levelGroup: String,
    val expAmount: Long,
    val source: String,
    val boosters: Map<String, Booster>,
    var multiplier: Double
) : BukkitProxyEvent()