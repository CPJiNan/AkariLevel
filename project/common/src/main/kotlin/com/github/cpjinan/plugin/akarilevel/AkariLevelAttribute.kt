package com.github.cpjinan.plugin.akarilevel

import org.bukkit.entity.Player

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/24 13:17
 */
interface AkariLevelAttribute {
    /** 获取玩家属性值 (全部来源) **/
    fun getAttribute(player: Player, attributeName: String): Number

    /** 获取玩家属性值 (插件来源) **/
    fun getSourceAttribute(player: Player, attributeName: String): Number

    /** 设置玩家属性值 (插件来源) **/
    fun setSourceAttribute(player: Player, attributeList: List<String>, amount: Number)

    /** 清空玩家属性值 (插件来源) **/
    fun removeSourceAttribute(player: Player)

    /** 更新玩家属性值 (全部来源) **/
    fun updateAttribute(player: Player)
}