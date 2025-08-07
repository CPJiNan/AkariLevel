package com.github.cpjinan.plugin.akarilevel.entity

import com.google.gson.annotations.SerializedName

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.entity
 *
 * 玩家数据。
 *
 * @author 季楠
 * @since 2025/8/7 22:58
 */
data class PlayerData(
    @SerializedName("level_groups")
    val levelGroups: MutableMap<String, PlayerLevelData> = mutableMapOf(),
)