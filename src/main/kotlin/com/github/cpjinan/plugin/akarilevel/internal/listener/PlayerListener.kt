package com.github.cpjinan.plugin.akarilevel.internal.listener

import com.github.cpjinan.plugin.akarilevel.api.DataAPI
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig.getDefaultTrace
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig.isEnabledAutoResetTrace
import com.github.cpjinan.plugin.akarilevel.common.PluginUpdate
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerListener {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        LevelAPI.getLevelGroupNames().forEach {
            PlayerAPI.addPlayerExp(event.player, it, event.amount.toLong(), "VANILLA_EXP_CHANGE")
            PlayerAPI.refreshPlayerLevel(event.player, it)
        }
        if (!PluginConfig.isEnabledVanilla()) event.amount = 0
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerAPI.refreshPlayerLevel(event.player)
        if (isEnabledAutoResetTrace()) PlayerAPI.setPlayerTraceLevelGroup(event.player, getDefaultTrace())

        if (event.player.isOp && PluginConfig.isEnabledOPNotify()) PluginUpdate.sendPlayerUpdateNotify(event.player)
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onPlayerQuit(event: PlayerQuitEvent) {
        DataAPI.saveData()
    }
}