package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache
import com.github.cpjinan.plugin.akarilevel.database.Database
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

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
        val uuid = event.player.uniqueId.toString()
        MemberCache.memberCache.invalidate(uuid)
        submit(async = true, delay = 50) {
            MemberCache.memberCache.invalidate(uuid)
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId.toString()
        submit(async = true) {
            try {
                // 玩家退出时强制保存数据。
                val memberData = MemberCache.memberCache[uuid]
                if (memberData != null) {
                    val json = MemberCache.gson.toJson(memberData)
                    with(Database.INSTANCE) {
                        set(memberTable, uuid, json)
                    }
                }
                MemberCache.memberCache.invalidate(uuid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}