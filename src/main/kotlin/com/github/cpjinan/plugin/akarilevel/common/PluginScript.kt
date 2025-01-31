@file:Suppress("UNUSED_PARAMETER")

package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.common.event.plugin.PluginReloadEvent
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil.debug
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.ScriptListener
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.compile
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.run
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
object PluginScript {
    /** 脚本列表 **/
    var scripts = ConcurrentHashMap<String, CompiledScript>()

    /** 监听器列表 **/
    var listeners: ConcurrentHashMap.KeySetView<ScriptListener, Boolean> = ConcurrentHashMap.newKeySet()

    /** 重载脚本 */
    fun reload() {
        unload()
        load()
    }

    /** 加载脚本 **/
    @Awake(LifeCycle.ACTIVE)
    fun load() {
        FileUtil.getFile("plugins/AkariLevel/script", true).filter { it.name.endsWith(".js") }.forEach {
            try {
                scripts[it.path.removePrefix("plugins/AkariLevel/script/")] = compile(FileUtil.readText(it))
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }

    /** 卸载脚本 **/
    fun unload() {
        // 卸载监听器
        listeners.forEach {
            it.unregister()
        }
        listeners.clear()
        // 卸载脚本拓展
        scripts.clear()
    }

    /** 服务器启动事件 **/
    @JvmStatic
    @Awake(LifeCycle.ACTIVE)
    private fun serverEnable() {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#1 &8| &6触发服务器启动事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "serverEnable")
            run(script, "pluginEnable")

            debug("&r| &b◈ &r#1 执行 $name 脚本的 serverEnable 及 pluginEnable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }

        debug(
            "&r| &a◈ &r#1 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    /** 服务器关闭事件 **/
    @JvmStatic
    @Awake(LifeCycle.DISABLE)
    private fun serverDisable() {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#2 &8| &6触发服务器关闭事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "pluginDisable")
            run(script, "serverDisable")

            debug("&r| &b◈ &r#2 执行 $name 脚本的 pluginDisable 及 serverDisable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }
        unload()

        debug(
            "&r| &a◈ &r#2 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    /** 插件重载前事件 **/
    @JvmStatic
    @SubscribeEvent
    fun pluginDisable(event: PluginReloadEvent.Pre) {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#3 &8| &6触发插件重载前事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "pluginDisable")

            debug("&r| &b◈ &r#3 执行 $name 脚本的 pluginDisable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }

        debug(
            "&r| &a◈ &r#3 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    /** 插件重载后事件 **/
    @JvmStatic
    @SubscribeEvent
    fun pluginEnable(event: PluginReloadEvent.Post) {
        debug(
            "&8[&3Akari&bLevel&8] &5调试&7#4 &8| &6触发插件重载后事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "pluginEnable")

            debug("&r| &b◈ &r#4 执行 $name 脚本的 pluginEnable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }

        debug(
            "&r| &a◈ &r#4 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    @Awake(LifeCycle.ENABLE)
    fun onEnable() {
        // 保存默认资源文件
        plugin.saveDefaultResource(
            "script/Example.js"
        )
        // 重载脚本
        reload()
    }
}