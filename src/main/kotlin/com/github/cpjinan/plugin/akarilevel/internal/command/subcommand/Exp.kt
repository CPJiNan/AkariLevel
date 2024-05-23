package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager.toBukkitPlayer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.sendLang

object Exp {
    val exp = subCommand {
        literal("add") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    AkariLevelAPI.addPlayerExp(
                        context.player("player").toBukkitPlayer(),
                        context["amount"].toInt(),
                        "COMMAND_ADD_EXP"
                    )
                    sender.sendLang("Add-Exp", context["player"], context["amount"])
                }
            }
        }
        literal("remove") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    AkariLevelAPI.removePlayerExp(
                        context.player("player").toBukkitPlayer(),
                        context["amount"].toInt(),
                        "COMMAND_REMOVE_EXP"
                    )
                    sender.sendLang("Remove-Exp", context["player"], context["amount"])
                }
            }
        }
        literal("set") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    AkariLevelAPI.setPlayerExp(
                        context.player("player").toBukkitPlayer(),
                        context["amount"].toInt(),
                        "COMMAND_SET_EXP"
                    )
                    sender.sendLang("Set-Exp", context["player"], context["amount"])
                }
            }
        }
        // 查询经验
        literal("check") {
            player("player").execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                sender.sendLang(
                    "Check-Exp",
                    context["player"],
                    AkariLevelAPI.getPlayerExp(context.player("player").toBukkitPlayer())
                )
            }
        }
    }
}