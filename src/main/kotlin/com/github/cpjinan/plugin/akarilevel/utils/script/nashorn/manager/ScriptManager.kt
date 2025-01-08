package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.manager

import me.inkerxoe.oishplugin.eternity.common.script.nashorn.script.CompiledScript
import me.inkerxoe.oishplugin.eternity.internal.manager.HookerManager.nashornHooker
import me.inkerxoe.oishplugin.eternity.utils.ToolsUtil
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File

/**
 * OishEternity
 * me.inkerxoe.oishplugin.eternity.common.script.nashorn.manager
 *
 * @author InkerXoe
 * @since 2024/2/4 09:56
 */
/**
 * 脚本文件管理器, 用于管理所有js节点的脚本文件, 同时提供公用ScriptEngine用于解析公式节点内容
 *
 * @constructor 构建脚本文件管理器
 */
object ScriptManager {
    /**
     * 获取公用ScriptEngine
     */
    val scriptEngine = nashornHooker.getNashornEngine()

    /**
     * 获取所有已编译的js脚本文件及路径
     */
    val compiledScripts = HashMap<String, CompiledScript>()


    /**
     * 加载全部脚本
     */
    @Awake(LifeCycle.ENABLE)
    private fun loadScripts() {
        for (file in getAllFiles("script")) {
            file.path.replace("plugins${File.separator}OishEternity${File.separator}scripts${File.separator}", "")
            try {
                val key = file.name
                compiledScripts[key] = CompiledScript(file)
                ToolsUtil.debug("compiledScript[${key}] -> ${compiledScripts[key]}")
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }

    /**
     * 重载脚本管理器
     */
    fun reload() {
        compiledScripts.clear()
        loadScripts()
    }
}