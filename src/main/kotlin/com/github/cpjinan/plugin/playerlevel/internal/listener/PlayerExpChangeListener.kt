package com.github.cpjinan.plugin.playerlevel.internal.listener

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import com.github.cpjinan.plugin.playerlevel.internal.manager.ConfigManager
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import kotlin.math.roundToInt

object PlayerExpChangeListener {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        val exp: Double = event.amount * ConfigManager.options.getDouble("exp-conversion-rate")
        LevelAPI.addPlayerExp(event.player, exp.roundToInt(), "PLAYER_EXP_CHANGE_LISTENER")
        LevelAPI.refreshPlayerLevel(event.player, "PLAYER_EXP_CHANGE_LISTENER")
        event.amount = 0
    }

}