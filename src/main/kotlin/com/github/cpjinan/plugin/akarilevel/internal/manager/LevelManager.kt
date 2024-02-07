package com.github.cpjinan.plugin.akarilevel.internal.manager

import taboolib.common5.compileJS
import taboolib.module.chat.colored

object LevelManager {
    fun getName(level: Int): String? = getLevelData(level)?.name?.replace("%level%", level.toString(), true)?.colored()

    fun getExp(level: Int): Int {
        if (level > ConfigManager.getMaxLevel()) return Int.MAX_VALUE
        return getLevelData(level)?.let { levelData ->
            levelData.exp
                .replace("%level%", level.toString(), true)
                .compileJS()?.eval()?.toString()?.toIntOrNull() ?: return Int.MAX_VALUE
        } ?: Int.MAX_VALUE
    }

    fun getCondition(level: Int): List<String>? = getLevelData(level)?.condition

    fun getAction(level: Int): List<String>? = getLevelData(level)?.action

    private fun getLevelData(level: Int): LevelData? {
        var levelData: LevelData? = null
        ConfigManager.getLevelData().forEach { (k, v) ->
            if (level >= k) levelData = v
        }
        return levelData
    }
}

data class LevelData(val name: String, val exp: String, val condition: List<String>, val action: List<String>)