package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import java.io.File

class DbJson : Database {
    private val file: File
    private val database: HashMap<String, HashMap<String, HashMap<String, String>>>

    init {
        val parent = Bukkit.getPluginManager().getPlugin(plugin.name)?.dataFolder ?: File(".")
        file = File(parent, PluginConfig.getJsonSection().getString("file")!!)
        database = if (file.exists()) {
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

    override fun getValue(table: String, index: String, key: String) = database[table]?.get(index)?.get(key).orEmpty()

    override fun setValue(table: String, index: String, key: String, value: String) {
        val tableMap = database.getOrPut(table) { HashMap() }
        val indexMap = tableMap.getOrPut(index) { HashMap() }
        indexMap[key] = value
        save()
    }

    override fun save() {
        if (!file.exists()) file.createNewFile()
        file.writeText(Json.encodeToString(database), Charsets.UTF_8)
    }
}