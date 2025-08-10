package com.github.cpjinan.plugin.akarilevel.event

import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.event
 *
 * 等级组取消注册事件。
 *
 * @author 季楠
 * @since 2025/8/7 22:45
 */
class LevelGroupUnregisterEvent(val levelGroup: String) : BukkitProxyEvent()