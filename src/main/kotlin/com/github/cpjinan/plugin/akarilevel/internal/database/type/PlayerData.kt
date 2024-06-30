package com.github.cpjinan.plugin.akarilevel.internal.database.type

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(var level: Long = 0, var exp: Long = 0)
