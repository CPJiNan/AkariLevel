package top.cpjinan.akarilevel.event

import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * top.cpjinan.akarilevel.event
 *
 * 等级组成员等级变更事件。
 *
 * @author 季楠
 * @since 2025/8/7 22:45
 */
class MemberLevelChangeEvent(
    val member: String,
    val levelGroup: String,
    val oldLevel: Long,
    var newLevel: Long,
    var source: String
) : BukkitProxyEvent()