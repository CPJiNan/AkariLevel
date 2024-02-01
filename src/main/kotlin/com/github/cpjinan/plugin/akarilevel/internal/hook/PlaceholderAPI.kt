package com.github.cpjinan.plugin.akarilevel.internal.hook

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.LevelManager
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderAPI : PlaceholderExpansion {
    override val identifier = ConfigManager.getPlaceholderIdentifier()
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        player?.let {
            if (it.isOnline) {
                val curLvl = AkariLevelAPI.getPlayerLevel(player)
                val prevLvl = curLvl - 1
                val nextLvl = curLvl + 1
                return when (args.lowercase()) {
                    "level" -> curLvl
                    "lastlevel" -> prevLvl
                    "nextlevel" -> nextLvl

                    "exp" -> AkariLevelAPI.getPlayerExp(player)

                    "levelname" -> LevelManager.getLevelData(curLvl)?.name
                    "lastlevelname" -> LevelManager.getLevelData(prevLvl)?.name
                    "nextlevelname" -> LevelManager.getLevelData(nextLvl)?.name

                    "levelexp" -> LevelManager.getLevelData(curLvl)?.exp
                    "lastlevelexp" -> LevelManager.getLevelData(prevLvl)?.exp
                    "nextlevelexp" -> LevelManager.getLevelData(nextLvl)?.exp
                    else -> ""
                }.toString()
            }
        }
        return ""
    }
}