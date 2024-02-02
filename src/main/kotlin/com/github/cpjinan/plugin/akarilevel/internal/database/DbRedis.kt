package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import taboolib.common.platform.function.console
import taboolib.expansion.AlkaidRedis
import taboolib.expansion.SingleRedisConnection
import taboolib.expansion.fromConfig

class DbRedis : Database {


    private var redisConnector = AlkaidRedis.create().fromConfig(ConfigManager.getRedisSection())

    private fun getRedisConnection(): SingleRedisConnection? {
        return try {
            redisConnector.connect().connection()
        } catch (e: Exception) {
            console().sendMessage("§c[AkariLevel] §4Redis connection failed, please check the configuration file.")
            return null
        }
    }


    override fun getPlayerByName(name: String): PlayerData {
        return try {
            val data = getRedisConnection()?.eval(
                "return redis.call('hget', KEYS[1], ARGV[1])",
                listOf("AkariLevel"),
                listOf(name)
            ) as String
            Json.decodeFromString(data)
        } catch (e: Exception) {
            PlayerData()
        }
    }

    override fun updatePlayer(name: String, value: PlayerData) {
        getRedisConnection()?.eval(
            "redis.call('hset', KEYS[1], ARGV[1], ARGV[2])",
            listOf("AkariLevel"),
            listOf(name, Json.encodeToString(value))
        )
    }

    override fun save() {
        getRedisConnection()?.close()
    }

}