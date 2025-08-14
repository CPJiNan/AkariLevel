@file:Suppress("DEPRECATION")

package com.github.cpjinan.plugin.akarilevel.level

import org.bukkit.Bukkit.getOfflinePlayer
import taboolib.common5.util.replace
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored
import taboolib.module.configuration.Type
import taboolib.platform.compat.replacePlaceholder
import top.maplex.arim.Arim
import top.maplex.arim.tools.folderreader.releaseResourceFolderAndRead
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
        @JvmStatic
        fun getConfigLevelGroups(): Map<String, ConfigLevelGroup> {
            return configLevelGroups
        }

        /** 新增配置等级组 **/
        @JvmStatic
        fun addConfigLevelGroup(name: String, configLevelGroup: ConfigLevelGroup) {
            configLevelGroups[name] = configLevelGroup
        }

        /** 移除配置等级组 **/
        @JvmStatic
        fun removeConfigLevelGroup(name: String) {
            configLevelGroups.remove(name)
        }

        /** 重载配置等级组 **/
        @JvmStatic
        fun reloadConfigLevelGroups() {
            configLevelGroups.forEach { _, levelGroup ->
                levelGroup.unregister()
            }
            releaseResourceFolderAndRead("level") {
                setReadType(Type.YAML, Type.JSON)
                walk {
                    getKeys(false).forEach {
                        ConfigLevelGroup(getConfigurationSection(it)!!).register()
                    }
                }
            }
        }
    }

    override val name: String = config.name
    override val display: String = config.getString("General.Display", name)!!

    override fun getLevelName(level: Long): String {
        return getLevelConfig(level).getString("Name")?.replace("{level}" to level)?.colored() ?: "$level"
    }

    override fun getLevelExp(level: Long): Long {
        return Arim.fixedCalculator.evaluate(
            getLevelConfig(level).getString("Exp").orEmpty().replace("{level}" to level)
        ).toLong()
    }

    override fun getLevelName(member: String, level: Long): String {
        return getLevelName(level).let {
            if (member.startsWith("player:")) it.replacePlaceholder(getOfflinePlayer(member.substringAfter("player:")))
            else it
        }
    }

    override fun getLevelExp(member: String, level: Long): Long {
        return Arim.fixedCalculator.evaluate(
            getLevelConfig(level).getString("Exp").orEmpty()
                .replace("{level}" to level)
                .let {
                    if (member.startsWith("player:")) it.replacePlaceholder(getOfflinePlayer(member.substringAfter("player:")))
                    else it
                }
        ).toLong()
    }

    /** 获取等级配置 **/
    fun getLevelConfig(level: Long): ConfigurationSection {
        return getKeyLevelConfigs()
            .filter { level >= it.key }
            .maxByOrNull { it.key }
            ?.value ?: throw IllegalArgumentException()
    }

    /** 获取关键等级配置列表 **/
    fun getKeyLevelConfigs(): Map<Long, ConfigurationSection> {
        return config.getConfigurationSection("Level.Key")!!.getKeys(false)
            .associateBy(
                { it.toDoubleOrNull()?.toLong()!! },
                { config.getConfigurationSection("Level.Key.$it")!! }
            ).toSortedMap()
    }

    override fun onRegister() {
        addConfigLevelGroup(name, this)
    }

    override fun onUnregister() {
        removeConfigLevelGroup(name)
    }
}