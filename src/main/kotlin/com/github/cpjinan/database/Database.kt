package com.github.cpjinan.database

import com.github.cpjinan.database.types.Player

interface Database {
  fun getPlayerByName(name: String): Player
  fun updatePlayer(name: String, value: Player)

  fun save()
}