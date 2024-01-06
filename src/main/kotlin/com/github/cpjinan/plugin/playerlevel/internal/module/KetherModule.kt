package com.github.cpjinan.plugin.playerlevel.internal.module

import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.runActions
import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.toKetherScript
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.printKetherErrorMessage

/**
 * Kether模块
 * @date 2024/01/06
 */
object KetherModule {
    /**
     * 运行Kether
     * @param [player] 玩家
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
     * 运行Kether并返回结果
     * @param [player] 玩家
     * @return [Any?]
     */
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