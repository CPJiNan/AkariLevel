package com.github.cpjinan.plugin.akarilevel.config

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.config
 *
 * @author 季楠
 * @since 2025/1/24 15:05
 */
@ConfigNode(bind = "attribute.yml")
object AttributeSettings {
    @Config("attribute.yml")
    lateinit var config: Configuration
        private set

    /** 属性插件 **/
    @ConfigNode("Plugin")
    var plugin = ""
}