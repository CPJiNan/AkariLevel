package com.github.cpjinan.plugin.akarilevel.utils.script

import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.util.replace
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.BukkitPlugin

object Kether {
    fun String.evalKether(
        sender: Any,
        namespace: List<String> = listOf(BukkitPlugin.getInstance().name),
        args: Map<String, Any>? = null,
    ): Any? {
        return eval(listOf(this), sender, namespace, args)
    }

    fun List<String>.evalKether(
        sender: Any,
        namespace: List<String> = listOf(BukkitPlugin.getInstance().name),
        args: Map<String, Any>? = null,
    ): Any? {
        return eval(this, sender, namespace, args)
    }

    private fun eval(
        script: List<String>,
        sender: Any,
        namespace: List<String> = listOf(BukkitPlugin.getInstance().name),
        args: Map<String, Any>? = null,
    ): Any? {
        var scriptList = script
        args?.forEach { (k, v) -> scriptList = scriptList.replace(Pair(k, v)) }
        try {
            return KetherShell.eval(
                scriptList,
                ScriptOptions.builder().namespace(namespace).sender(adaptPlayer(sender)).build()
            ).thenApply { it }.get()
        } catch (e: Exception) {
            e.printKetherErrorMessage()
            return null
        }
    }
}