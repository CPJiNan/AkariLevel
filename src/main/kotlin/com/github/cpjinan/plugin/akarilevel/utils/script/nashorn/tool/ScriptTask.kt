package com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.tool

import com.github.cpjinan.plugin.akarilevel.utils.core.SchedulerUtil.sync
import com.github.cpjinan.plugin.akarilevel.utils.core.SchedulerUtil.syncAndGet
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.platform.BukkitPlugin

/**
 * Bukkit 任务
 *
 * @constructor Bukkit 任务
 */
class ScriptTask {
    private var task: Runnable? = null

    private var async = false

    private var period: Long = -1

    private var delay: Long = -1

    private var bukkitTask: BukkitTask? = null

    private var plugin: Plugin = BukkitPlugin.getInstance()

    /**
     * 设置任务
     *
     * @param task 任务
     * @return ScriptTask 本身
     */
    fun setTask(task: Runnable): ScriptTask {
        this.task = task
        return this
    }

    /**
     * 设置触发任务调度的插件
     *
     * @param plugin 任务
     * @return ScriptTask 本身
     */
    fun setPlugin(plugin: Plugin): ScriptTask {
        this.plugin = plugin
        return this
    }

    /**
     * 设置是否异步执行
     *
     * @param async 是否异步
     * @return ScriptTask 本身
     */
    fun setAsync(async: Boolean): ScriptTask {
        this.async = async
        return this
    }

    /**
     * 设置任务执行间隔
     *
     * @param period 执行间隔
     * @return ScriptTask 本身
     */
    fun setPeriod(period: Long): ScriptTask {
        this.period = period.coerceAtLeast(-1)
        return this
    }

    /**
     * 设置任务执行延迟
     *
     * @param delay 执行延迟
     * @return ScriptTask 本身
     */
    fun setDelay(delay: Long): ScriptTask {
        this.delay = delay.coerceAtLeast(-1)
        return this
    }

    /**
     * 注册任务
     *
     * @return ScriptTask 本身
     */
    fun register(): ScriptTask {
        val bukkitRunnable = object : BukkitRunnable() {
            override fun run() {
                task?.run()
            }
        }
        // 我没研究过能不能异步注册, 所以直接同步, 稳妥一点
        bukkitTask = syncAndGet {
            // 如果之前注册过了就先移除并卸载
            unregister()
            // 注册任务
            when {
                async && period > 0 -> {
                    bukkitRunnable.runTaskTimerAsynchronously(plugin, delay.coerceAtLeast(0), period)
                }

                async && delay > 0 -> {
                    bukkitRunnable.runTaskLaterAsynchronously(plugin, delay)
                }

                async -> {
                    bukkitRunnable.runTaskAsynchronously(plugin)
                }

                period > 0 -> {
                    bukkitRunnable.runTaskTimer(plugin, delay.coerceAtLeast(0), period)
                }

                delay > 0 -> {
                    bukkitRunnable.runTaskLater(plugin, delay)
                }

                else -> {
                    bukkitRunnable.runTask(plugin)
                }
            }
        }
        return this
    }

    /**
     * 卸载任务
     *
     * @return ScriptTask 本身
     */
    fun unregister(): ScriptTask {
        // 注册了就取消任务
        sync {
            bukkitTask?.also {
                Bukkit.getScheduler().cancelTask(it.taskId)
            }
        }
        return this
    }
}