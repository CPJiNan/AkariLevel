package com.github.cpjinan.plugin.akarilevel.internal.manager

import com.github.cpjinan.plugin.akarilevel.utils.FileUtil
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ConfigManager {
    var settings: YamlConfiguration = YamlConfiguration.loadConfiguration(File(FileUtil.dataFolder, "settings.yml"))

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
    fun getSqlTable() = settings.getString("Database.SQL.table")!!

    // Trace
    fun getDefaultTrace() = settings.getString("Trace.Default")!!
    fun isEnabledAutoResetTrace() = settings.getBoolean("Trace.Auto-Reset")


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