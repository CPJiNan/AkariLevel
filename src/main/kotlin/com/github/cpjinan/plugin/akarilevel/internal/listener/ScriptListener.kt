package com.github.cpjinan.plugin.akarilevel.internal.listener

import com.github.cpjinan.plugin.akarilevel.api.ScriptAPI
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelChangeEvent
import com.github.cpjinan.plugin.akarilevel.utils.script.Kether.evalKether
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitPlugin

object ScriptListener {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        ScriptAPI.getScriptSections().forEach { (_, section) ->
            when (section.getString("Event")) {
                "PlayerExpChangeEvent" -> {
                    section.getStringList("Kether").evalKether(
                        event.player,
                        listOf(BukkitPlugin.getInstance().name),
                        hashMapOf(
                            "%player%" to event.player.name,
                            "%levelGroup%" to event.levelGroup,
                            "%expAmount%" to event.expAmount,
                            "%source%" to event.source
                        )
                    )
                }
            }
        }
    }

    @SubscribeEvent
    fun onPlayerLevelChange(event: PlayerLevelChangeEvent) {
        ScriptAPI.getScriptSections().forEach { (_, section) ->
            when (section.getString("Event")) {
                "PlayerLevelChangeEvent" -> {
                    section.getStringList("Kether").evalKether(
                        event.player,
                        listOf(BukkitPlugin.getInstance().name),
                        hashMapOf(
                            "%player%" to event.player.name,
                            "%levelGroup%" to event.levelGroup,
                            "%oldLevel%" to event.oldLevel,
                            "%newLevel%" to event.newLevel,
                            "%source%" to event.source
                        )
                    )
                }
            }
        }
    }
}