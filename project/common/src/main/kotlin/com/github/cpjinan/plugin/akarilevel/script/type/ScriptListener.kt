package com.github.cpjinan.plugin.akarilevel.script.type

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script.type
 *
 * @author 季楠
 * @since 2025/1/24 10:26
 */
interface ScriptListener {
    /**
     * 设置注册监听器的插件
     *
     * @param plugin 插件
     * @return 修改后的 ScriptListener 本身
     */
    fun setPlugin(plugin: Plugin): ScriptListener

    /**
     * 设置监听优先级
     *
     * @param priority 监听优先级
     * @return 修改后的 ScriptListener 本身
     */
    fun setPriority(priority: EventPriority): ScriptListener

    /**
     * 设置忽略已取消事件
     *
     * @param ignoreCancelled 忽略已取消事件
     * @return 修改后的 ScriptListener 本身
     */
    fun setIgnoreCancelled(ignoreCancelled: Boolean): ScriptListener

    /**
     * 设置事件处理器
     *
     * @param executor 事件处理器
     * @return 修改后的 ScriptListener 本身
     */
    fun setExecutor(executor: Consumer<Event>): ScriptListener

    /**
     * 注册监听器
     *
     * @return 修改后的 ScriptListener 本身
     */
    fun register(): ScriptListener

    /**
     * 卸载监听器
     *
     * @return 修改后的 ScriptListener 本身
     */
    fun unregister(): ScriptListener
}