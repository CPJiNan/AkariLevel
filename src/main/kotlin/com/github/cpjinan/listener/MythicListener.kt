package com.github.cpjinan.listener

import com.github.cpjinan.util.MythicExpDrop
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MythicListener : Listener {
    @EventHandler
    fun onRegisterDrop(event: MythicDropLoadEvent) {
        if (event.dropName.equals("PlayerExp", ignoreCase = true)) {
            event.register(MythicExpDrop(event.container.configLine, event.config))
        }
    }
}