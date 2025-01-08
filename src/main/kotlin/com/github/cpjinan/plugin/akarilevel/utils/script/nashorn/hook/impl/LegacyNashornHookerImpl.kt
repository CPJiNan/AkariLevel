package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.impl

import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.NashornHooker
import javax.script.Invocable
import javax.script.ScriptEngine

/**
 * OishEternity
 * me.inkerxoe.oishplugin.eternity.common.script.nashorn.hook.impl
 *
 * @author InkerXoe
 * @since 2024/2/4 09:33
 */

/**
 * jdk自带nashorn挂钩
 *
 * @constructor 启用jdk自带nashorn挂钩
 */
class LegacyNashornHookerImpl : NashornHooker() {
    override fun getNashornEngine(args: Array<String>, classLoader: ClassLoader): ScriptEngine {
        return NashornScriptEngineFactory().getScriptEngine(args, classLoader)
    }

    override fun invoke(
        compiledScript: me.inkerxoe.oishplugin.eternity.common.script.nashorn.script.CompiledScript,
        function: String,
        map: Map<String, Any>?,
        vararg args: Any
    ): Any? {
        val newObject: ScriptObjectMirror =
            (compiledScript.scriptEngine as Invocable).invokeFunction("newObject") as ScriptObjectMirror
        map?.forEach { (key, value) -> newObject[key] = value }
        return newObject.callMember(function, *args)
    }

    override fun isFunction(engine: ScriptEngine, func: Any?): Boolean {
        if (func is ScriptObjectMirror && func.isFunction) {
            return true
        }
        return false
    }
}