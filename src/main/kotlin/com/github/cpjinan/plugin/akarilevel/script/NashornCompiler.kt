@file:RuntimeDependencies(
    RuntimeDependency(
        "!org.openjdk.nashorn:nashorn-core:15.6",
        test = "!jdk.nashorn.api.scripting.NashornScriptEngineFactory",
        relocate = ["!org.openjdk.nashorn", "!com.github.cpjinan.plugin.akarilevel.nashorn"]
    )
)

package com.github.cpjinan.plugin.akarilevel.script

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
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
 * @author NeigeItems, 季楠
 * @since 2025/8/17 12:51
 */

/**
 * 获取类加载器。
 */
lateinit var classLoader: ClassLoader

/**
 * 获取脚本引擎工厂实例。
 */
val scriptEngineFactory by lazy {
    try {
        JDKNashornScriptEngineFactory()
    } catch (_: NoClassDefFoundError) {
        NashornScriptEngineFactory()
    }
}

/**
 * 获取脚本引擎。
 */
fun getScriptEngine(): ScriptEngine {
    return try {
        (scriptEngineFactory as JDKNashornScriptEngineFactory).getScriptEngine(
            arrayOf<String>(),
            classLoader
        ).also { loadLib(it) }
    } catch (_: NoClassDefFoundError) {
        (scriptEngineFactory as NashornScriptEngineFactory).getScriptEngine(
            arrayOf<String>(),
            classLoader
        ).also { loadLib(it) }
    }
}

/**
 * 编译 JS 脚本。
 *
 * @param string 待编译脚本文本。
 * @return 已编译 JS 脚本。
 */
fun compile(string: String): CompiledScript {
    return (getScriptEngine() as Compilable).compile(string)
}

/**
 * 编译 JS 脚本。
 *
 * @param engine 脚本引擎。
 * @param string 待编译脚本文本。
 * @return 已编译 JS 脚本。
 */
fun compile(engine: ScriptEngine, string: String): CompiledScript {
    return (engine as Compilable).compile(string)
}

/**
 * 执行函数并获取返回值。
 *
 * @param compiledScript 待调用脚本。
 * @param function 待执行函数名。
 * @param args 传入函数的参数。
 * @return 返回值。
 */
fun invoke(compiledScript: CompiledScript, function: String, vararg args: Any): Any? {
    compiledScript.eval()
    return (compiledScript.engine as Invocable).invokeFunction(function, *args)
}

/**
 * 执行函数但不获取返回值。
 *
 * @param compiledScript 待调用脚本。
 * @param function 待执行函数名。
 */
fun run(compiledScript: CompiledScript, function: String) {
    compiledScript.eval()
    if (hasFunction(compiledScript.engine, function)) {
        try {
            invoke(compiledScript, function)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 检测引擎中是否存在对应函数。
 *
 * @param engine 脚本引擎。
 * @param function 函数名称。
 * @return 是否存在对应函数。
 */
fun hasFunction(engine: ScriptEngine, function: String): Boolean {
    try {
        return engine.get(function).let { it is JDKScriptObjectMirror && it.isFunction }
    } catch (_: NoClassDefFoundError) {
        return engine.get(function).let { it is ScriptObjectMirror && it.isFunction }
    }
}

/**
 * 加载 JS 脚本依赖。
 */
fun loadLib(engine: ScriptEngine) {
    engine.eval(
        """
            var Bukkit = Packages.org.bukkit.Bukkit;
            var PluginManager = Bukkit.getPluginManager();
            var EventPriority = Packages.org.bukkit.event.EventPriority;

            var AkariLevel = PluginManager.getPlugin("AkariLevel");

            var LevelGroup = Packages.com.github.cpjinan.plugin.akarilevel.level.LevelGroup;
            var ConfigLevelGroup = Packages.com.github.cpjinan.plugin.akarilevel.level.ConfigLevelGroup;

            var Listener = Packages.com.github.cpjinan.plugin.akarilevel.script.ScriptListener;

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