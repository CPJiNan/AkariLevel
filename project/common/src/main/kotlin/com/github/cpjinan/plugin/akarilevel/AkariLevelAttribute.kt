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
    fun getAttribute(player: Player, attributeName: String): Number
    fun getSourceAttribute(player: Player, attributeName: String): Number
    fun setSourceAttribute(player: Player, attributeList: List<String>, amount: Number)
    fun removeSourceAttribute(player: Player)
    fun updateAttribute(player: Player)
}