package com.github.cpjinan.plugin.akarilevel.script

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion
import java.util.function.BiFunction

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.script
 *
 * 脚本变量。
 *
 * @author NeigeItems, 季楠
 * @since 2025/10/25 20:50
 */
class ScriptPlaceholder(val identifier: String) {
    companion object {
        /**
         * 注册变量。
         *
         * @param identifier 变量标识符。
         * @param author     作者。
         * @param version    版本。
         * @param executor   变量处理器。
         * @return [ScriptPlaceholder] 对象。
         */
        @JvmStatic
        fun registerPlaceholder(
            identifier: String,
            author: String,
            version: String,
            executor: BiFunction<OfflinePlayer?, String, String>
        ): ScriptPlaceholder {
            return ScriptPlaceholder(identifier).apply {
                this.author = author
                this.version = version
                this.executor = executor
            }
        }

        /**
         * 取消注册变量。
         *
         * @param placeholder 待取消注册的脚本变量。
         */
        @JvmStatic
        fun unregisterPlaceholder(placeholder: ScriptPlaceholder?) {
            placeholder?.let {
                it.enabled = false
            }
        }
    }

    private var enabled: Boolean = false
    private var author: String = "unknown"
    private var version: String = "1.0.0"
    private var executor: BiFunction<OfflinePlayer?, String, String> = BiFunction { _, _ -> "" }
    private val expansion: PlaceholderExpansion = object : PlaceholderExpansion {
        override val identifier: String = this@ScriptPlaceholder.identifier

        override val enabled: Boolean
            get() = this@ScriptPlaceholder.enabled

        override fun onPlaceholderRequest(player: Player?, args: String): String {
            return executor.apply(player, args)
        }

        override fun onPlaceholderRequest(player: OfflinePlayer?, args: String): String {
            return executor.apply(player, args)
        }
    }

    /**
     * 设置作者。
     *
     * @param author 作者。
     * @return 修改后的 [ScriptPlaceholder] 本身。
     */
    fun setAuthor(author: String): ScriptPlaceholder {
        this.author = author
        return this
    }

    /**
     * 设置版本。
     *
     * @param version 版本。
     * @return 修改后的 [ScriptPlaceholder] 本身。
     */
    fun setVersion(version: String): ScriptPlaceholder {
        this.version = version
        return this
    }

    /**
     * 设置变量处理器。
     *
     * @param executor 变量处理器。
     * @return 修改后的 [ScriptPlaceholder] 本身。
     */
    fun setExecutor(executor: BiFunction<OfflinePlayer?, String, String>): ScriptPlaceholder {
        this.executor = executor
        return this
    }

    /**
     * 注册变量。
     *
     * @return 修改后的 [ScriptPlaceholder] 本身。
     */
    fun register(): ScriptPlaceholder {
        enabled = true
        ScriptManager.placeholders.add(this)
        return this
    }

    /**
     * 取消注册变量。
     *
     * @return 修改后的 [ScriptPlaceholder] 本身。
     */
    fun unregister(): ScriptPlaceholder {
        enabled = false
        return this
    }
}