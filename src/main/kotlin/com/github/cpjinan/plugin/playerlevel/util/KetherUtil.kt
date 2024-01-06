package com.github.cpjinan.plugin.playerlevel.util

import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * Vulpecula
 * top.lanscarlos.vulpecula.utils
 *
 * @author Lanscarlos
 * @since 2022-08-19 16:38
 */

object KetherUtil {
    /**
     * 到 kether 脚本
     * @param [namespace] 命名空间
     * @return [Script]
     */
    fun String.toKetherScript(namespace: List<String> = emptyList()): Script {
        return if (namespace.contains("playerlevel")) {
            this.parseKetherScript(namespace)
        } else {
            this.parseKetherScript(namespace.plus("playerlevel"))
        }
    }

    /**
     * 运行操作
     * @param [func] 函数
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
     * 运行并返回结果
     * @param [script] 脚本
     * @param [sender] 发送者
     * @param [namespace] 命名空间
     * @param [args] 参数
     * @param [throws] 抛出
     * @return [CompletableFuture<Any?>]
     */
    @Deprecated("")
    fun eval(
        script: String,
        sender: Any? = null,
        namespace: List<String> = listOf("playerlevel"),
        args: Map<String, Any?>? = null,
        throws: Boolean = false
    ): CompletableFuture<Any?> {
        val func = {
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