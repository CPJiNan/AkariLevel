package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.manager
 *
 * 配置管理器。
 *
 * @author 季楠
 * @since 2025/8/7 22:14
 */
object ConfigManager {
    /**
     * 重载配置文件。
     */
    fun reload() {
        SettingsConfig.config.reload()
    }
}