package com.github.cpjinan.manager

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

object ConfigManager {
  private const val configFile = "config.yml"
  private const val levelFile = "level.yml"

  @Config(configFile, autoReload = true)
  lateinit var config: Configuration

  @ConfigNode("options", configFile)
  lateinit var options: ConfigurationSection

  @Config(levelFile, autoReload = true)
  lateinit var levelConfig: Configuration

  @ConfigNode("level", levelFile)
  private lateinit var level: ConfigurationSection

  fun getMaxLevel() = level.getInt("max-level")

  fun getLevelExp(lvl: Int) = level.getInt("$lvl.exp")

  fun getLevelName(lvl: Int) = level.getString("$lvl.name")

  fun getLevelChangeAction(lvl: Int) = level.getStringList("$lvl.action")
}