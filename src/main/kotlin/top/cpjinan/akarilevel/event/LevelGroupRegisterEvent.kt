package top.cpjinan.akarilevel.event

import taboolib.platform.type.BukkitProxyEvent

/**
 * AkariLevel
 * top.cpjinan.akarilevel.event
 *
 * 等级组注册事件。
 *
 * @author 季楠
 * @since 2025/8/7 22:45
 */
class LevelGroupRegisterEvent(val levelGroup: String) : BukkitProxyEvent()