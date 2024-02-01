package com.github.cpjinan.plugin.akarilevel.internal.listener

import com.github.cpjinan.plugin.akarilevel.api.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.internal.hook.Attribute
import taboolib.common.platform.event.SubscribeEvent

class AkariLevelListener {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        event.exp = Attribute().getAddition(event.player, event.exp, event.source)
    }
}