package com.github.cpjinan.plugin.akarilevel.internal.manager

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.database.getHost
import java.util.*

object ConfigManager {
    @Config("settings.yml", autoReload = false)
    lateinit var settings: ConfigFile

    // Config Version
    const val VERSION = 2

    // Config initialization
    @Config("lang/settings/zh_CN.yml", autoReload = false)
    lateinit var settings_zh_CN: ConfigFile

    @Config("lang/settings/en_US.yml", autoReload = false)
    lateinit var settings_en_US: ConfigFile

    @Config("lang/level/zh_CN.yml", autoReload = false)
    lateinit var level_zh_CN: ConfigFile

    @Config("lang/level/en_US.yml", autoReload = false)
    lateinit var level_en_US: ConfigFile

    // Options
    fun getConfigVersion() = settings.getInt("Options.Config-Version")
    fun isEnabledCheckUpdate() = settings.getBoolean("Options.Check-Update")
    fun isEnabledSendMetrics() = settings.getBoolean("Options.Send-Metrics")
    fun isEnabledDebug() = settings.getBoolean("Options.Debug")

    // Database
    fun getMethod() = settings.getString("Database.Method")
    fun getJsonSection() = settings.getConfigurationSection("Database.JSON")!!
    fun getCborSection() = settings.getConfigurationSection("Database.CBOR")!!
    fun getSqlHost() = settings.getHost("Database.SQL")
    fun getSqlTable() = settings.getString("Database.SQL.table")!!
    fun isEnabledRedis() = settings.getBoolean("Database.REDIS.enable")
    fun getRedisSection() = settings.getConfigurationSection("Database.REDIS")!!

    // Hook
    fun isEnabledMythicMobs() = settings.getBoolean("Hook.MythicMobs.Enable")
    fun isEnabledAttribute() = settings.getBoolean("Hook.Attribute.Enable")
    fun getMythicMobsExpDropName() = settings.getString("Hook.MythicMobs.Drop-Name")!!
    fun getAttributePlugin() = settings.getString("Hook.Attribute.Plugin")!!
    fun getAttributeName() = settings.getString("Hook.Attribute.Name")!!
    fun getAttributeFormula() = settings.getString("Hook.Attribute.Formula")!!
    fun getAttributeSource() = settings.getStringList("Hook.Attribute.Source")
    fun getPlaceholderIdentifier() = settings.getString("Hook.PlaceholderAPI.Identifier")!!
    fun getExpProgressBarEmpty() = settings.getString("Hook.PlaceholderAPI.Progress-Bar.Exp.Empty")!!
    fun getExpProgressBarFull() = settings.getString("Hook.PlaceholderAPI.Progress-Bar.Exp.Full")!!
    fun getExpProgressBarLength() = settings.getInt("Hook.PlaceholderAPI.Progress-Bar.Exp.Length")
    fun getLevelProgressBarEmpty() = settings.getString("Hook.PlaceholderAPI.Progress-Bar.Level.Empty")!!
    fun getLevelProgressBarFull() = settings.getString("Hook.PlaceholderAPI.Progress-Bar.Level.Full")!!
    fun getLevelProgressBarLength() = settings.getInt("Hook.PlaceholderAPI.Progress-Bar.Level.Length")

    @Config("level.yml", autoReload = false)
    lateinit var levelConfig: ConfigFile

    // Level
    fun getMaxLevel() = levelConfig.getInt("Max-Level")
    fun getLevelData(): TreeMap<Int, LevelData> {
        val map = TreeMap<Int, LevelData>()
        levelConfig.getConfigurationSection("Settings")?.getKeys(false)?.forEach { key ->
            val level = key.toIntOrNull() ?: return@forEach
            val name = levelConfig.getString("Settings.${key.toIntOrNull()}.Name") ?: return@forEach
            val exp = levelConfig.getString("Settings.${key.toIntOrNull()}.Exp") ?: return@forEach
            val condition = levelConfig.getStringList("Settings.${key.toIntOrNull()}.Condition")
            val action = levelConfig.getStringList("Settings.${key.toIntOrNull()}.Action")
            map[level] = LevelData(name, exp, condition, action)
        }
        return map
    }
}