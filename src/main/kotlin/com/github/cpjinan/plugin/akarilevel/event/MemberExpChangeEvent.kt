package com.github.cpjinan.plugin.akarilevel.event

import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.event
 *
 * 等级组成员经验变更事件。
 *
 * @author 季楠
 * @since 2025/8/7 22:45
 */
class MemberExpChangeEvent(
    val member: String,
    val levelGroup: String,
    var expAmount: Long,
    var source: String
) : BukkitProxyEvent()