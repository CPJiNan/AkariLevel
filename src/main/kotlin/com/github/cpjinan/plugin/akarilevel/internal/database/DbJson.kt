package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependency
import java.io.File

@RuntimeDependency(
    value = "com.google.code.gson:gson:2.12.1",
    test = "com.google.gson.Gson",
    relocate = ["!com.google.gson", "!com.github.cpjinan.plugin.akarilevel.gson"]
)
class DbJson : Database {
    private val file: File
    private val database: HashMap<String, HashMap<String, HashMap<String, String>>>

    init {
        val parent = Bukkit.getPluginManager().getPlugin(plugin.name)?.dataFolder ?: File(".")
        file = File(parent, PluginConfig.getJsonSection().getString("file")!!)
        database = if (file.exists()) {
            val content = file.readText(Charsets.UTF_8)
            if (content.isNotBlank()) {
                val type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>>() {}.type
                Gson().fromJson(content, type)
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
        val jsonContent = Gson().toJson(database)
        file.writeText(jsonContent, Charsets.UTF_8)
    }
}