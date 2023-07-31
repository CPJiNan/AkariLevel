package com.github.cpjinan

import com.github.cpjinan.manager.DebugManager
import com.github.cpjinan.manager.RegisterManager
import taboolib.common.platform.Plugin

object PlayerLevel : Plugin() {

    override fun onEnable() {
        DebugManager.logoPrint()
        RegisterManager.registerAll()
    }
}