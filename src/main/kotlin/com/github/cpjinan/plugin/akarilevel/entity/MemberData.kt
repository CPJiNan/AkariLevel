package com.github.cpjinan.plugin.akarilevel.entity

import com.google.gson.annotations.SerializedName

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.entity
 *
 * 成员数据。
 *
 * @author 季楠
 * @since 2025/8/7 22:58
 */
data class MemberData(
    @SerializedName("level_groups")
    val levelGroups: MutableMap<String, MemberLevelData> = mutableMapOf(),
)