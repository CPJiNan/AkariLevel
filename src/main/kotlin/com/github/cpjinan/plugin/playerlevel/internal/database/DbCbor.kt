package com.github.cpjinan.plugin.playerlevel.internal.database

import com.github.cpjinan.plugin.playerlevel.internal.database.types.Player
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.bukkit.Bukkit
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class DbCbor(filePath: String) : Database {
  private val file: File
  private val playerData: HashMap<String, Player>

  init {
    val parent = Bukkit.getPluginManager().getPlugin("PlayerLevel")?.dataFolder ?: File(".")
    file = File(parent, filePath)
    playerData = if (file.exists()) {
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

  override fun getPlayerByName(name: String): Player = playerData[name] ?: Player()

  override fun updatePlayer(name: String, value: Player) {
    playerData[name] = value
  }

  override fun save() {
    if (!file.exists()) file.createNewFile()

    file.writeBytes(Cbor.encodeToByteArray(playerData))
  }
}