@file:RuntimeDependencies(
    RuntimeDependency(
        "!org.openjdk.nashorn:nashorn-core:15.4",
        test = "!jdk.nashorn.api.scripting.NashornScriptEngineFactory"
    )
)

package top.cpjinan.akarilevel.script

import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy
import top.cpjinan.akarilevel.event.PluginReloadEvent
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.script.CompiledScript
import javax.script.ScriptEngine

/**
 * AkariLevel
 * top.cpjinan.akarilevel.script
 *
 * 脚本处理器抽象类。
 *
 * @author 季楠
 * @since 2025/10/26 11:52
 */
abstract class ScriptHandler {
    companion object {
        /**
         * 实例。
         */
        val instance by unsafeLazy { nmsProxy<ScriptHandler>() }

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
                            scripts[it.nameWithoutExtension] = instance.compile(it.readText())
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
                instance.invoke(script, "onServerEnable")
                instance.invoke(script, "onPluginEnable")
            }
        }

        /**
         * 服务器关闭事件。
         */
        @Awake(LifeCycle.DISABLE)
        private fun onServerDisable() {
            scripts.forEach { (_, script) ->
                instance.invoke(script, "onPluginDisable")
                instance.invoke(script, "onServerDisable")
            }
        }

        /**
         * 插件重载前事件。
         */
        @SubscribeEvent
        fun onPluginDisable(event: PluginReloadEvent.Pre) {
            scripts.forEach { (_, script) ->
                instance.invoke(script, "onPluginDisable")
            }
        }

        /**
         * 插件重载后事件。
         */
        @SubscribeEvent
        fun onPluginEnable(event: PluginReloadEvent.Post) {
            scripts.forEach { (_, script) ->
                instance.invoke(script, "onPluginEnable")
            }
        }
    }

    /**
     * 获取脚本引擎。
     *
     * @return 脚本引擎。
     */
    abstract fun getScriptEngine(): ScriptEngine

    /**
     * 编译 JS 脚本。
     *
     * @param string 待编译脚本文本。
     * @return 已编译 JS 脚本。
     */
    abstract fun compile(string: String): CompiledScript

    /**
     * 检测引擎中是否存在对应函数。
     *
     * @param engine 脚本引擎。
     * @param function 函数名称。
     * @return 是否存在对应函数。
     */
    abstract fun hasFunction(engine: ScriptEngine, function: String): Boolean

    /**
     * 执行函数并获取返回值。
     *
     * @param compiledScript 待调用脚本。
     * @param function 待执行函数名。
     * @param args 传入函数的参数。
     * @return 返回值。
     */
    abstract fun invoke(compiledScript: CompiledScript, function: String, vararg args: Any): Any?
}