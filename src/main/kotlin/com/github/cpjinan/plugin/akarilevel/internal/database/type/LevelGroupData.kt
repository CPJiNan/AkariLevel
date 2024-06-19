package com.github.cpjinan.plugin.akarilevel.internal.database.type

import org.bukkit.configuration.ConfigurationSection


data class LevelGroupData(
    val display: String,
    val subscribeSource: List<String>,
    val isEnabledTrace: Boolean,
    val traceCondition: List<String>,
    val traceAction: List<String>,
    val maxLevel: Int,
    val isEnabledAutoLevelup: Boolean,
    val isEnabledExpLimit: Boolean,
    val keyLevelSettings: ConfigurationSection
)