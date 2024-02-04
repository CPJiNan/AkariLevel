package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import java.io.File

class DbJson : Database {
    private val file: File
    private val playerData: HashMap<String, PlayerData>

    init {
        val parent = Bukkit.getPluginManager().getPlugin("AkariLevel")?.dataFolder ?: File(".")
        file = File(parent, ConfigManager.getJsonSection().getString("file")!!)
        playerData = if (file.exists()) {
            val content = file.readText(Charsets.UTF_8)
            if (content.isNotBlank()) {
                Json.decodeFromString(content)
            } else {
                hashMapOf()
            }
        } else {
            hashMapOf()
        }
    }

    override fun getPlayerByName(name: String): PlayerData = playerData[name] ?: PlayerData()

    override fun updatePlayer(name: String, value: PlayerData) {
        playerData[name] = value
    }

    override fun save() {
        if (!file.exists()) file.createNewFile()

        file.writeText(Json.encodeToString(playerData), Charsets.UTF_8)
    }
}