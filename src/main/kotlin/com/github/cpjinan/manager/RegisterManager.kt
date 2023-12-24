package com.github.cpjinan.manager

import com.github.cpjinan.database.Database
import com.github.cpjinan.database.DbJson
import com.github.cpjinan.listener.MythicListener
import org.bukkit.Bukkit
import taboolib.common.platform.Platform
import taboolib.common.platform.function.info
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin
import java.net.HttpURLConnection
import java.net.URL

object RegisterManager {
  private var database: Database? = null

  /**
   * 快捷注册方法
   */
  fun registerAll() {
    registerMetrics()
    registerUpdate()
    registerMythicMobs()
    registerUrl()
    database = openDatabase()
  }

  private fun openDatabase(): Database {
    val dbType = ConfigManager.options.getString("database.type") ?: "JSON"
    val dbUri = ConfigManager.options.getString("database.uri")
    return when (dbType) {
      "JSON" -> {
        DbJson(dbUri ?: "database.json")
      }

      else -> {
        throw IllegalArgumentException("unknown dbType")
      }
    }
  }

  /**
   * bStats统计注册方法
   */
  private fun registerMetrics() {
    if (ConfigManager.options.getBoolean("metrics")) Metrics(
      18992,
      BukkitPlugin.getInstance().description.version,
      Platform.BUKKIT
    )
  }

  /**
   * 网页读取注册方法
   */
  private fun registerUrl() {
    Thread {
      val urlConnection =
        URL("https://cpjinan.github.io/Pages/PlayerLevel/notice.html").openConnection() as HttpURLConnection
      try {
        val message = urlConnection.inputStream.bufferedReader().readText()
        if (message.length > 2) info(message)
      } catch (_: java.net.ConnectException) {
      } finally {
        urlConnection.disconnect()
      }
    }.start()
  }

  /**
   * 输出插件更新提示方法
   */
  private fun registerUpdate() {
    if (ConfigManager.options.getBoolean("update")) {
      Thread {
        val urlConnection =
          URL("https://cpjinan.github.io/Pages/PlayerLevel/version.html").openConnection() as HttpURLConnection
        try {
          val latestVersion = urlConnection.inputStream.bufferedReader().readText()
          val version = BukkitPlugin.getInstance().description.version
          if (latestVersion != version) {
            info("发现了一个新的PlayerLevel版本！")
            info("最新版本: $latestVersion")
            info("当前版本: $version")
            info("请加QQ群704109949以获取插件最新版本...")
          }
        } catch (_: java.net.ConnectException) {
        } finally {
          urlConnection.disconnect()
        }
      }.start()
    }
  }

  private fun registerMythicMobs() {
    if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
      Bukkit.getPluginManager().getPlugin("PlayerLevel")
        ?.let { Bukkit.getPluginManager().registerEvents(MythicListener, it) }
    }
  }

  fun getDatabase(): Database = if (database != null) {
    database!!
  } else {
    database = openDatabase()
    database!!
  }
}