package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache
import com.github.cpjinan.plugin.akarilevel.database.Database
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.platform.util.onlinePlayers

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.listener
 *
 * 玩家监听器。
 *
 * @author 季楠, QwQ-dev
 * @since 2025/8/12 21:40
 */
object PlayerListener {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // 玩家进入时重新加载数据。
        val playerName = event.player.name
        MemberCache.memberCache.invalidate(playerName)
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerName = event.player.name
        submit(async = true) {
            try {
                // 玩家退出时保存数据。
                val memberData = MemberCache.memberCache[playerName]
                if (memberData != null) {
                    val json = MemberCache.gson.toJson(memberData)
                    with(Database.INSTANCE) {
                        set(memberTable, playerName, json)
                    }
                }
                MemberCache.memberCache.invalidate(playerName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        // 服务器关闭时保存在线玩家数据。
        onlinePlayers.forEach {
            val playerName = it.name
            try {
                val memberData = MemberCache.memberCache[playerName]
                if (memberData != null) {
                    val json = MemberCache.gson.toJson(memberData)
                    with(Database.INSTANCE) {
                        set(memberTable, playerName, json)
                    }
                }
                MemberCache.memberCache.invalidate(playerName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}