package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.getConfigSections
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.getDataFolder
import java.io.File

object PluginConfig {
    var settings: YamlConfiguration = YamlConfiguration.loadConfiguration(File(getDataFolder(), "settings.yml"))
    var level: HashMap<String, ConfigurationSection> = getLevelGroups()

    // Config Version
    const val VERSION = 6

    // Options
    fun getLanguage() = settings.getString("Options.Language", "en_US")!!
    fun getConfigVersion() = settings.getInt("Options.Config-Version")
    fun isEnabledCheckUpdate() = settings.getBoolean("Options.Check-Update")
    fun isEnabledOPNotify() = settings.getBoolean("Options.OP-Notify")
    fun isEnabledSendMetrics() = settings.getBoolean("Options.Send-Metrics")
    fun isEnabledDebug() = settings.getBoolean("Options.Debug")

    // Database
    fun getMethod() = settings.getString("Database.Method")
    fun getJsonSection() = settings.getConfigurationSection("Database.JSON")!!
    fun getCborSection() = settings.getConfigurationSection("Database.CBOR")!!
    fun getSqlTable() = settings.getString("Database.SQL.table")!!
    fun isEnabledUUID() = settings.getBoolean("Database.UUID")

    // Trace
    fun getDefaultTrace() = settings.getString("Trace.Default")!!
    fun isEnabledVanilla() = settings.getBoolean("Trace.Vanilla")
    fun isEnabledAutoResetTrace() = settings.getBoolean("Trace.Auto-Reset")

    // Team
    fun isEnabledTeam() = settings.getBoolean("Team.Enable")
    fun getTeamPlugin() = settings.getString("Team.Plugin")!!
    fun getShareSource() = settings.getStringList("Team.Source")
    fun getShareTotal() = settings.getString("Team.Total")!!
    fun getShareLeaderWeight() = settings.getLong("Team.Weight.Leader")
    fun getShareMemberWeight() = settings.getLong("Team.Weight.Member")

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

    // Level
    fun getLevelGroups(): HashMap<String, ConfigurationSection> {
        val levelGroups = HashMap<String, ConfigurationSection>()
        FileUtil.getFile("plugins/AkariLevel/level", true).forEach { file ->
            if (file.name.endsWith(".yml")) {
                file.getConfigSections().forEach { (key, section) ->
                    levelGroups[key] = section
                }
            }
        }
        return levelGroups
    }
}