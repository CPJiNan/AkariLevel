package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn

import com.github.cpjinan.plugin.akarilevel.common.PluginExpansion.nashornHooker
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.Reader
import javax.script.Invocable
import javax.script.ScriptEngine

open class CompiledScript {
    private val handle: javax.script.CompiledScript

    /**
     * 获取该脚本对应的 ScriptEngine
     */
    val scriptEngine: ScriptEngine

    /**
     * 编译js脚本并进行包装, 便于调用其中的指定函数
     *
     * @property reader js 脚本文件
     * @constructor 编译 js 脚本并进行包装
     */
    constructor(reader: Reader) {
        scriptEngine = nashornHooker.getNashornEngine()
        this.loadLib()
        handle = nashornHooker.compile(scriptEngine, reader)
        magicFunction()
    }

    /**
     * 编译 js 脚本并进行包装, 便于调用其中的指定函数
     *
     * @property file js 脚本文件
     * @constructor 编译 js 脚本并进行包装
     */
    constructor(file: File) {
        scriptEngine = nashornHooker.getNashornEngine()
        this.loadLib()
        file.reader().use {
            handle = nashornHooker.compile(scriptEngine, it)
        }
        magicFunction()
    }

    /**
     * 编译 js 脚本并进行包装, 便于调用其中的指定函数
     *
     * @property script js 脚本文本
     * @constructor 编译 js 脚本并进行包装
     */
    constructor(script: String) {
        scriptEngine = nashornHooker.getNashornEngine()
        this.loadLib()
        handle = nashornHooker.compile(scriptEngine, script)
        magicFunction()
    }

    /**
     * 加载 JS 前置库
     */
    open fun loadLib() {}

    /**
     * 执行脚本中的指定函数
     *
     * @param function 函数名
     * @param map 传入的默认对象
     * @param args 传入对应方法的参数
     * @return 解析值
     */
    fun invoke(function: String, map: Map<String, Any>?, vararg args: Any): Any? {
        return nashornHooker.invoke(this, function, map, *args)
    }

    /**
     * 执行脚本中的指定函数
     *
     * @param function 函数名
     * @param args 传入对应方法的参数
     * @return 解析值
     */
    fun simpleInvoke(function: String, vararg args: Any?): Any? {
        return (scriptEngine as Invocable).invokeFunction(function, *args)
    }

    /**
     * 此段代码用于解决 js 脚本的高并发调用问题, 只可意会不可言传
     */
    private fun magicFunction() {
        handle.eval()
        scriptEngine.eval(
            """
            function MagicFunction() {}
            MagicFunction.prototype = this
            function newObject() { return new MagicFunction() }
        """
        )
    }
}