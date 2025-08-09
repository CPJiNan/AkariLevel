package com.github.cpjinan.plugin.akarilevel.level

import taboolib.common5.util.replace
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored
import top.maplex.arim.Arim
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.level
 *
 * [LevelGroup] 接口的实现，从配置文件获取等级组。
 *
 * @author 季楠
 * @since 2025/8/7 23:15
 */
class ConfigLevelGroup(val config: ConfigurationSection) : LevelGroup {
    companion object {
        private var configLevelGroups: MutableMap<String, ConfigLevelGroup> = ConcurrentHashMap()

        /** 获取配置等级组列表 **/
        fun getConfigLevelGroups(): Map<String, ConfigLevelGroup> {
            return configLevelGroups
        }

        /** 注册配置等级组 **/
        fun registerConfigLevelGroup(name: String, configLevelGroup: ConfigLevelGroup) {
            configLevelGroups[name] = configLevelGroup
        }

        /** 取消注册配置等级组 **/
        fun unregisterConfigLevelGroup(name: String) {
            configLevelGroups.remove(name)
        }
    }

    override val name: String = config.name
    override val display: String = config.getString("General.Display", name)!!
    override val members: MutableList<String> = mutableListOf()

    override fun getLevelName(level: Long): String {
        return getLevelConfig(level).getString("Name")?.replace("{level}" to level)?.colored() ?: "$level"
    }

    override fun getLevelExp(level: Long): Long {
        return Arim.fixedCalculator.evaluate(
            getLevelConfig(level).getString("Exp").orEmpty().replace("{level}" to level)
        ).toLong()
    }

    /** 获取等级配置 **/
    fun getLevelConfig(level: Long): ConfigurationSection {
        return getKeyLevelConfig()
            .filter { level >= it.key }
            .maxByOrNull { it.key }
            ?.value ?: throw IllegalArgumentException()
    }

    /** 获取关键等级配置 **/
    fun getKeyLevelConfig(): Map<Long, ConfigurationSection> {
        return config.getConfigurationSection("Level.Key")!!.getKeys(false)
            .associateBy(
                { it.toDoubleOrNull()?.toLong()!! },
                { config.getConfigurationSection("Level.Key.$it")!! }
            ).toSortedMap()
    }

    override fun onRegister(onCancel: (Boolean) -> Unit) {
        registerConfigLevelGroup(name, this)
    }

    override fun onUnregister(onCancel: (Boolean) -> Unit) {
        unregisterConfigLevelGroup(name)
    }
}