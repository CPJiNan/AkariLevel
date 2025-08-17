package com.github.cpjinan.plugin.akarilevel.listener

import com.github.cpjinan.plugin.akarilevel.config.PlayerConfig
import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import com.github.cpjinan.plugin.akarilevel.manager.CacheManager.forcePersist
import com.github.cpjinan.plugin.akarilevel.manager.CacheManager.isDirty
import org.bukkit.event.player.PlayerExpChangeEvent
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
 * @author 季楠
 * @since 2025/8/12 21:40
 */
object PlayerListener {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        val playerName = event.player.name
        val amount = event.amount.toLong()
        // 为所有等级组增加此来源的经验。
        LevelGroup.getLevelGroups().values.forEach {
            it.addMemberExp(playerName, amount, "VANILLA_EXP_CHANGE")
        }
        // 如果要取消原版经验变更事件，设置经验变化量为 0。
        if (PlayerConfig.cancelVanilla) event.amount = 0
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val playerName = event.player.name
        // 自动加入等级组。
        PlayerConfig.autoJoin.forEach {
            val group = LevelGroup.getLevelGroups()[it]
            if (group != null && !group.hasMember(playerName)) {
                group.addMember(playerName, "AUTO_JOIN")
            }
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerName = event.player.name
        if (!isDirty(playerName)) return
        submit(async = true) {
            forcePersist(playerName)
        }
    }
}