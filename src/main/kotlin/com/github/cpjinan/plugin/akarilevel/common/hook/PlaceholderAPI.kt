package com.github.cpjinan.plugin.akarilevel.common.hook

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import org.bukkit.entity.Player
import taboolib.common5.util.createBar
import taboolib.module.chat.colored
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderAPI : PlaceholderExpansion {
    override val identifier = ConfigManager.getPlaceholderIdentifier()
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        player?.let {
            if (it.isOnline) {
                val levelGroup: String =
                    args.split("_")[0].takeIf { name -> name != "Trace" }
                        ?: PlayerAPI.getPlayerTraceLevelGroup(it)
                val curLvl = PlayerAPI.getPlayerLevel(player, levelGroup)
                val prevLvl = curLvl - 1
                val nextLvl = curLvl + 1
                return when (args.lowercase().split("_")[1]) {
                    "display" -> LevelAPI.getLevelGroupData(levelGroup).display.colored()

                    "level" -> curLvl
                    "lastlevel" -> prevLvl
                    "nextlevel" -> nextLvl

                    "exp" -> PlayerAPI.getPlayerExp(player, levelGroup)

                    "levelname" -> LevelAPI.getLevelName(levelGroup, curLvl).colored()
                    "lastlevelname" -> LevelAPI.getLevelName(levelGroup, prevLvl).colored()
                    "nextlevelname" -> LevelAPI.getLevelName(levelGroup, nextLvl).colored()

                    "levelexp" -> LevelAPI.getLevelExp(levelGroup, curLvl)
                    "lastlevelexp" -> LevelAPI.getLevelExp(levelGroup, prevLvl)
                    "nextlevelexp" -> LevelAPI.getLevelExp(levelGroup, nextLvl)

                    "expprogresspercent" -> ((PlayerAPI.getPlayerExp(player, levelGroup)
                        .toDouble() / LevelAPI.getLevelExp(
                        levelGroup,
                        nextLvl
                    )) * 100).coerceIn(0.0, 100.0).toInt()

                    "levelprogresspercent" -> ((PlayerAPI.getPlayerLevel(player, levelGroup)
                        .toDouble() / LevelAPI.getLevelGroupData(levelGroup).maxLevel) * 100).coerceIn(0.0, 100.0)
                        .toInt()

                    "expprogressbar" -> createBar(
                        ConfigManager.getExpProgressBarEmpty().colored(),
                        ConfigManager.getExpProgressBarFull().colored(),
                        ConfigManager.getExpProgressBarLength(),
                        PlayerAPI.getPlayerExp(player, levelGroup).toDouble() / LevelAPI.getLevelExp(
                            levelGroup,
                            nextLvl
                        )
                    )

                    "levelprogressbar" -> createBar(
                        ConfigManager.getLevelProgressBarEmpty().colored(),
                        ConfigManager.getLevelProgressBarFull().colored(),
                        ConfigManager.getLevelProgressBarLength(),
                        PlayerAPI.getPlayerLevel(player, levelGroup)
                            .toDouble() / LevelAPI.getLevelGroupData(levelGroup).maxLevel
                    )

                    else -> ""
                }.toString()
            }
        }
        return ""
    }
}