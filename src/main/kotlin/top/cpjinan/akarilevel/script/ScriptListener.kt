package top.cpjinan.akarilevel.script

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
 * top.cpjinan.akarilevel.script
 *
 * 脚本监听器。
 *
 * @author NeigeItems, 季楠
 * @since 2025/8/17 11:49
 */
class ScriptListener(val event: Class<Event>) {
    companion object {
        /**
         * 注册监听器。
         *
         * @param eventClass    事件类。
         * @param eventExecutor 事件处理器。
         * @return [Listener] 对象。
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
         * 注册监听器。
         *
         * @param eventClass        事件类。
         * @param eventPriority     监听优先级。
         * @param eventExecutor     事件处理器。
         * @param ignoreCancelled   是否忽略已取消事件。
         * @return [Listener] 对象。
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
         * 注册监听器。
         *
         * @param eventClass      事件类。
         * @param eventPriority   监听优先级。
         * @param eventExecutor   事件处理器。
         * @param plugin          注册监听器的插件。
         * @param ignoreCancelled 是否忽略已取消事件。
         * @return [Listener] 对象。
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
         * 取消监听器。
         *
         * @param listener 待取消注册的监听器。
         */
        @JvmStatic
        fun unregisterListener(listener: Listener?) {
            listener?.let {
                HandlerList.unregisterAll(it)
            }
        }
    }

    private var listener: Listener? = null
    private var priority: EventPriority = EventPriority.NORMAL
    private var executor: Consumer<Event> = Consumer<Event> {}
    private var plugin: Plugin = BukkitPlugin.getInstance()
    private var ignoreCancelled: Boolean = true

    /**
     * 设置注册监听器的插件。
     *
     * @param plugin 插件。
     * @return 修改后的 ScriptListener 本身。
     */
    fun setPlugin(plugin: Plugin): ScriptListener {
        this.plugin = plugin
        return this
    }

    /**
     * 设置监听优先级。
     *
     * @param priority 监听优先级。
     * @return 修改后的 [ScriptListener] 本身。
     */
    fun setPriority(priority: EventPriority): ScriptListener {
        this.priority = priority
        return this
    }

    /**
     * 设置忽略已取消事件。
     *
     * @param ignoreCancelled 忽略已取消事件。
     * @return 修改后的 [ScriptListener] 本身。
     */
    fun setIgnoreCancelled(ignoreCancelled: Boolean): ScriptListener {
        this.ignoreCancelled = ignoreCancelled
        return this
    }

    /**
     * 设置事件处理器。
     *
     * @param executor 事件处理器。
     * @return 修改后的 [ScriptListener] 本身。
     */
    fun setExecutor(executor: Consumer<Event>): ScriptListener {
        this.executor = executor
        return this
    }

    /**
     * 注册监听器。
     *
     * @return 修改后的 [ScriptListener] 本身。
     */
    fun register(): ScriptListener {
        unregister()
        listener = registerListener(
            event,
            priority,
            plugin,
            ignoreCancelled,
            executor
        )
        ScriptHandler.listeners.add(this)
        return this
    }

    /**
     * 取消注册监听器。
     *
     * @return 修改后的 [ScriptListener] 本身。
     */
    fun unregister(): ScriptListener {
        unregisterListener(listener)
        return this
    }
}