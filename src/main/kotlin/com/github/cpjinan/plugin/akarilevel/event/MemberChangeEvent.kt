package com.github.cpjinan.plugin.akarilevel.event

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.event
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