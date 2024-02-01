package com.github.cpjinan.plugin.akarilevel.utils

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.*
import taboolib.platform.BukkitPlugin
import java.util.concurrent.CompletableFuture

/**
 * kether util
 * @author Lanscarlos
 * @since 2022-08-19
 */
object KetherUtil {
    /**
     * run Kether
     * @param [player] player
     * @return [Boolean]
     */
    fun String.runKether(player: Player? = null): Boolean {
        var result = false
        try {
            val script = if (this.startsWith("def")) {
                this
            } else {
                "def main = { $this }"
            }
            script.toKetherScript().runActions {
                this.sender = player?.let { adaptCommandSender(it) }
            }.thenAccept {
                if (it != false) result = true
            }
        } catch (e: Exception) {
            e.printKetherErrorMessage()
        }
        return result
    }

    /**
     * eval kether
     * @param [player] player
     * @return [Any?]
     */
    fun String.evalKether(player: Player? = null): Any? {
        var result: Any? = null
        try {
            val script = if (this.startsWith("def")) {
                this
            } else {
                "def main = { $this }"
            }
            script.toKetherScript().runActions {
                this.sender = player?.let { adaptCommandSender(it) }
            }.thenAccept {
                result = it
            }
        } catch (e: Exception) {
            e.printKetherErrorMessage()
        }
        return result
    }

    /**
     * to kether script
     * @param [namespace] namespace
     * @return [Script]
     */
    fun String.toKetherScript(namespace: List<String> = emptyList()): Script {
        return if (namespace.contains(BukkitPlugin.getInstance().name)) {
            this.parseKetherScript(namespace)
        } else {
            this.parseKetherScript(namespace.plus(BukkitPlugin.getInstance().name))
        }
    }

    /**
     * run actions
     * @param [func] function
     * @return [CompletableFuture<Any?>]
     */
    fun Script.runActions(func: ScriptContext.() -> Unit): CompletableFuture<Any?> {
        return try {
            ScriptContext.create(this).apply(func).runActions()
        } catch (e: Exception) {
            e.printKetherErrorMessage()
            CompletableFuture.completedFuture(null)
        }
    }

    /**
     * eval actions
     * @param [script] script
     * @param [sender] sender
     * @param [namespace] namespace
     * @param [args] args
     * @param [throws] throws
     * @return [CompletableFuture<Any?>]
     */
    @Deprecated("")
    fun eval(
        script: String,
        sender: Any? = null,
        namespace: List<String> = listOf(BukkitPlugin.getInstance().name),
        args: Map<String, Any?>? = null,
        throws: Boolean = false
    ): CompletableFuture<Any?> {
        val func = {
            @Suppress("DEPRECATION")
            KetherShell.eval(script, sender = sender?.let { adaptCommandSender(it) }, namespace = namespace, context = {
                args?.forEach { (k, v) -> set(k, v) }
            })
        }
        return if (throws) func()
        else try {
            func()
        } catch (e: Exception) {
            e.printKetherErrorMessage()
            CompletableFuture.completedFuture(false)
        }
    }
}