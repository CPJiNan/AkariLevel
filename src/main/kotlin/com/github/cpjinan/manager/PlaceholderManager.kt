package com.github.cpjinan.manager

import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderManager : PlaceholderExpansion{
        override val identifier = "playerlevel"
        override fun onPlaceholderRequest(player: Player?, args: String): String {
            player?.let {
                if (it.isOnline) {
                    return when (args.lowercase()) {
                        "level" -> ConfigManager.player.getInt("${player.name}.level")
                        "levelname" -> ConfigManager.level.getString("${ConfigManager.player.getInt("${player.name}.level")}.name")
                        "lastlevel" -> ConfigManager.player.getInt("${player.name}.level") - 1
                        "lastlevelname" -> ConfigManager.level.getString("${ConfigManager.player.getInt("${player.name}.level") - 1}.name")
                        "nextlevel" -> ConfigManager.player.getInt("${player.name}.level") + 1
                        "nextlevelname" -> ConfigManager.level.getString("${ConfigManager.player.getInt("${player.name}.level") + 1}.name")
                        "exp" -> ConfigManager.player.getInt("${player.name}.exp")
                        "lastlevelexp" -> ConfigManager.level.getInt("${ConfigManager.player.getInt("${player.name}.level") - 1}.exp")
                        "levelexp" -> ConfigManager.level.getInt("${ConfigManager.player.getInt("${player.name}.level")}.exp")
                        "nextlevelexp" -> ConfigManager.level.getInt("${ConfigManager.player.getInt("${player.name}.level") + 1}.exp")
                        else -> ""
                    }.toString()
                }
            }
            return ""
        }
}