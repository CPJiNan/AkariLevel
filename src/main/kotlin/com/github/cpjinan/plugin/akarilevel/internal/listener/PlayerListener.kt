package com.github.cpjinan.plugin.akarilevel.internal.listener

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import kotlin.math.roundToInt

object PlayerListener {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        val exp: Double = event.amount * ConfigManager.settings.getDouble("Level.Vanilla-Exp-Rate")
        AkariLevelAPI.addPlayerExp(event.player, exp.roundToInt(), "VANILLA_EXP_CHANGE")
        AkariLevelAPI.refreshPlayerLevel(event.player, "VANILLA_EXP_CHANGE")
        // Exp 已通过 AkariExp 的方式给予玩家, 无需再次添加
        event.amount = 0
    }
}