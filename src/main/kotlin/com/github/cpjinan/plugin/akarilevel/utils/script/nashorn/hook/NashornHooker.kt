package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook

import java.io.Reader
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngine

/**
 * @author InkerXoe
 * @since 2024/2/4 09:24
 */
abstract class NashornHooker {
    /**
     * 获取一个新的 Nashorn 引擎
     *
     * @return 一个新的 Nashorn 引擎
     */
    fun getNashornEngine(): ScriptEngine {
        return getNashornEngine(arrayOf("-Dnashorn.args=--language=es6"))
    }

    /**
     * 获取一个共享上下文的 Nashorn 引擎
     *
     * @return 一个新的 Nashorn 引擎
     */
    fun getGlobalEngine(): ScriptEngine {
        return getNashornEngine(arrayOf("-Dnashorn.args=--language=es6", "--global-per-engine"))
    }

    /**
     * 获取一个新的 Nashorn 引擎
     *
     * @param args 应用于引擎的参数
     * @return 一个新的 Nashorn 引擎
     */
    fun getNashornEngine(args: Array<String>): ScriptEngine {
        return getNashornEngine(args, this::class.java.classLoader)
    }

    /**
     * 获取一个新的 Nashorn 引擎
     *
     * @param args 应用于引擎的参数
     * @param classLoader 用于生成引擎的 classLoader
     * @return 一个新的 Nashorn 引擎
     */
    abstract fun getNashornEngine(args: Array<String>, classLoader: ClassLoader): ScriptEngine

    /**
     * 编译一段 js 脚本, 返回已编译脚本对象 (将创建一个新的 ScriptEngine 用于解析脚本)
     *
     * @param string 待编译脚本文本
     * @return 已编译 JS 脚本
     */
    fun compile(string: String): CompiledScript {
        return (getNashornEngine() as Compilable).compile(string)
    }

    /**
     * 编译一段 js 脚本, 返回已编译脚本对象 (将创建一个新的 ScriptEngine 用于解析脚本)
     *
     * @param reader 待编译脚本文件
     * @return 已编译 JS 脚本
     */
    fun compile(reader: Reader): CompiledScript {
        return (getNashornEngine() as Compilable).compile(reader)
    }

    /**
     * 编译一段 js 脚本, 返回已编译脚本对象
     *
     * @param engine 用于编译脚本的脚本引擎
     * @param string 待编译脚本文本
     * @return 已编译 JS 脚本
     */
    fun compile(engine: ScriptEngine, string: String): CompiledScript {
        return (engine as Compilable).compile(string)
    }

    /**
     * 编译一段 js 脚本, 返回已编译脚本对象
     *
     * @param engine 用于编译脚本的脚本引擎
     * @param reader 待编译脚本文件
     * @return 已编译 JS 脚本
     */
    fun compile(engine: ScriptEngine, reader: Reader): CompiledScript {
        return (engine as Compilable).compile(reader)
    }

    /**
     * 调用脚本中的某个函数, 返回函数返回值
     * 因为内部涉及 ScriptObjectMirror 的使用, 所以放入 nashornHooker
     *
     * @param compiledScript 待调用脚本
     * @param function 待调用函数名
     * @param map 顶级变量
     * @param args 传入函数的参数
     * @return 返回值
     */
    abstract fun invoke(
        compiledScript: com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.CompiledScript,
        function: String,
        map: Map<String, Any>?,
        vararg args: Any
    ): Any?

    /**
     * 检测引擎中是否包含对应函数
     *
     * @param engine 函数所在引擎
     * @param func 待检测函数
     * @return 是否包含对应函数
     */
    abstract fun isFunction(engine: ScriptEngine, func: Any?): Boolean

    /**
     * 检测引擎中是否包含对应函数
     *
     * @param engine 函数所在引擎
     * @param func 函数名
     * @return 是否包含对应函数
     */
    fun isFunction(engine: ScriptEngine, func: String): Boolean {
        return isFunction(engine, engine.get(func))
    }
}