package com.github.cpjinan.plugin.akarilevel

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import java.io.File

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/15 14:20
 */
@ConfigNode(bind = "settings.yml")
object AkariLevelSettings {

    @Awake(LifeCycle.ENABLE)
    fun reload() {
        val file = File(getDataFolder(), "settings.yml")
        if (file.exists()) {
            file.copyTo(File("settings.yml"), true)
            file.delete()
        }
    }

    @Config("settings.yml")
    lateinit var config: Configuration
        private set

    /**
     * 调试模式
     */
    @ConfigNode("Options.Debug")
    var debug = false
}