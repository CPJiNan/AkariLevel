package com.github.cpjinan.plugin.akarilevel.utils.core

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import taboolib.platform.BukkitPlugin
import java.util.function.Consumer

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.util
 *
 * @author 季楠
 * @since 2025/1/23 15:15
 */
object ListenerUtil {
    /**
     * 注册监听器
     *
     * @param eventClass      事件类
     * @param eventExecutor   事件处理器
     * @return Listener 对象
     */
    @JvmStatic
    fun <T : Event> registerListener(
        eventClass: Class<T>,
        eventExecutor: Consumer<T>
    ): Listener {
        return registerListener(
            eventClass = eventClass,
            eventExecutor = eventExecutor
        )
    }

    /**
     * 注册监听器
     *
     * @param eventClass      事件类
     * @param eventPriority   监听优先级
     * @param eventExecutor   事件处理器
     * @param ignoreCancelled 是否忽略已取消事件
     * @return Listener 对象
     */
    @JvmStatic
    fun <T : Event> registerListener(
        eventClass: Class<T>,
        eventPriority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        eventExecutor: Consumer<T>
    ): Listener {
        return registerListener(
            eventClass = eventClass,
            eventPriority = eventPriority,
            ignoreCancelled = ignoreCancelled,
            eventExecutor = eventExecutor
        )
    }

    /**
     * 注册监听器
     *
     * @param eventClass      事件类
     * @param eventPriority   监听优先级
     * @param eventExecutor   事件处理器
     * @param plugin          注册监听器的插件
     * @param ignoreCancelled 是否忽略已取消事件
     * @return Listener 对象
     */
    @JvmStatic
    fun <T : Event> registerListener(
        eventClass: Class<T>,
        eventPriority: EventPriority = EventPriority.NORMAL,
        plugin: Plugin = BukkitPlugin.getInstance(),
        ignoreCancelled: Boolean = true,
        eventExecutor: Consumer<T>
    ): Listener {
        val listener = object : Listener {}
        Bukkit.getPluginManager().registerEvent(
            eventClass,
            listener,
            eventPriority,
            { _, event ->
                if (eventClass.isAssignableFrom(event.javaClass)) {
                    @Suppress("UNCHECKED_CAST")
                    eventExecutor.accept(event as T)
                }
            },
            plugin,
            ignoreCancelled
        )
        return listener
    }

    /**
     * 注销监听器
     *
     * @param listener 待注销的监听器
     */
    @JvmStatic
    fun unregisterListener(listener: Listener?) {
        listener?.let {
            HandlerList.unregisterAll(it)
        }
    }
}