package com.github.cpjinan.plugin.akarilevel.script.type

import com.github.cpjinan.plugin.akarilevel.DefaultAkariLevelScript
import com.github.cpjinan.plugin.akarilevel.util.ListenerUtils
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import taboolib.platform.BukkitPlugin
import java.util.function.Consumer

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script.type
 *
 * @property event 监听的事件
 * @constructor Bukkit 监听器
 *
 * @author 季楠
 * @since 2025/1/23 15:34
 */
class DefaultScriptListener(val event: Class<Event>) : ScriptListener {
    private var listener: Listener? = null
    private var priority: EventPriority = EventPriority.NORMAL
    private var executor: Consumer<Event> = Consumer<Event> {}
    private var plugin: Plugin = BukkitPlugin.getInstance()
    private var ignoreCancelled: Boolean = true

    /**
     * 设置注册监听器的插件
     *
     * @param plugin 插件
     * @return 修改后的 ScriptListener 本身
     */
    override fun setPlugin(plugin: Plugin): DefaultScriptListener {
        this.plugin = plugin
        return this
    }

    /**
     * 设置监听优先级
     *
     * @param priority 监听优先级
     * @return 修改后的 ScriptListener 本身
     */
    override fun setPriority(priority: EventPriority): DefaultScriptListener {
        this.priority = priority
        return this
    }

    /**
     * 设置忽略已取消事件
     *
     * @param ignoreCancelled 忽略已取消事件
     * @return 修改后的 ScriptListener 本身
     */
    override fun setIgnoreCancelled(ignoreCancelled: Boolean): DefaultScriptListener {
        this.ignoreCancelled = ignoreCancelled
        return this
    }

    /**
     * 设置事件处理器
     *
     * @param executor 事件处理器
     * @return 修改后的 ScriptListener 本身
     */
    override fun setExecutor(executor: Consumer<Event>): DefaultScriptListener {
        this.executor = executor
        return this
    }

    /**
     * 注册监听器
     *
     * @return 修改后的 ScriptListener 本身
     */
    override fun register(): DefaultScriptListener {
        unregister()
        listener = ListenerUtils.registerListener(
            event,
            priority,
            plugin,
            ignoreCancelled,
            executor
        )
        DefaultAkariLevelScript.listeners.add(this)
        return this
    }

    /**
     * 卸载监听器
     *
     * @return 修改后的 ScriptListener 本身
     */
    override fun unregister(): DefaultScriptListener {
        ListenerUtils.unregisterListener(listener)
        return this
    }
}