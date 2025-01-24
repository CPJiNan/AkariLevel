package com.github.cpjinan.plugin.akarilevel

import taboolib.common.LifeCycle
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.common.platform.function.registerLifeCycleTask

@PlatformSide(Platform.BUKKIT)
object AkariLevelPlugin : Plugin() {
    init {
        registerLifeCycleTask(LifeCycle.INIT) {
            try {
                DefaultAkariLevelLoader.startup()
            } catch (ex: Throwable) {
                ex.printStackTrace()
                disablePlugin()
            }
        }
    }
}