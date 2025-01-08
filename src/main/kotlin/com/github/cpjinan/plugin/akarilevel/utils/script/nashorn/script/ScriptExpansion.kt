package me.inkerxoe.oishplugin.eternity.common.script.nashorn.script

import java.io.File
import java.io.Reader

/**
 * OishEternity
 * me.inkerxoe.oishplugin.eternity.common.script.nashorn.script
 *
 * @author InkerXoe
 * @since 2024/2/4 09:30
 */
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
                const plugin = Packages.me.inkerxoe.oishplugin.eternity.OishEternity.plugin
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