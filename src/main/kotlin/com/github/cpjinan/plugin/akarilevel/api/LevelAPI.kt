package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.internal.database.type.LevelData
import com.github.cpjinan.plugin.akarilevel.internal.database.type.LevelGroupData
import com.github.cpjinan.plugin.akarilevel.utils.ConfigUtil.getConfigSections
import com.github.cpjinan.plugin.akarilevel.utils.FileUtil
import org.bukkit.configuration.ConfigurationSection
import taboolib.common5.compileJS
import taboolib.module.chat.colored

object LevelAPI {
    // region function
    fun getLevelGroups(): HashMap<String, ConfigurationSection> = getLvlGroups()

    fun getLevelGroupNames(): ArrayList<String> = getLvlGroupNames()

    fun getLevelGroupData(): HashMap<String, LevelGroupData> = getLvlGroupData()

    fun getLevelGroupData(levelGroup: String): LevelGroupData = getLvlGroupData(levelGroup)

    fun getKeyLevelData(levelGroup: String): HashMap<Int, LevelData> = getKeyLvlData(levelGroup)

    fun getLevelData(levelGroup: String, level: Int): LevelData = getLvlData(levelGroup, level)

    fun getLevelName(levelGroup: String, level: Int): String = getLvlName(levelGroup, level)

    fun getLevelExp(levelGroup: String, level: Int): Int = getLvlExp(levelGroup, level)

    fun getLevelCondition(levelGroup: String, level: Int): List<String> = getLvlCondition(levelGroup, level)

    fun getLevelAction(levelGroup: String, level: Int): List<String> = getLvlAction(levelGroup, level)

    // region basic function
    private fun getLvlGroups(): HashMap<String, ConfigurationSection> {
        val levelGroups = HashMap<String, ConfigurationSection>()
        FileUtil.getFile("plugins/AkariLevel/level", true).forEach { file ->
            if (file.name.endsWith(".yml")) {
                file.getConfigSections().forEach { (key, section) ->
                    levelGroups[key] = section
                }
            }
        }
        return levelGroups
    }

    private fun getLvlGroupNames(): ArrayList<String> {
        return ArrayList(getLvlGroups().keys)
    }

    private fun getLvlGroupData(): HashMap<String, LevelGroupData> {
        return getLvlGroups().mapValues { (_, section) ->
            LevelGroupData(
                display = section.getString("Display")!!,
                subscribeSource = section.getStringList("Source.Subscribe"),
                sourceFormula = "Source.Formula",
                isEnabledTrace = section.getBoolean("Trace.Enable"),
                traceCondition = section.getStringList("Trace.Condition"),
                traceAction = section.getStringList("Trace.Action"),
                maxLevel = section.getInt("Level.Max"),
                isEnabledAutoLevelup = section.getBoolean("Level.Auto-Levelup"),
                isEnabledExpLimit = section.getBoolean("Level.Exp-Limit"),
                keyLevelSettings = section.getConfigurationSection("Level.Key")!!
            )
        } as HashMap<String, LevelGroupData>
    }

    private fun getLvlGroupData(levelGroup: String): LevelGroupData {
        return getLvlGroupData()[levelGroup] ?: throw IllegalArgumentException("Unknown level group $levelGroup.")
    }

    private fun getKeyLvlData(levelGroup: String): HashMap<Int, LevelData> {
        val keyLevelSettings = getLvlGroupData(levelGroup).keyLevelSettings
        return keyLevelSettings.getKeys(false)
            .mapNotNull { key ->
                key.toIntOrNull()?.let { level ->
                    val name = keyLevelSettings.getString("$level.Name") ?: return@mapNotNull null
                    val exp = keyLevelSettings.getString("$level.Exp") ?: return@mapNotNull null
                    val condition = keyLevelSettings.getStringList("$level.Condition")
                    val action = keyLevelSettings.getStringList("$level.Action")
                    level to LevelData(name, exp, condition, action)
                }
            }.toMap(HashMap())
    }

    private fun getLvlData(levelGroup: String, level: Int): LevelData {
        var levelData: LevelData? = null
        getKeyLvlData(levelGroup).forEach { (k, v) ->
            if (level >= k) levelData = v
        }
        return levelData ?: throw IllegalArgumentException("No level data found for level $level in group $levelGroup")
    }

    private fun getLvlName(levelGroup: String, level: Int): String =
        getLvlData(levelGroup, level).name.replace("%level%", level.toString(), true).colored()

    private fun getLvlExp(levelGroup: String, level: Int): Int {
        if (level > getLvlGroupData(levelGroup).maxLevel) return Int.MAX_VALUE
        return getLvlData(levelGroup, level).let { levelData ->
            levelData.exp
                .replace("%level%", level.toString(), true)
                .compileJS()?.eval()?.toString()?.toIntOrNull() ?: return Int.MAX_VALUE
        }
    }

    private fun getLvlCondition(levelGroup: String, level: Int): List<String> = getLvlData(levelGroup, level).condition

    private fun getLvlAction(levelGroup: String, level: Int): List<String> = getLvlData(levelGroup, level).action

}