package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.utils.ListenerUtil
import com.github.cpjinan.plugin.akarilevel.utils.SchedulerUtil.sync
import com.github.cpjinan.plugin.akarilevel.utils.SchedulerUtil.syncAndGet
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

/**
 * Bukkit 监听器
 *
 * @property event 待监听事件
 * @constructor Bukkit 监听器
 */
class ScriptListener(private val event: Class<Event>) {
    private var priority: EventPriority = EventPriority.NORMAL

    private var ignoreCancelled: Boolean = true

    private var executor: Consumer<Event> = Consumer<Event> {}

    private var listener: Listener? = null

    private var plugin: Plugin = AkariLevel.plugin

    /**
     * 设置注册监听器的插件
     *
     * @param plugin 任务
     * @return ScriptListener 本身
     */
    fun setPlugin(plugin: Plugin): ScriptListener {
        this.plugin = plugin
        return this
    }

    /**
     * 设置监听优先级
     *
     * @param priority 监听优先级
     * @return ScriptListener 本身
     */
    fun setPriority(priority: EventPriority): ScriptListener {
        this.priority = priority
        return this
    }

    /**
     * 设置忽略已取消事件
     *
     * @param ignoreCancelled 忽略已取消事件
     * @return ScriptListener 本身
     */
    fun setIgnoreCancelled(ignoreCancelled: Boolean): ScriptListener {
        this.ignoreCancelled = ignoreCancelled
        return this
    }

    /**
     * 设置事件处理器
     *
     * @param executor 事件处理器
     * @return ScriptListener 本身
     */
    fun setExecutor(executor: Consumer<Event>): ScriptListener {
        this.executor = executor
        return this
    }

    /**
     * 注册监听器
     *
     * @return ScriptListener 本身
     */
    fun register(): ScriptListener {
        // HandlerList是非线程安全的, 需要同步注册
        listener = syncAndGet {
            // 如果之前注册过了就先移除并卸载
            unregister()
            ListenerUtil.registerListener(
                event,
                priority,
                plugin,
                ignoreCancelled,
                executor
            )
        }
        return this
    }

    /**
     * 卸载监听器
     *
     * @return ScriptListener 本身
     */
    fun unregister(): ScriptListener {
        // 注册了就取消监听
        sync {
            ListenerUtil.unregisterListener(listener)
        }
        return this
    }
}