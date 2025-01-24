package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.script.type.ScriptListener
import java.util.concurrent.ConcurrentHashMap
import javax.script.CompiledScript

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script
 *
 * @author 季楠
 * @since 2025/1/23 10:49
 */
interface AkariLevelScript {
    /** 脚本列表 **/
    var scripts: ConcurrentHashMap<String, CompiledScript>

    /** 监听器列表 **/
    var listeners: ConcurrentHashMap.KeySetView<ScriptListener, Boolean>

    /** 重载脚本 **/
    fun reload()

    /** 加载脚本 **/
    fun load()

    /** 卸载脚本 **/
    fun unload()
}