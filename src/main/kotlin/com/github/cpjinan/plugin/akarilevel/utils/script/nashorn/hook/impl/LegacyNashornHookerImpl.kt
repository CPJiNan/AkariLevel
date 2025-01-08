package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.impl

import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.NashornHooker
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.script.CompiledScript
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror
import javax.script.Invocable
import javax.script.ScriptEngine

/**
 * @author InkerXoe
 * @since 2024/2/4 09:33
 */
class LegacyNashornHookerImpl : NashornHooker() {
    override fun getNashornEngine(args: Array<String>, classLoader: ClassLoader): ScriptEngine {
        return NashornScriptEngineFactory().getScriptEngine(args, classLoader)
    }

    override fun invoke(
        compiledScript: CompiledScript,
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
        return func is ScriptObjectMirror && func.isFunction
    }
}