package com.github.cpjinan.plugin.akarilevel.internal.database.type

import org.bukkit.configuration.ConfigurationSection

data class LevelData(
    val name: String,
    val exp: String,
    val condition: List<String>,
    val action: List<String>,
    val levelSection: ConfigurationSection
)