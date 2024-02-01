package com.github.cpjinan.plugin.akarilevel.internal.manager

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import org.bukkit.entity.Player
import taboolib.common5.compileJS

object LevelManager {
    fun getRequireExp(player: Player): Int {
        val level = AkariLevelAPI.getPlayerLevel(player)
        if (level >= ConfigManager.getMaxLevel()) return Int.MAX_VALUE
        return getLevelData(level + 1)?.let { levelData ->
            levelData.exp
                .replace("%level%", level.toString(), true)
                .compileJS()?.eval()?.toString()?.toIntOrNull() ?: return Int.MAX_VALUE
        } ?: Int.MAX_VALUE
    }

    private fun getLevelData(level: Int): LevelData? {
        var levelData: LevelData? = null
        ConfigManager.getLevelData().forEach { (k, v) ->
            if (level >= k) levelData = v
        }
        return levelData
    }
}

data class LevelData(val name: String, val exp: String, val condition: List<String>, val action: List<String>)