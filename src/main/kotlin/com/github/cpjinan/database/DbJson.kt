package com.github.cpjinan.database

import com.github.cpjinan.database.types.Player
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import java.io.File

class DbJson(filePath: String) : Database {
  private val file: File
  private val playerData: HashMap<String, Player>

  init {
    val parent = Bukkit.getPluginManager().getPlugin("PlayerLevel")?.dataFolder ?: File(".")
    file = File(parent, filePath)
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

  override fun getPlayerByName(name: String): Player = playerData[name] ?: Player()

  override fun updatePlayer(name: String, value: Player) {
    playerData[name] = value
  }

  override fun save() {
    if (!file.exists()) file.createNewFile()

    file.writeText(Json.encodeToString(playerData), Charsets.UTF_8)
  }
}