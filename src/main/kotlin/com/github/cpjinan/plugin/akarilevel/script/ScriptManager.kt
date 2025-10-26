@file:RuntimeDependencies(
    RuntimeDependency(
        "!org.openjdk.nashorn:nashorn-core:15.4",
        test = "!jdk.nashorn.api.scripting.NashornScriptEngineFactory"
    )
)

package com.github.cpjinan.plugin.akarilevel.script

import com.github.cpjinan.plugin.akarilevel.event.PluginReloadEvent
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.common.platform.function.submit
import taboolib.common5.compileJS
import taboolib.common5.scriptEngineFactory
import taboolib.module.nms.remap.require
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.script.CompiledScript
import javax.script.Invocable
import javax.script.ScriptEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory as JDKNashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror as JDKScriptObjectMirror

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script
 *
 * 脚本管理器。
 *
 * @author 季楠
 * @since 2025/10/24 23:14
 */
object ScriptManager {
    /**
     * 类加载器。
     */
    lateinit var classLoader: ClassLoader

    /**
     * 脚本列表。
     */
    var scripts = ConcurrentHashMap<String, CompiledScript>()

    /**
     * 命令列表。
     */
    var commands: ConcurrentHashMap.KeySetView<ScriptCommand, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * 监听器列表。
     */
    var listeners: ConcurrentHashMap.KeySetView<ScriptListener, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * 任务列表。
     */
    var tasks: ConcurrentHashMap.KeySetView<ScriptTask, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * 变量列表。
     */
    var placeholders: ConcurrentHashMap.KeySetView<ScriptPlaceholder, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * 获取脚本引擎。
     */
    @JvmStatic
    fun getScriptEngine(): ScriptEngine {
        return if (require(JDKNashornScriptEngineFactory::class.java)) {
            (scriptEngineFactory as JDKNashornScriptEngineFactory).getScriptEngine(arrayOf<String>(), classLoader)
        } else {
            (scriptEngineFactory as NashornScriptEngineFactory).getScriptEngine(arrayOf<String>(), classLoader)
        }.apply {
            eval(
                """
            var Bukkit = Packages.org.bukkit.Bukkit;
            var PluginManager = Bukkit.getPluginManager();
            var EventPriority = Packages.org.bukkit.event.EventPriority;
            
            var AkariLevel = PluginManager.getPlugin("AkariLevel");
            
            var LevelGroup = Packages.com.github.cpjinan.plugin.akarilevel.level.LevelGroup;
            var ConfigLevelGroup = Packages.com.github.cpjinan.plugin.akarilevel.level.ConfigLevelGroup;
            
            var ScriptManager = Packages.com.github.cpjinan.plugin.akarilevel.script.ScriptManager;
            var Command = Packages.com.github.cpjinan.plugin.akarilevel.script.ScriptCommand;
            var Listener = Packages.com.github.cpjinan.plugin.akarilevel.script.ScriptListener;
            var Task = Packages.com.github.cpjinan.plugin.akarilevel.script.ScriptTask;
            var Placeholder = Packages.com.github.cpjinan.plugin.akarilevel.script.ScriptPlaceholder;
            
            var PlayerJoinEvent = Packages.org.bukkit.event.player.PlayerJoinEvent;
            var PlayerQuitEvent = Packages.org.bukkit.event.player.PlayerQuitEvent;
            var PlayerExpChangeEvent = Packages.org.bukkit.event.player.PlayerExpChangeEvent;
            
            var LevelGroupRegisterEvent = Packages.com.github.cpjinan.plugin.akarilevel.event.LevelGroupRegisterEvent;
            var LevelGroupUnregisterEvent = Packages.com.github.cpjinan.plugin.akarilevel.event.LevelGroupUnregisterEvent;
            var MemberChangeEvent = Packages.com.github.cpjinan.plugin.akarilevel.event.MemberChangeEvent;
            var MemberExpChangeEvent = Packages.com.github.cpjinan.plugin.akarilevel.event.MemberExpChangeEvent;
            var MemberLevelChangeEvent = Packages.com.github.cpjinan.plugin.akarilevel.event.MemberLevelChangeEvent;
            var PluginReloadEvent = Packages.com.github.cpjinan.plugin.akarilevel.event.PluginReloadEvent;
            """.trimIndent()
            )
        }
    }

    /**
     * 检测引擎中是否存在对应函数。
     *
     * @param engine 脚本引擎。
     * @param function 函数名称。
     * @return 是否存在对应函数。
     */
    @JvmStatic
    fun hasFunction(engine: ScriptEngine, function: String): Boolean {
        return if (require(JDKScriptObjectMirror::class.java)) {
            engine.get(function).let { it is JDKScriptObjectMirror && it.isFunction }
        } else {
            engine.get(function).let { it is ScriptObjectMirror && it.isFunction }
        }
    }

    /**
     * 执行函数并获取返回值。
     *
     * @param compiledScript 待调用脚本。
     * @param function 待执行函数名。
     * @param args 传入函数的参数。
     * @return 返回值。
     */
    @JvmStatic
    fun invoke(compiledScript: CompiledScript, function: String, vararg args: Any): Any? {
        compiledScript.eval()
        return if (hasFunction(compiledScript.engine, function)) {
            try {
                (compiledScript.engine as Invocable).invokeFunction(function, *args)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else null
    }

    /**
     * 创建调度任务。
     *
     * @param now 是否立即执行。
     * @param async 是否异步执行。
     * @param delay 延迟执行时间 (Tick)。
     * @param period 重复执行间隔 (Tick)。
     * @param executor 任务逻辑。
     */
    @JvmStatic
    fun submitTask(
        now: Boolean = false,
        async: Boolean = false,
        delay: Long = 0,
        period: Long = 0,
        executor: Runnable
    ) {
        submit(now, async, delay, period) { executor.run() }
    }

    /**
     * 重载脚本。
     */
    @JvmStatic
    fun reload() {
        unload()
        load()
    }

    /**
     * 加载脚本。
     */
    @Awake(LifeCycle.ACTIVE)
    @JvmStatic
    fun load() {
        File(getDataFolder(), "script").run {
            if (!exists()) releaseResourceFolder("script")
            walk()
                .filter { it.isFile && it.name.endsWith(".js") }
                .forEach {
                    try {
                        scripts[it.nameWithoutExtension] = it.readText().compileJS()!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    /**
     * 卸载脚本。
     */
    @JvmStatic
    fun unload() {
        // 卸载命令。
        commands.forEach { it.unregister() }
        commands.clear()
        // 卸载监听器。
        listeners.forEach { it.unregister() }
        listeners.clear()
        // 卸载任务。
        tasks.forEach { it.unregister() }
        tasks.clear()
        // 卸载变量。
        placeholders.forEach { it.unregister() }
        placeholders.clear()
        // 卸载脚本。
        scripts.clear()
    }

    /**
     * 服务器启动事件。
     */
    @Awake(LifeCycle.ACTIVE)
    private fun onServerEnable() {
        scripts.forEach { (_, script) ->
            invoke(script, "onServerEnable")
            invoke(script, "onPluginEnable")
        }
    }

    /**
     * 服务器关闭事件。
     */
    @Awake(LifeCycle.DISABLE)
    private fun onServerDisable() {
        scripts.forEach { (_, script) ->
            invoke(script, "onPluginDisable")
            invoke(script, "onServerDisable")
        }
        unload()
    }

    /**
     * 插件重载前事件。
     */
    @SubscribeEvent
    fun onPluginDisable(event: PluginReloadEvent.Pre) {
        scripts.forEach { (_, script) ->
            invoke(script, "onPluginDisable")
        }
    }

    /**
     * 插件重载后事件。
     */
    @SubscribeEvent
    fun onPluginEnable(event: PluginReloadEvent.Post) {
        scripts.forEach { (_, script) ->
            invoke(script, "onPluginEnable")
        }
    }
}