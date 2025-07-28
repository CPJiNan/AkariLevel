package com.github.cpjinan.plugin.akarilevel.event

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.event
 *
 * @author 季楠
 * @since 2025/7/28 22:04
 */
class MemberChangeEvent(
    var member: String,
    var levelGroup: String,
    var type: LevelGroup.MemberChangeType,
    var source: String
) : BukkitProxyEvent()