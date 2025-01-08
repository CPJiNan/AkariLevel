package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.script

import java.io.File
import java.io.Reader

/**
 * @author InkerXoe
 * @since 2024/2/4 09:30
 */
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
                const plugin = Packages.com.github.cpjinan.plugin.akarilevel.plugin
                const pluginManager = Bukkit.getPluginManager()
                const scheduler = Bukkit.getScheduler()
                
                function sync(task) {
                    if (Bukkit.isPrimaryThread()) {
                        task()
                    } else {
                        scheduler.callSyncMethod(plugin, task)
                    }
                }
                
                function async(task) {
                    scheduler["runTaskAsynchronously(Plugin,Runnable)"](plugin, task)
                }
            """.trimIndent()
        )
    }
}