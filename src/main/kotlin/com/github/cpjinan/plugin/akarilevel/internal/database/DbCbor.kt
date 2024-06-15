package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.bukkit.Bukkit
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class DbCbor : Database {
    private val file: File
    private val database: HashMap<String, HashMap<String, HashMap<String, String>>>

    init {
        val parent = Bukkit.getPluginManager().getPlugin(plugin.name)?.dataFolder ?: File(".")
        file = File(parent, ConfigManager.getCborSection().getString("file")!!)
        database = if (file.exists()) {
            val content = file.readBytes()
            if (content.isNotEmpty()) {
                Cbor.decodeFromByteArray(content)
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
        file.writeBytes(Cbor.encodeToByteArray(database))
    }
}