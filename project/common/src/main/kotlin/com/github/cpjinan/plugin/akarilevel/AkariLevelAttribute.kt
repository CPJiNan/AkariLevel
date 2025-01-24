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
    fun getAttributeValue(player: Player, attribute: String): Number
}