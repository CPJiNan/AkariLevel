package top.cpjinan.akarilevel.event

import taboolib.platform.type.BukkitProxyEvent
import top.cpjinan.akarilevel.level.LevelGroup

/**
 * AkariLevel
 * top.cpjinan.akarilevel.event
 *
 * 等级组成员变更事件。
 *
 * @author 季楠
 * @since 2025/8/7 22:45
 */
class MemberChangeEvent(
    val member: String,
    val levelGroup: String,
    val type: LevelGroup.MemberChangeType,
    var source: String
) : BukkitProxyEvent()