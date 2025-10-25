package com.github.cpjinan.plugin.akarilevel.script

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import taboolib.platform.BukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script
 *
 * 脚本任务。
 *
 * @author NeigeItems, 季楠
 * @since 2025/10/25 20:48
 */
class ScriptTask {
    companion object {
        /**
         * 注册任务。
         *
         * @param task 任务逻辑。
         * @return [BukkitTask] 对象。
         */
        @JvmStatic
        fun registerTask(
            task: Runnable
        ): BukkitTask {
            return registerTask(
                task = task
            )
        }

        /**
         * 注册任务。
         *
         * @param task  任务逻辑。
         * @param async 是否异步执行。
         * @return [BukkitTask] 对象。
         */
        @JvmStatic
        fun registerTask(
            task: Runnable,
            async: Boolean = false
        ): BukkitTask {
            return registerTask(
                task = task,
                async = async
            )
        }

        /**
         * 注册任务。
         *
         * @param task   任务逻辑。
         * @param async  是否异步执行。
         * @param delay  延迟执行时间 (Tick)。
         * @return [BukkitTask] 对象。
         */
        @JvmStatic
        fun registerTask(
            task: Runnable,
            async: Boolean = false,
            delay: Long = 0
        ): BukkitTask {
            return registerTask(
                task = task,
                async = async,
                delay = delay
            )
        }

        /**
         * 注册任务。
         *
         * @param task   任务逻辑。
         * @param async  是否异步执行。
         * @param delay  延迟执行时间 (Tick)。
         * @param period 重复执行间隔 (Tick)。
         * @return [BukkitTask] 对象。
         */
        @JvmStatic
        fun registerTask(
            task: Runnable,
            async: Boolean = false,
            delay: Long = 0,
            period: Long = -1
        ): BukkitTask {
            return registerTask(
                task = task,
                async = async,
                delay = delay,
                period = period
            )
        }

        /**
         * 注册任务。
         *
         * @param task   任务逻辑。
         * @param async  是否异步执行。
         * @param delay  延迟执行时间 (Tick)。
         * @param period 重复执行间隔 (Tick)。
         * @param plugin 注册任务的插件。
         * @return [BukkitTask] 对象。
         */
        @JvmStatic
        fun registerTask(
            task: Runnable,
            async: Boolean = false,
            delay: Long = 0,
            period: Long = -1,
            plugin: Plugin = BukkitPlugin.getInstance()
        ): BukkitTask {
            return when {
                async && period > 0 -> {
                    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay.coerceAtLeast(0), period)
                }

                async && delay > 0 -> {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay)
                }

                async -> {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
                }

                period > 0 -> {
                    Bukkit.getScheduler().runTaskTimer(plugin, task, delay.coerceAtLeast(0), period)
                }

                delay > 0 -> {
                    Bukkit.getScheduler().runTaskLater(plugin, task, delay)
                }

                else -> {
                    Bukkit.getScheduler().runTask(plugin, task)
                }
            }
        }

        /**
         * 取消任务。
         *
         * @param task 待取消的任务。
         */
        @JvmStatic
        fun unregisterTask(task: BukkitTask?) {
            task?.cancel()
        }
    }

    private var task: Runnable? = null
    private var bukkitTask: BukkitTask? = null
    private var async: Boolean = false
    private var delay: Long = 0
    private var period: Long = -1
    private var plugin: Plugin = BukkitPlugin.getInstance()

    /**
     * 设置任务逻辑。
     *
     * @param task 任务逻辑。
     * @return 修改后的 ScriptTask 本身。
     */
    fun setTask(task: Runnable): ScriptTask {
        this.task = task
        return this
    }

    /**
     * 设置注册任务的插件。
     *
     * @param plugin 插件。
     * @return 修改后的 [ScriptTask] 本身。
     */
    fun setPlugin(plugin: Plugin): ScriptTask {
        this.plugin = plugin
        return this
    }

    /**
     * 设置是否异步执行。
     *
     * @param async 是否异步执行。
     * @return 修改后的 [ScriptTask] 本身。
     */
    fun setAsync(async: Boolean): ScriptTask {
        this.async = async
        return this
    }

    /**
     * 设置延迟执行时间。
     *
     * @param delay 延迟执行时间 (Tick)。
     * @return 修改后的 [ScriptTask] 本身。
     */
    fun setDelay(delay: Long): ScriptTask {
        this.delay = delay.coerceAtLeast(0)
        return this
    }

    /**
     * 设置重复执行间隔。
     *
     * @param period 重复执行间隔 (Tick)。
     * @return 修改后的 [ScriptTask] 本身。
     */
    fun setPeriod(period: Long): ScriptTask {
        this.period = period.coerceAtLeast(-1)
        return this
    }

    /**
     * 注册任务。
     *
     * @return 修改后的 [ScriptTask] 本身。
     */
    fun register(): ScriptTask {
        unregister()
        task?.let {
            bukkitTask = registerTask(
                it,
                async,
                delay,
                period,
                plugin
            )
        }
        ScriptManager.tasks.add(this)
        return this
    }

    /**
     * 取消注册任务。
     *
     * @return 修改后的 [ScriptTask] 本身。
     */
    fun unregister(): ScriptTask {
        unregisterTask(bukkitTask)
        return this
    }
}