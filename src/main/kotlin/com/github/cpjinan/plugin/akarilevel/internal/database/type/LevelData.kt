package com.github.cpjinan.plugin.akarilevel.internal.database.type

data class LevelData(
    val name: String,
    val exp: String,
    val condition: List<String>,
    val action: List<String>
)