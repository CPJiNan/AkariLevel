package com.github.cpjinan.plugin.playerlevel

import com.github.cpjinan.plugin.playerlevel.internal.manager.DebugManager
import com.github.cpjinan.plugin.playerlevel.internal.manager.RegisterManager
import taboolib.common.platform.Plugin

//@RuntimeDependencies(RuntimeDependency("!org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0"))
object PlayerLevel : Plugin() {
//  override fun onLoad() {
//    RuntimeEnv.ENV.loadDependency(PlayerLevel.javaClass, true)
//  }

  override fun onEnable() {
    DebugManager.logoPrint()
    RegisterManager.registerAll()
  }

  override fun onDisable() {
    RegisterManager.getDatabase().save()
  }
}

