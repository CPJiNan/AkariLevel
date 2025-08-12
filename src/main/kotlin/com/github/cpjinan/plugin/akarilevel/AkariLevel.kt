package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.cache.safeInvalidateAllMemberCache
import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import com.github.cpjinan.plugin.akarilevel.level.ConfigLevelGroup
import com.github.cpjinan.plugin.akarilevel.listener.PersistenceEventListener
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.metrics.Metrics
import taboolib.platform.util.bukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * 插件主类。
 *
 * @author 季楠
 * @since 2025/8/7 22:04
 */
object AkariLevel : Plugin() {
    /**
     * 插件加载事件。
     */
    override fun onLoad() {
        console().sendLang("Plugin-Loading", bukkitPlugin.description.version)
        // bStats 统计。
        if (SettingsConfig.sendMetrics) Metrics(
            18992,
            bukkitPlugin.description.version,
            Platform.BUKKIT
        )
    }

    /**
     * 插件启用事件。
     */
    override fun onEnable() {
        console().sendMessage("")
        console().sendMessage("&o     _    _              _ _                   _  ".colored())
        console().sendMessage("&o    / \\  | | ____ _ _ __(_) |    _____   _____| | ".colored())
        console().sendMessage("&o   / _ \\ | |/ / _` | '__| | |   / _ \\ \\ / / _ \\ | ".colored())
        console().sendMessage("&o  / ___ \\|   < (_| | |  | | |__|  __/\\ V /  __/ | ".colored())
        console().sendMessage("&o /_/   \\_\\_|\\_\\__,_|_|  |_|_____\\___| \\_/ \\___|_| ".colored())
        console().sendMessage("")

        // 从配置文件加载等级组。
        ConfigLevelGroup.reloadConfigLevelGroups()

        // 初始化持久化系统
        PersistenceEventListener.initialize()

        console().sendLang("Plugin-Enabled")

        // DEBUG
        ConfigLevelGroup.getConfigLevelGroups().values.forEach {
            println(it.getMemberLevel("Test"))
            it.addMember("Test", "")
            println(it.getMemberLevel("Test"))
            it.setMemberLevel("Test", 10, "")
            println(it.getMemberLevel("Test"))
            safeInvalidateAllMemberCache()  // 使用安全的缓存清空
            println(it.getMemberLevel("Test"))
        }
    }

    /**
     * 插件卸载事件。
     */
    override fun onDisable() {
        // 关闭顺手保存所有数据
        PersistenceEventListener.shutdown()

        console().sendLang("Plugin-Disable")
    }
}