package com.github.cpjinan.plugin.akarilevel

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/15 14:20
 */
@ConfigNode(bind = "settings.yml")
object AkariLevelSettings {
    @Config("settings.yml")
    lateinit var config: Configuration
        private set

    /**
     * 调试模式
     */
    @ConfigNode("Options.Debug")
    var debug = false
}