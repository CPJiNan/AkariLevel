package com.github.cpjinan.plugin.playerlevel.internal.manager

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * 配置管理器
 * @author CPJiNan
 * @date 2024/01/06
 */
object ConfigManager {
    @Config("config.yml", autoReload = true)
    lateinit var config: Configuration

    @ConfigNode("options", "config.yml")
    lateinit var options: ConfigurationSection

    @Config("level.yml", autoReload = true)
    lateinit var levelConfig: Configuration

    @ConfigNode("level", "level.yml")
    lateinit var level: ConfigurationSection

    fun getMaxLevel() = level.getInt("max-level")
    fun getLevelExp(lvl: Int) = level.getInt("$lvl.exp")
    fun getLevelName(lvl: Int) = level.getString("$lvl.name")
    fun getLevelChangeAction(lvl: Int) = level.getStringList("$lvl.action")
}