package com.github.cpjinan.plugin.playerlevel.internal.manager

import com.github.cpjinan.plugin.playerlevel.internal.database.Database
import com.github.cpjinan.plugin.playerlevel.internal.database.DbCbor
import com.github.cpjinan.plugin.playerlevel.internal.database.DbJson
import com.github.cpjinan.plugin.playerlevel.internal.listener.Mythic4Listener
import com.github.cpjinan.plugin.playerlevel.internal.listener.Mythic5Listener
import org.bukkit.Bukkit
import taboolib.common.platform.Platform
import taboolib.common.platform.function.info
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin
import java.net.HttpURLConnection
import java.net.URL

/**
 * 注册管理器
 * @author CPJiNan
 * @date 2024/01/06
 */
object RegisterManager {
    private var database: Database? = null

    /**
     * 快捷注册
     */
    fun registerAll() {
        registerMetrics()
        registerUpdate()
        registerMythicMobs()
        registerUrl()
        database = openDatabase()
    }

    /**
     * 数据存储注册
     * @return [Database]
     */
    private fun openDatabase(): Database {
        val dbType = ConfigManager.options.getString("database.type") ?: "JSON"
        val dbUri = ConfigManager.options.getString("database.uri")
        return when (dbType) {
            "JSON" -> {
                DbJson(dbUri ?: "database.json")
            }

            "CBOR" -> {
                DbCbor(dbUri ?: "database.cbor")
            }

            else -> {
                throw IllegalArgumentException("unknown dbType")
            }
        }
    }

    /**
     * bStats统计注册
     */
    private fun registerMetrics() {
        if (ConfigManager.options.getBoolean("metrics")) Metrics(
            18992,
            BukkitPlugin.getInstance().description.version,
            Platform.BUKKIT
        )
    }

    /**
     * 网页读取注册
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
     * 输出插件更新提示
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

    /**
     * Mythicmobs插件拓展注册
     */
    private fun registerMythicMobs() {
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            when(Bukkit.getPluginManager().getPlugin("MythicMobs")?.description?.version?.get(0)){
                '4' -> Bukkit.getPluginManager().getPlugin("PlayerLevel")?.let { Bukkit.getPluginManager().registerEvents(Mythic4Listener, it) }
                '5' -> Bukkit.getPluginManager().getPlugin("PlayerLevel")?.let { Bukkit.getPluginManager().registerEvents(Mythic5Listener, it) }
            }
        }
    }

    /**
     * 获取数据库
     * @return [Database]
     */
    fun getDatabase(): Database = if (database != null) {
        database!!
    } else {
        database = openDatabase()
        database!!
    }
}