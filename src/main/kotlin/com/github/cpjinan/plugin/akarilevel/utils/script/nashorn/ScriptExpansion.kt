package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn

import com.github.cpjinan.plugin.akarilevel.common.PluginExpansion.nashornHooker
import java.io.File
import java.io.Reader

class ScriptExpansion : CompiledScript {
    /**
     * 构建JavaScript脚本扩展
     *
     * @property reader js脚本文件
     * @constructor JavaScript脚本扩展
     */
    constructor(reader: Reader) : super(reader)

    /**
     * 构建JavaScript脚本扩展
     *
     * @property file js脚本文件
     * @constructor JavaScript脚本扩展
     */
    constructor(file: File) : super(file)

    /**
     * 构建JavaScript脚本扩展
     *
     * @property script js脚本文本
     * @constructor JavaScript脚本扩展
     */
    constructor(script: String) : super(script)

    override fun loadLib() {
        scriptEngine.eval(
            """
                const Bukkit = Packages.org.bukkit.Bukkit
                const EventPriority = Packages.org.bukkit.event.EventPriority
                
                const Listener = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool.ScriptListener
                const Task = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool.ScriptTask
                
                const CommandUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil.INSTANCE
                const ConfigUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.INSTANCE
                const FileUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil.INSTANCE
                const LoggerUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil.INSTANCE
                
                const JavaScriptUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.JavaScript.INSTANCE
                const KetherUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.Kether.INSTANCE
                
                const ListenerUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.ListenerUtil
                const SchedulerUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.SchedulerUtil
                
                const DataAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.DataAPI.INSTANCE
                const LevelAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.LevelAPI.INSTANCE
                const PlayerAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.PlayerAPI.INSTANCE
                
                const PlayerExpChangeEvent = com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
                const PlayerLevelChangeEvent = com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelChangeEvent
                
                const AkariLevel = Packages.com.github.cpjinan.plugin.akarilevel.AkariLevel

                const pluginManager = Bukkit.getPluginManager()
                const scheduler = Bukkit.getScheduler()
                const plugin = pluginManager.getPlugin("AkariLevel")
                
                let sync = SchedulerUtil.sync
                let async = SchedulerUtil.async
            """.trimIndent()
        )
    }

    /**
     * 执行指定函数
     *
     * @param function 函数名
     * @param expansionName 脚本名称(默认为unnamed)
     */
    @Suppress("UNUSED_PARAMETER")
    fun run(function: String, expansionName: String = "unnamed") {
        if (nashornHooker.isFunction(scriptEngine, function)) {
            try {
                invoke(function, null)
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }
}