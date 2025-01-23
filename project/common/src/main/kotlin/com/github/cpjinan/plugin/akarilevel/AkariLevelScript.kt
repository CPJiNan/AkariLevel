@file:Suppress("UNUSED_PARAMETER")

package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.event.AkariLevelReloadEvent
import com.github.cpjinan.plugin.akarilevel.script.compile
import com.github.cpjinan.plugin.akarilevel.script.run
import com.github.cpjinan.plugin.akarilevel.util.FileUtils
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap
import javax.script.CompiledScript

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script
 *
 * @author 季楠
 * @since 2025/1/23 10:49
 */
object AkariLevelScript {
    val scripts = ConcurrentHashMap<String, CompiledScript>()

    @Awake(LifeCycle.LOAD)
    fun onLoad() {
        FileUtils.saveResource(
            "script/Example.js"
        )
        reload()
    }

    /**
     * 重载脚本
     */
    fun reload() {
        unload()
        load()
    }

    /**
     * 加载脚本
     */
    @Awake(LifeCycle.ACTIVE)
    private fun load() {
        FileUtils.getFile("script", true).filter { it.name.endsWith(".js") }.forEach {
            try {
                scripts[it.path] = compile(FileUtils.readText(it))
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }

    /**
     * 卸载脚本
     */
    fun unload() {
        scripts.clear()
    }

    /**
     * 服务器启动事件
     */
    @JvmStatic
    @Awake(LifeCycle.ACTIVE)
    private fun serverEnable() {
        scripts.forEach { (_, script) ->
            run(script, "serverEnable")
            run(script, "pluginEnable")
        }
    }

    /**
     * 服务器关闭事件
     */
    @JvmStatic
    @Awake(LifeCycle.DISABLE)
    private fun serverDisable() {
        scripts.forEach { (_, script) ->
            run(script, "pluginDisable")
            run(script, "serverDisable")
        }
        unload()
    }

    /**
     * 插件重载前事件
     */
    @JvmStatic
    @SubscribeEvent
    fun pluginDisable(event: AkariLevelReloadEvent.Pre) {
        scripts.forEach { (_, script) ->
            run(script, "pluginDisable")
        }
    }

    /**
     * 插件重载后事件
     */
    @JvmStatic
    @SubscribeEvent
    fun pluginEnable(event: AkariLevelReloadEvent.Post) {
        scripts.forEach { (_, script) ->
            run(script, "pluginEnable")
        }
    }
}