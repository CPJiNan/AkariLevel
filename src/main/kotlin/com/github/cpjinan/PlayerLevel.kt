package com.github.cpjinan

import com.github.cpjinan.listener.MythicListener
import com.github.cpjinan.manager.DebugManager
import com.github.cpjinan.manager.RegisterManager
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin

object PlayerLevel : Plugin() {

    override fun onEnable() {
        DebugManager.logoPrint()
        RegisterManager.registerAll()
    }
}