package com.github.cpjinan.plugin.akarilevel.internal.database.type

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(var level: Int = 0, var exp: Int = 0)
