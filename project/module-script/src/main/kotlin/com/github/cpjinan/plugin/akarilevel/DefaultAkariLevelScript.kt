@file:Suppress("UNUSED_PARAMETER")

package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.event.AkariLevelReloadEvent
import com.github.cpjinan.plugin.akarilevel.script.compile
import com.github.cpjinan.plugin.akarilevel.script.run
import com.github.cpjinan.plugin.akarilevel.script.ScriptListener
import com.github.cpjinan.plugin.akarilevel.util.DebugUtils
import com.github.cpjinan.plugin.akarilevel.util.FileUtils
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
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
object DefaultAkariLevelScript : AkariLevelScript {
    /** 脚本列表 **/
    override var scripts = ConcurrentHashMap<String, CompiledScript>()

    /** 监听器列表 **/
    override var listeners: ConcurrentHashMap.KeySetView<ScriptListener, Boolean> = ConcurrentHashMap.newKeySet()

    /** 重载脚本 */
    override fun reload() {
        unload()
        load()
    }

    /** 加载脚本 **/
    @Awake(LifeCycle.ACTIVE)
    override fun load() {
        FileUtils.getFile("script", true).filter { it.name.endsWith(".js") }.forEach {
            try {
                scripts[it.path.removePrefix("plugins/AkariLevel/script/")] = compile(FileUtils.readText(it))
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }

    /** 卸载脚本 **/
    override fun unload() {
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
        DebugUtils.debug(
            "&8[&3Akari&bLevel&8] &5调试&7#4 &8| &6触发服务器启动事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "serverEnable")
            run(script, "pluginEnable")

            DebugUtils.debug("&r| &b◈ &r#4 执行 $name 脚本的 serverEnable 及 pluginEnable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }

        DebugUtils.debug(
            "&r| &a◈ &r#4 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    /** 服务器关闭事件 **/
    @JvmStatic
    @Awake(LifeCycle.DISABLE)
    private fun serverDisable() {
        DebugUtils.debug(
            "&8[&3Akari&bLevel&8] &5调试&7#5 &8| &6触发服务器关闭事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "pluginDisable")
            run(script, "serverDisable")

            DebugUtils.debug("&r| &b◈ &r#5 执行 $name 脚本的 pluginDisable 及 serverDisable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }
        unload()

        DebugUtils.debug(
            "&r| &a◈ &r#5 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    /** 插件重载前事件 **/
    @JvmStatic
    @SubscribeEvent
    fun pluginDisable(event: AkariLevelReloadEvent.Pre) {
        DebugUtils.debug(
            "&8[&3Akari&bLevel&8] &5调试&7#6 &8| &6触发插件重载前事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "pluginDisable")

            DebugUtils.debug("&r| &b◈ &r#6 执行 $name 脚本的 pluginDisable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }

        DebugUtils.debug(
            "&r| &a◈ &r#6 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    /** 插件重载后事件 **/
    @JvmStatic
    @SubscribeEvent
    fun pluginEnable(event: AkariLevelReloadEvent.Post) {
        DebugUtils.debug(
            "&8[&3Akari&bLevel&8] &5调试&7#7 &8| &6触发插件重载后事件，正在展示脚本模块处理逻辑。",
        )

        val start = System.currentTimeMillis()
        var time = start

        scripts.forEach { (name, script) ->
            run(script, "pluginEnable")

            DebugUtils.debug("&r| &b◈ &r#7 执行 $name 脚本的 pluginEnable 函数，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()
        }

        DebugUtils.debug(
            "&r| &a◈ &r#7 事件处理完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
        )
    }

    @Awake(LifeCycle.CONST)
    fun onConst() {
        // 注册服务
        PlatformFactory.registerAPI<AkariLevelScript>(DefaultAkariLevelScript)
    }

    @Awake(LifeCycle.ENABLE)
    fun onEnable() {
        // 保存默认资源文件
        FileUtils.saveResource(
            "script/Example.js"
        )
        // 重载脚本
        reload()
    }
}