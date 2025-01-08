package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn

import java.io.File
import java.io.Reader

class ScriptExpansion : CompiledScript {
    /**
     * 构建JavaScript脚本扩展
     *
     * @property reader js 脚本文件
     * @constructor JavaScript 脚本扩展
     */
    constructor(reader: Reader) : super(reader)

    /**
     * 构建JavaScript脚本扩展
     *
     * @property file js 脚本文件
     * @constructor JavaScript 脚本扩展
     */
    constructor(file: File) : super(file)

    /**
     * 构建JavaScript脚本扩展
     *
     * @property script js 脚本文本
     * @constructor JavaScript 脚本扩展
     */
    constructor(script: String) : super(script)

    override fun loadLib() {
        scriptEngine.eval(
            """
                const Bukkit = Packages.org.bukkit.Bukkit
                const Material = Packages.org.bukkit.Material
                const ItemStack = Packages.org.bukkit.inventory.ItemStack
                const EventPriority = Packages.org.bukkit.event.EventPriority
                const Listener = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool.ScriptListener
                const Task = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool.ScriptTask
                
                const CommandUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil
                const ConfigUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil
                const FileUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
                const LoggerUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil
                
                const JavaScriptUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.JavaScript
                const KetherUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.Kether
                
                const ListenerUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.ListenerUtil
                const SchedulerUtils = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.SchedulerUtil
                
                const DataAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.DataAPI
                const LevelAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.LevelAPI
                const PlayerAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
                
                const PlayerExpChangeEvent = com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
                const PlayerLevelChangeEvent = com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelChangeEvent
                
                const AkariLevel = Packages.com.github.cpjinan.plugin.akarilevel.AkariLevel

                const pluginManager = Bukkit.getPluginManager()
                const scheduler = Bukkit.getScheduler()
                const plugin = pluginManager.getPlugin("AkariLevel")
                
                let sync = SchedulerUtils.sync
                let async = SchedulerUtils.async
            """.trimIndent()
        )
    }
}