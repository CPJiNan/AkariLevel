package top.cpjinan.akarilevel.event

import taboolib.platform.type.BukkitProxyEvent
import top.cpjinan.akarilevel.booster.Booster
import java.util.*

/**
 * AkariLevel
 * top.cpjinan.akarilevel.event
 *
 * 经验加成器应用事件。
 *
 * @author 季楠
 * @since 2025/12/6 21:14
 */
class BoosterApplyEvent(
    val member: String,
    val levelGroup: String,
    val expAmount: Long,
    val source: String,
    val boosters: Map<UUID, Booster>,
    var multiplier: Double
) : BukkitProxyEvent()