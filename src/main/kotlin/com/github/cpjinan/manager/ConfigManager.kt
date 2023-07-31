package com.github.cpjinan.manager

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

object ConfigManager {
    private const val configFile = "config.yml"
    private const val levelFile = "level.yml"
    private const val dataFile = "database.yml"

    @Config(configFile, autoReload = true)
    lateinit var config : Configuration
    @ConfigNode("options", configFile)
    lateinit var options: ConfigurationSection

    @Config(levelFile, autoReload = true)
    lateinit var levelConfig : Configuration
    @ConfigNode("level", levelFile)
    lateinit var level: ConfigurationSection

    @Config(dataFile, autoReload = true)
    lateinit var dataConfig : Configuration
    @ConfigNode("player", dataFile)
    lateinit var player: ConfigurationSection

}