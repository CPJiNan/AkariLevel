package com.github.cpjinan.plugin.akarilevel.internal.manager

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import java.util.*

object ConfigManager {
    @Config("settings.yml", autoReload = false)
    lateinit var settings: ConfigFile

    // Options
    fun getConfigVersion() = settings.getInt("Options.Config-Version")
    fun isEnabledCheckUpdate() = settings.getBoolean("Options.Check-Update")
    fun isEnabledSendMetrics() = settings.getBoolean("Options.Send-Metrics")
    fun isEnabledDebug() = settings.getBoolean("Options.Debug")

    // Database
    fun getMethod() = settings.getString("Database.Method")
    fun getJsonSection() = settings.getConfigurationSection("Database.JSON")!!
    fun getCborSection() = settings.getConfigurationSection("Database.CBOR")!!
    fun getSqlSection() = settings.getConfigurationSection("Database.SQL")!!

    // Hook
    fun isEnabledMythicMobs() = settings.getBoolean("Hook.MythicMobs.Enabled")
    fun isEnabledAttribute() = settings.getBoolean("Hook.Attribute.Enabled")
    fun getMythicMobsExpDropName() = settings.getString("Hook.MythicMobs.Drop-Name")!!
    fun getAttributePlugin() = settings.getString("Hook.Attribute.Plugin")!!
    fun getAttributeName() = settings.getString("Hook.Attribute.Name")!!
    fun getAttributeFormula() = settings.getString("Hook.Attribute.Formula")!!
    fun getAttributeSource() = settings.getStringList("Hook.Attribute.Source")
    fun getPlaceholderIdentifier() = settings.getString("Hook.PlaceholderAPI.Identifier")!!

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