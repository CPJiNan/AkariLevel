package top.cpjinan.akarilevel.script

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
 * top.cpjinan.akarilevel.script
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
            
            var LevelGroup = Packages.top.cpjinan.akarilevel.level.LevelGroup;
            var ConfigLevelGroup = Packages.top.cpjinan.akarilevel.level.ConfigLevelGroup;
            
            var Command = Packages.top.cpjinan.akarilevel.script.ScriptCommand;
            var Listener = Packages.top.cpjinan.akarilevel.script.ScriptListener;
            var Task = Packages.top.cpjinan.akarilevel.script.ScriptTask;
            var Placeholder = Packages.top.cpjinan.akarilevel.script.ScriptPlaceholder;
            
            var PlayerJoinEvent = Packages.org.bukkit.event.player.PlayerJoinEvent;
            var PlayerQuitEvent = Packages.org.bukkit.event.player.PlayerQuitEvent;
            var PlayerExpChangeEvent = Packages.org.bukkit.event.player.PlayerExpChangeEvent;
            
            var LevelGroupRegisterEvent = Packages.top.cpjinan.akarilevel.event.LevelGroupRegisterEvent;
            var LevelGroupUnregisterEvent = Packages.top.cpjinan.akarilevel.event.LevelGroupUnregisterEvent;
            var MemberChangeEvent = Packages.top.cpjinan.akarilevel.event.MemberChangeEvent;
            var MemberExpChangeEvent = Packages.top.cpjinan.akarilevel.event.MemberExpChangeEvent;
            var MemberLevelChangeEvent = Packages.top.cpjinan.akarilevel.event.MemberLevelChangeEvent;
            var PluginReloadEvent = Packages.top.cpjinan.akarilevel.event.PluginReloadEvent;
            
            var LegacyMythicMobsDropExpEvent = Packages.top.cpjinan.akarilevel.event.LegacyMythicMobsDropExpEvent;
            var MythicMobsDropExpEvent = Packages.top.cpjinan.akarilevel.event.MythicMobsDropExpEvent;
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