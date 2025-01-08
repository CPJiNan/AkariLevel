package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.NashornHooker
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.impl.LegacyNashornHookerImpl
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.hook.impl.NashornHookerImpl
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.script.CompiledScript
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.io.File

/**
 * @author InkerXoe
 * @since 2024/2/4 09:56
 */
object PluginScript {
    @Awake(LifeCycle.LOAD)
    fun onLoad() {
        plugin.saveDefaultResource(
            "script/Example.js"
        )
        reload()
    }

    val nashornHooker: NashornHooker =
        when {
            try {
                Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory")
                true
            } catch (error: Throwable) {
                false
            } -> LegacyNashornHookerImpl()

            else -> NashornHookerImpl()
        }

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
        for (file in FileUtil.getFile("plugins/AkariLevel/script", true)) {
            file.path.replace("plugins${File.separator}AkariLevel${File.separator}script${File.separator}", "")
            try {
                val key = file.name
                compiledScripts[key] = CompiledScript(file)
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