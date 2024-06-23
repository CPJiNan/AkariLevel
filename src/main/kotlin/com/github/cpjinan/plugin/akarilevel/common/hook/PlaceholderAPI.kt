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
                    args.lowercase().split("_")[0].takeIf { name -> name != "Trace" }
                        ?: PlayerAPI.getPlayerTraceLevelGroup(it)
                val curLvl = PlayerAPI.getPlayerLevel(player, levelGroup)
                val prevLvl = curLvl - 1
                val nextLvl = curLvl + 1
                return when (args.lowercase()) {
                    "${levelGroup}_display" -> LevelAPI.getLevelGroupData(levelGroup).display.colored()

                    "${levelGroup}_level" -> curLvl
                    "${levelGroup}_lastlevel" -> prevLvl
                    "${levelGroup}_nextlevel" -> nextLvl

                    "${levelGroup}_exp" -> PlayerAPI.getPlayerExp(player, levelGroup)

                    "${levelGroup}_levelname" -> LevelAPI.getLevelName(levelGroup, curLvl).colored()
                    "${levelGroup}_lastlevelname" -> LevelAPI.getLevelName(levelGroup, prevLvl).colored()
                    "${levelGroup}_nextlevelname" -> LevelAPI.getLevelName(levelGroup, nextLvl).colored()

                    "${levelGroup}_levelexp" -> LevelAPI.getLevelExp(levelGroup, curLvl)
                    "${levelGroup}_lastlevelexp" -> LevelAPI.getLevelExp(levelGroup, prevLvl)
                    "${levelGroup}_nextlevelexp" -> LevelAPI.getLevelExp(levelGroup, nextLvl)

                    "${levelGroup}_expprogressbar" -> createBar(
                        ConfigManager.getExpProgressBarEmpty().colored(),
                        ConfigManager.getExpProgressBarFull().colored(),
                        ConfigManager.getExpProgressBarLength(),
                        PlayerAPI.getPlayerExp(player, levelGroup).toDouble() / LevelAPI.getLevelExp(
                            levelGroup,
                            nextLvl
                        )
                    )

                    "${levelGroup}_levelprogressbar" -> createBar(
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