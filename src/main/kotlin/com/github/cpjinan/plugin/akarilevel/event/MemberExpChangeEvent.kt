package com.github.cpjinan.plugin.akarilevel.event

import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.event
 *
 * @author 季楠
 * @since 2025/7/28 22:05
 */
class MemberExpChangeEvent(
    var member: String,
    var levelGroup: String,
    var expAmount: Long,
    var source: String
) : BukkitProxyEvent()