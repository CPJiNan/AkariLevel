@file:RuntimeDependencies(
    RuntimeDependency(
        "!org.openjdk.nashorn:nashorn-core:15.6",
        test = "!jdk.nashorn.api.scripting.NashornScriptEngineFactory",
        relocate = ["!org.openjdk.nashorn", "!com.github.cpjinan.plugin.akarilevel.library.nashorn"]
    ),
    RuntimeDependency(
        "!org.ow2.asm:asm:9.6",
        relocate = ["!org.ow2.asm", "!com.github.cpjinan.plugin.akarilevel.library.asm"]
    ),
    RuntimeDependency(
        "!org.ow2.asm:asm-util:9.6",
        relocate = ["!org.ow2.asm", "!com.github.cpjinan.plugin.akarilevel.library.asm"]
    ),
    RuntimeDependency(
        "!org.ow2.asm:asm-commons:9.6",
        relocate = ["!org.ow2.asm", "!com.github.cpjinan.plugin.akarilevel.library.asm"]
    ),
)


package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.platform.BukkitPlugin
import java.io.Reader
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.Invocable
import javax.script.ScriptEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory as JDKNashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror as JDKScriptObjectMirror

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script
 *
 * @author Taboolib, 季楠
 * @since 2025/1/23 10:04
 */

/**
 * 获取脚本引擎工厂实例
 *
 * @return Nashorn 引擎
 */
val scriptEngineFactory by lazy {
    try {
        JDKNashornScriptEngineFactory()
    } catch (ex: NoClassDefFoundError) {
        NashornScriptEngineFactory()
    }
}

/**
 * 获取公用的脚本引擎
 *
 * @return 公用的 Nashorn 引擎
 */
val globalScriptEngine: ScriptEngine by lazy {
    getScriptEngine()
}

/**
 * 获取一个新的脚本引擎
 *
 * @return 新的 Nashorn 引擎
 */
fun getScriptEngine(): ScriptEngine {
    val args = arrayOf<String>()
    return try {
        (scriptEngineFactory as JDKNashornScriptEngineFactory).getScriptEngine(
            args,
            BukkitPlugin.getInstance()::class.java.classLoader
        ).also { loadLib(it) }
    } catch (ex: NoClassDefFoundError) {
        (scriptEngineFactory as NashornScriptEngineFactory).getScriptEngine(
            args,
            BukkitPlugin.getInstance()::class.java.classLoader
        ).also { loadLib(it) }
    }
}

/**
 * 编译 JS 脚本 (创建一个新的脚本引擎)
 *
 * @param string 待编译脚本文本
 * @return 已编译 JS 脚本
 */
fun compile(string: String): CompiledScript {
    return (getScriptEngine() as Compilable).compile(string)
}

/**
 * 编译 JS 脚本 (创建一个新的脚本引擎)
 *
 * @param reader 待编译脚本文件
 * @return 已编译 JS 脚本
 */
fun compile(reader: Reader): CompiledScript {
    return (getScriptEngine() as Compilable).compile(reader)
}

/**
 * 编译 JS 脚本 (使用现有的脚本引擎)
 *
 * @param engine 脚本引擎
 * @param string 待编译脚本文本
 * @return 已编译 JS 脚本
 */
fun compile(engine: ScriptEngine, string: String): CompiledScript {
    return (engine as Compilable).compile(string)
}

/**
 * 编译 JS 脚本 (使用现有的脚本引擎)
 *
 * @param reader 待编译脚本文件
 * @return 已编译 JS 脚本
 */
fun compile(engine: ScriptEngine, reader: Reader): CompiledScript {
    return (engine as Compilable).compile(reader)
}

/**
 * 执行函数并获取返回值
 *
 * @param compiledScript 待调用脚本
 * @param function 待执行函数名
 * @param args 传入函数的参数
 * @return 返回值
 */
fun invoke(
    compiledScript: CompiledScript,
    function: String,
    vararg args: Any
): Any? {
    compiledScript.eval()
    return (compiledScript.engine as Invocable).invokeFunction(function, *args)
}

/**
 * 执行函数但不获取返回值
 *
 * @param compiledScript 待调用脚本
 * @param function 待执行函数名
 */
fun run(compiledScript: CompiledScript, function: String) {
    compiledScript.eval()
    if (hasFunction(compiledScript.engine, function)) {
        try {
            invoke(compiledScript, function)
        } catch (error: Throwable) {
            error.printStackTrace()
        }
    }
}

/**
 * 检测引擎中是否存在对应函数
 *
 * @param engine 脚本引擎
 * @param function 函数名称
 * @return 是否存在对应函数
 */
fun hasFunction(engine: ScriptEngine, function: String): Boolean {
    try {
        val func = engine.get(function)
        return func is JDKScriptObjectMirror && func.isFunction
    } catch (ex: NoClassDefFoundError) {
        val func = engine.get(function)
        return func is ScriptObjectMirror && func.isFunction
    }
}

/**
 * 将插件依赖传递给 JS 脚本
 */
fun loadLib(engine: ScriptEngine) {
    engine.eval(
        """
            var Bukkit = Packages.org.bukkit.Bukkit
            var PluginManager = Bukkit.getPluginManager()
            var EventPriority = Packages.org.bukkit.event.EventPriority
            
            var AkariLevel = PluginManager.getPlugin("AkariLevel")
            
            var Listener = Packages.com.github.cpjinan.plugin.akarilevel.script.ScriptListener
            
            var DataAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.DataAPI.INSTANCE
            var LevelAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.LevelAPI.INSTANCE
            var PlayerAPI = Packages.com.github.cpjinan.plugin.akarilevel.api.PlayerAPI.INSTANCE
            
            var PlayerExpChangeEvent = com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
            var PlayerLevelChangeEvent = com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelChangeEvent
            var PluginReloadEvent = com.github.cpjinan.plugin.akarilevel.common.event.plugin.PluginReloadEvent
            
            var CommandUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil.INSTANCE
            var ConfigUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.INSTANCE
            var FileUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil.INSTANCE
            var LoggerUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.LoggerUtil.INSTANCE
            var ListenerUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.core.ListenerUtil.INSTANCE
            
            var JavaScriptUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.JavaScript.INSTANCE
            var KetherUtil = Packages.com.github.cpjinan.plugin.akarilevel.utils.script.Kether.INSTANCE
        """.trimIndent()
    )
}