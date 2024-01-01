package com.github.cpjinan.plugin.playerlevel.internal.module

import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.runActions
import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.toKetherScript
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.printKetherErrorMessage

object KetherModule {
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
    fun String.evalKether(player: Player? = null): Any? {
        var result : Any? = null
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
}