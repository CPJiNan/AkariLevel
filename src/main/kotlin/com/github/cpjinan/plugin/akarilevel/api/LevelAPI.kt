package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.internal.database.type.LevelData
import com.github.cpjinan.plugin.akarilevel.internal.database.type.LevelGroupData
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import org.bukkit.configuration.ConfigurationSection
import taboolib.common5.compileJS
import taboolib.module.chat.colored

/**
 * 等级和等级组相关 API
 * @author CPJiNan
 * @since 2024/06/23
 */
object LevelAPI {
    /**
     * 获取等级组配置列表
     * @return 等级组配置列表
     */
    fun getLevelGroups(): HashMap<String, ConfigurationSection> = getLvlGroups()

    /**
     * 获取等级组编辑名列表
     * @return 等级组名称列表
     */
    fun getLevelGroupNames(): ArrayList<String> = getLvlGroupNames()

    /**
     * 获取等级组数据列表
     * @return 等级组数据列表
     */
    fun getLevelGroupData(): HashMap<String, LevelGroupData> = getLvlGroupData()

    /**
     * 获取指定等级组数据
     * @param levelGroup 等级组编辑名
     * @return 等级组数据
     */
    fun getLevelGroupData(levelGroup: String): LevelGroupData = getLvlGroupData(levelGroup)

    /**
     * 获取指定等级组关键等级数据列表
     * @param levelGroup 等级组编辑名
     * @return 关键等级数据列表
     */
    fun getKeyLevelData(levelGroup: String): HashMap<Long, LevelData> = getKeyLvlData(levelGroup)

    /**
     * 获取指定等级组某等级数据
     * @param levelGroup 等级组编辑名
     * @param level 等级
     * @return 等级数据
     */
    fun getLevelData(levelGroup: String, level: Long): LevelData = getLvlData(levelGroup, level)

    /**
     * 获取指定等级组某等级名称
     * @param levelGroup 等级组编辑名
     * @param level 等级
     * @return 等级名称
     */
    fun getLevelName(levelGroup: String, level: Long): String = getLvlName(levelGroup, level)

    /**
     * 获取指定等级组升级到某等级所需经验
     * @param levelGroup 等级组编辑名
     * @param level 等级
     * @return 等级名称
     */
    fun getLevelExp(levelGroup: String, level: Long): Long = getLvlExp(levelGroup, level)

    /**
     * 获取指定等级组升级到某等级所需条件列表
     * @param levelGroup 等级组编辑名
     * @param level 等级
     * @return 升级条件列表
     */
    fun getLevelCondition(levelGroup: String, level: Long): List<String> = getLvlCondition(levelGroup, level)

    /**
     * 获取指定等级组升级到某等级执行动作列表
     * @param levelGroup 等级组编辑名
     * @param level 等级
     * @return 升级执行动作列表
     */
    fun getLevelAction(levelGroup: String, level: Long): List<String> = getLvlAction(levelGroup, level)

    private fun getLvlGroups(): HashMap<String, ConfigurationSection> = ConfigManager.level

    private fun getLvlGroupNames(): ArrayList<String> {
        return ArrayList(getLvlGroups().keys)
    }

    private fun getLvlGroupData(): HashMap<String, LevelGroupData> {
        return getLvlGroups().mapValues { (_, section) ->
            LevelGroupData(
                display = section.getString("Display")!!,
                subscribeSource = section.getStringList("Source.Subscribe"),
                isEnabledTrace = section.getBoolean("Trace.Enable"),
                traceCondition = section.getStringList("Trace.Condition"),
                traceAction = section.getStringList("Trace.Action"),
                maxLevel = section.getLong("Level.Max"),
                isEnabledAutoLevelup = section.getBoolean("Level.Auto-Levelup"),
                isEnabledExpLimit = section.getBoolean("Level.Exp-Limit"),
                keyLevelSettings = section.getConfigurationSection("Level.Key")!!
            )
        } as HashMap<String, LevelGroupData>
    }

    private fun getLvlGroupData(levelGroup: String): LevelGroupData {
        return getLvlGroupData()[levelGroup] ?: throw IllegalArgumentException("Unknown level group $levelGroup.")
    }

    private fun getKeyLvlData(levelGroup: String): HashMap<Long, LevelData> {
        val keyLevelSettings = getLvlGroupData(levelGroup).keyLevelSettings
        return keyLevelSettings.getKeys(false)
            .mapNotNull { key ->
                key.toLongOrNull()?.let { level ->
                    val name = keyLevelSettings.getString("$level.Name") ?: return@mapNotNull null
                    val exp = keyLevelSettings.getString("$level.Exp") ?: return@mapNotNull null
                    val condition = keyLevelSettings.getStringList("$level.Condition")
                    val action = keyLevelSettings.getStringList("$level.Action")
                    level to LevelData(name, exp, condition, action)
                }
            }.toMap(HashMap())
    }

    private fun getLvlData(levelGroup: String, level: Long): LevelData {
        var levelData: LevelData? = null
        getKeyLvlData(levelGroup).forEach { (k, v) ->
            if (level >= k) levelData = v
        }
        return levelData ?: throw IllegalArgumentException("No level data found for level $level in group $levelGroup")
    }

    private fun getLvlName(levelGroup: String, level: Long): String =
        getLvlData(levelGroup, level).name.replace("%level%", level.toString(), true).colored()

    private fun getLvlExp(levelGroup: String, level: Long): Long {
        if (level > getLvlGroupData(levelGroup).maxLevel) return Long.MAX_VALUE
        return getLvlData(levelGroup, level).let { levelData ->
            levelData.exp
                .replace("%level%", level.toString(), true)
                .compileJS()?.eval()?.toString()?.toLongOrNull() ?: return Long.MAX_VALUE
        }
    }

    private fun getLvlCondition(levelGroup: String, level: Long): List<String> = getLvlData(levelGroup, level).condition

    private fun getLvlAction(levelGroup: String, level: Long): List<String> = getLvlData(levelGroup, level).action

}