package com.github.cpjinan.plugin.akarilevel.internal.manager

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.database.getHost

object ConfigManager {
    @Config("settings.yml", autoReload = false)
    lateinit var settings: ConfigFile

    // Config Version
    const val VERSION = 3

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

    // Hook
    fun isEnabledAttribute() = settings.getBoolean("Attribute.Enable")
    fun getAttributePlugin() = settings.getString("Attribute.Plugin")!!
    fun getAttributeName() = settings.getString("Attribute.Name")!!
    fun getAttributeFormula() = settings.getString("Attribute.Formula")!!
    fun getAttributeSource() = settings.getStringList("Attribute.Source")
    fun getPlaceholderIdentifier() = settings.getString("PlaceholderAPI.Identifier")!!
    fun getExpProgressBarEmpty() = settings.getString("PlaceholderAPI.Progress-Bar.Exp.Empty")!!
    fun getExpProgressBarFull() = settings.getString("PlaceholderAPI.Progress-Bar.Exp.Full")!!
    fun getExpProgressBarLength() = settings.getInt("PlaceholderAPI.Progress-Bar.Exp.Length")
    fun getLevelProgressBarEmpty() = settings.getString("PlaceholderAPI.Progress-Bar.Level.Empty")!!
    fun getLevelProgressBarFull() = settings.getString("PlaceholderAPI.Progress-Bar.Level.Full")!!
    fun getLevelProgressBarLength() = settings.getInt("PlaceholderAPI.Progress-Bar.Level.Length")
}