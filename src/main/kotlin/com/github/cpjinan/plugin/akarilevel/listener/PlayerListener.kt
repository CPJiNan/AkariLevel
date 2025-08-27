package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache
import com.github.cpjinan.plugin.akarilevel.database.Database
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
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerName = event.player.name
        submit(async = true) {
            try {
                // 玩家退出时强制保存数据。
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