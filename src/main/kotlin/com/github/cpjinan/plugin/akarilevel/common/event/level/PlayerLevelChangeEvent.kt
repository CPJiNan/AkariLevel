package com.github.cpjinan.plugin.akarilevel.common.event.level

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 玩家等级变更事件
 * @param player 玩家
 * @param levelGroup 等级组编辑名
 * @param oldLevel 更改前等级
 * @param newLevel 更改后等级
 * @param source 事件来源
 */
class PlayerLevelChangeEvent(
    val player: Player,
    var levelGroup: String,
    var oldLevel: Long,
    var newLevel: Long,
    var source: String
) :
    BukkitProxyEvent()