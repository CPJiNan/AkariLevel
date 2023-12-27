package com.github.cpjinan.plugin.playerlevel.internal.database.types

import kotlinx.serialization.Serializable

@Serializable
data class Player(var level: Int = 0, var exp: Int = 0)
