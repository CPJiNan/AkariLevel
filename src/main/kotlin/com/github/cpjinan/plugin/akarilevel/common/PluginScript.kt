package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.common.event.plugin.PluginReloadEvent
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import com.github.cpjinan.plugin.akarilevel.utils.core.SchedulerUtil.async
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.ScriptExpansion
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.NashornHooker
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.impl.LegacyNashornHookerImpl
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.impl.NashornHookerImpl
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool.ScriptListener
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool.ScriptTask
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNUSED_PARAMETER")
object PluginScript {
    @Awake(LifeCycle.LOAD)
    fun onLoad() {
        plugin.saveDefaultResource(
            "script/Example.js"
        )
        reload()
    }

    /**
     * 所有脚本扩展<文件名, 脚本扩展>
     */
    val scripts = ConcurrentHashMap<String, ScriptExpansion>()

    /**
     * 所有脚本扩展注册的监听器
     */
    val listeners: ConcurrentHashMap.KeySetView<ScriptListener, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * 所有脚本扩展注册的 Bukkit 任务
     */
    val tasks: ConcurrentHashMap.KeySetView<ScriptTask, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * nashorn 依赖挂钩
     */
    val nashornHooker: NashornHooker = when {
        // jdk 自带 nashorn
        try {
            Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory")
            true
        } catch (error: Throwable) {
            false
        } -> LegacyNashornHookerImpl()
        // 主动下载 nashorn
        else -> NashornHookerImpl()
    }

    /**
     * 获取公用ScriptEngine
     */
    val scriptEngine = nashornHooker.getNashornEngine()

    /**
     * 重载管理器
     */
    fun reload() {
        // 卸载脚本扩展
        unload()
        // 加载脚本扩展
        load()
    }

    /**
     * 卸载管理器
     */
    fun unload() {
        // 卸载脚本监听器
        listeners.forEach {
            it.unregister()
        }
        listeners.clear()
        // 卸载Bukkit任务
        tasks.forEach {
            it.unregister()
        }
        tasks.clear()
        // 清除脚本扩展
        scripts.clear()
    }

    /**
     * 加载脚本扩展
     */
    @Awake(LifeCycle.ACTIVE)
    private fun load() {
        // 加载文件中的扩展
        for (file in FileUtil.getFile("plugins/AkariLevel/script", true)) {
            val fileName =
                file.path.replace("plugins${File.separator}AkariLevel${File.separator}script${File.separator}", "")
            // 仅加载.js文件
            if (!fileName.endsWith(".js")) continue
            // 防止某个脚本出错导致加载中断
            try {
                // 加载脚本
                val script = ScriptExpansion(file)
                scripts[fileName] = script
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }

    /**
     * 触发 pluginEnable
     * PluginReloadEvent 是异步触发的, 所以内部没有 runTaskAsynchronously
     */
    @JvmStatic
    @SubscribeEvent
    fun pluginEnable(event: PluginReloadEvent.Post) {
        scripts.forEach { (scriptName, scriptExpansion) ->
            scriptExpansion.run("pluginEnable", scriptName)
        }
    }

    /**
     * 触发 serverEnable (同时也会触发pluginEnable)
     * 内部 runTaskAsynchronously 了
     */
    @JvmStatic
    @Awake(LifeCycle.ACTIVE)
    private fun serverEnable() {
        async {
            scripts.forEach { (scriptName, scriptExpansion) ->
                scriptExpansion.run("pluginEnable", scriptName)
                scriptExpansion.run("serverEnable", scriptName)
            }
        }
    }

    /**
     * 触发 pluginDisable
     * PluginReloadEvent是异步触发的, 所以内部没有 runTaskAsynchronously
     */
    @JvmStatic
    @SubscribeEvent
    fun pluginDisable(event: PluginReloadEvent.Pre) {
        scripts.forEach { (scriptName, scriptExpansion) ->
            scriptExpansion.run("pluginDisable", scriptName)
        }
    }

    /**
     * 触发 serverDisable (同时也会触发disable)
     * 关服的时候不能开新任务，所以是在主线程触发的
     */
    @JvmStatic
    @Awake(LifeCycle.DISABLE)
    private fun serverDisable() {
        scripts.forEach { (scriptName, scriptExpansion) ->
            scriptExpansion.run("pluginDisable", scriptName)
            scriptExpansion.run("serverDisable", scriptName)
        }
        unload()
    }
}