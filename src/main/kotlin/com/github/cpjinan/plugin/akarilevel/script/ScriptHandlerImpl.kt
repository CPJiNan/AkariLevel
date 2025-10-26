package com.github.cpjinan.plugin.akarilevel.script

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror
import taboolib.common5.scriptEngineFactory
import taboolib.module.nms.remap.require
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
 * 脚本处理器实现。
 *
 * @author 季楠
 * @since 2025/10/26 11:55
 */
class ScriptHandlerImpl : ScriptHandler() {
    override fun getScriptEngine(): ScriptEngine {
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

    override fun compile(string: String): CompiledScript {
        return (getScriptEngine() as Compilable).compile(string)
    }

    override fun hasFunction(engine: ScriptEngine, function: String): Boolean {
        return if (require(JDKScriptObjectMirror::class.java)) {
            engine.get(function).let { it is JDKScriptObjectMirror && it.isFunction }
        } else {
            engine.get(function).let { it is ScriptObjectMirror && it.isFunction }
        }
    }

    override fun invoke(compiledScript: CompiledScript, function: String, vararg args: Any): Any? {
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
}