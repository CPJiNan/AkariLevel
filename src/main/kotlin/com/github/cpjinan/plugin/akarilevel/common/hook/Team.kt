package com.github.cpjinan.plugin.akarilevel.common.hook

import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig.getShareLeaderWeight
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig.getShareMemberWeight
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig.getShareTotal
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import org.bukkit.Bukkit
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.compileJS
import taboolib.platform.compat.replacePlaceholder
import kotlin.math.roundToLong

object Team {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        val eventPlayer = event.player
        if (PluginConfig.isEnabledTeam() && Bukkit.getServer().pluginManager.isPluginEnabled(PluginConfig.getTeamPlugin()) && event.source in PluginConfig.getShareSource()) {
            when (PluginConfig.getTeamPlugin()) {
                "DungeonPlus" -> {
                    val team = DungeonPlus.teamManager.getTeam(eventPlayer) ?: return
                    val totalAmount =
                        getShareTotal()
                            .replace("%exp%", event.expAmount.toString(), true)
                            .replace("%size%", team.players.size.toString(), true)
                            .replacePlaceholder(eventPlayer)
                            .compileJS()?.eval()?.toString()?.toDouble()?.roundToLong() ?: event.expAmount
                    val totalWeight =
                        getShareLeaderWeight() + (team.players.size - 1) * getShareMemberWeight()
                    val leaderAmount =
                        (totalAmount * (getShareLeaderWeight().toDouble() / totalWeight)).roundToLong()
                    val memberAmount =
                        (totalAmount * (getShareMemberWeight().toDouble() / totalWeight)).roundToLong()
                    team.players.forEach {
                        if (it == event.player.uniqueId) {
                            event.expAmount =
                                if (it == team.leader) leaderAmount
                                else memberAmount
                        } else {
                            val player = Bukkit.getPlayer(it) ?: return@forEach
                            PlayerAPI.addPlayerExp(
                                player,
                                event.levelGroup,
                                if (player.uniqueId == team.leader) leaderAmount
                                else memberAmount,
                                "TEAM_SHARE_EXP"
                            )
                        }
                    }
                }

                else -> throw IllegalArgumentException("Unsupported team plugin ${PluginConfig.getTeamPlugin()}.")
            }
        }
    }
}