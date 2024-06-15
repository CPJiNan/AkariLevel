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
}