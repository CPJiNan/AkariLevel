package com.github.cpjinan.plugin.akarilevel.common.event.exp

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 玩家经验变更事件
 * @param player 玩家
 * @param levelGroup 等级组编辑名
 * @param expAmount 经验变化量 (经验减少时为负值)
 * @param source 事件来源
 * @author CPJiNan
 * @since 2024/06/23
 */
class PlayerExpChangeEvent(
    val player: Player,
    var levelGroup: String,
    var expAmount: Int,
    var source: String
) :
    BukkitProxyEvent()