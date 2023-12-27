package com.github.cpjinan.plugin.playerlevel.internal.manager

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderManager : PlaceholderExpansion {
  override val identifier = "playerlevel"
  override fun onPlaceholderRequest(player: Player?, args: String): String {
    player?.let {
      if (it.isOnline) {
        val curLvl = LevelAPI.getPlayerLevel(player)
        val prevLvl = curLvl - 1
        val nextLvl = curLvl + 1
        return when (args.lowercase()) {
          "level" -> curLvl
          "lastlevel" -> prevLvl
          "nextlevel" -> nextLvl

          "exp" -> LevelAPI.getPlayerExp(player)

          "levelname" -> ConfigManager.getLevelName(curLvl)
          "lastlevelname" -> ConfigManager.getLevelName(prevLvl)
          "nextlevelname" -> ConfigManager.getLevelName(nextLvl)

          "levelexp" -> ConfigManager.getLevelExp(curLvl)
          "lastlevelexp" -> ConfigManager.getLevelExp(prevLvl)
          "nextlevelexp" -> ConfigManager.getLevelExp(nextLvl)
          else -> ""
        }.toString()
      }
    }
    return ""
  }
}