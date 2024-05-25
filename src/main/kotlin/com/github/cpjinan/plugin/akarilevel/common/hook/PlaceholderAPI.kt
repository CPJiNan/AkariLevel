package com.github.cpjinan.plugin.akarilevel.common.hook

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.LevelManager
import org.bukkit.entity.Player
import taboolib.common5.util.createBar
import taboolib.module.chat.colored
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

                    "levelname" -> LevelManager.getName(curLvl)
                    "lastlevelname" -> LevelManager.getName(prevLvl)
                    "nextlevelname" -> LevelManager.getName(nextLvl)

                    "levelexp" -> LevelManager.getExp(curLvl)
                    "lastlevelexp" -> LevelManager.getExp(prevLvl)
                    "nextlevelexp" -> LevelManager.getExp(nextLvl)

                    "expprogressbar" -> createBar(
                        ConfigManager.getExpProgressBarEmpty().colored(),
                        ConfigManager.getExpProgressBarFull().colored(),
                        ConfigManager.getExpProgressBarLength(),
                        AkariLevelAPI.getPlayerExp(player).toDouble() / LevelManager.getExp(nextLvl)
                    )

                    "levelprogressbar" -> createBar(
                        ConfigManager.getLevelProgressBarEmpty().colored(),
                        ConfigManager.getLevelProgressBarFull().colored(),
                        ConfigManager.getLevelProgressBarLength(),
                        AkariLevelAPI.getPlayerLevel(player).toDouble() / ConfigManager.getMaxLevel()
                    )

                    else -> ""
                }.toString()
            }
        }
        return ""
    }
}