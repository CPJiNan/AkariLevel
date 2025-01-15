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
     * 配置文件版本
     */
    @ConfigNode("Options.Config-Version")
    var configVersion: Int? = null

    /**
     * 检查版本更新
     */
    @ConfigNode("Options.Check-Update")
    var checkUpdate = true

    /**
     * OP 版本更新通知
     */
    @ConfigNode("Options.OP-Notify")
    var opNotify = true

    /**
     * bStats 统计
     */
    @ConfigNode("Options.Send-Metrics")
    var sendMetrics = true

    /**
     * 调试模式
     */
    @ConfigNode("Options.Debug")
    var debug = false
}