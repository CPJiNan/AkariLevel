package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.event.PluginReloadEvent
import com.github.cpjinan.plugin.akarilevel.script.ScriptListener
import com.github.cpjinan.plugin.akarilevel.script.compile
import com.github.cpjinan.plugin.akarilevel.script.run
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.script.CompiledScript

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.manager
 *
 * 脚本管理器。
 *
 * @author 季楠
 * @since 2025/8/17 11:51
 */
object ScriptManager {
    /**
     * 脚本列表。
     */
    var scripts = ConcurrentHashMap<String, CompiledScript>()

    /**
     * 监听器列表。
     */
    var listeners: ConcurrentHashMap.KeySetView<ScriptListener, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * 重载脚本。
     */
    fun reload() {
        unload()
        load()
    }

    /**
     * 加载脚本。
     */
    @Awake(LifeCycle.ACTIVE)
    fun load() {
        File(getDataFolder(), "script").run {
            if (!exists()) releaseResourceFolder("script")
            walk()
                .filter { it.isFile && it.name.endsWith(".js") }
                .forEach {
                    try {
                        scripts[it.nameWithoutExtension] = compile(it.readText())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    /**
     * 卸载脚本。
     */
    fun unload() {
        // 卸载监听器。
        listeners.forEach { it.unregister() }
        listeners.clear()
        // 卸载脚本。
        scripts.clear()
    }

    /**
     * 服务器启动事件。
     */
    @Awake(LifeCycle.ACTIVE)
    private fun onServerEnable() {
        scripts.forEach { (_, script) ->
            run(script, "onServerEnable")
            run(script, "onPluginEnable")
        }
    }

    /**
     * 服务器关闭事件。
     */
    @Awake(LifeCycle.DISABLE)
    private fun onServerDisable() {
        scripts.forEach { (_, script) ->
            run(script, "onPluginDisable")
            run(script, "onServerDisable")
        }
        unload()
    }

    /**
     * 插件重载前事件。
     */
    @SubscribeEvent
    fun onPluginDisable(event: PluginReloadEvent.Pre) {
        scripts.forEach { (_, script) ->
            run(script, "onPluginDisable")
        }
    }

    /**
     * 插件重载后事件。
     */
    @SubscribeEvent
    fun onPluginEnable(event: PluginReloadEvent.Post) {
        scripts.forEach { (_, script) ->
            run(script, "onPluginEnable")
        }
    }
}