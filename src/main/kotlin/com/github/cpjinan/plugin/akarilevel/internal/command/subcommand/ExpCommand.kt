package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI.toBukkitPlayer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.sendLang

object ExpCommand {
    val exp = subCommand {
        literal("add") {
            player("player").dynamic("levelGroup").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    PlayerAPI.addPlayerExp(
                        context.player("player").toBukkitPlayer(),
                        context["levelGroup"],
                        context["amount"].toInt(),
                        "COMMAND_ADD_EXP"
                    )
                    sender.sendLang("Add-Exp", context["player"], context["levelGroup"], context["amount"])
                }
            }
        }
        literal("remove") {
            player("player").dynamic("levelGroup").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    PlayerAPI.removePlayerExp(
                        context.player("player").toBukkitPlayer(),
                        context["levelGroup"],
                        context["amount"].toInt(),
                        "COMMAND_REMOVE_EXP"
                    )
                    sender.sendLang("Remove-Exp", context["player"], context["levelGroup"], context["amount"])
                }
            }
        }
        literal("set") {
            player("player").dynamic("levelGroup").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    PlayerAPI.setPlayerExp(
                        context.player("player").toBukkitPlayer(),
                        context["levelGroup"],
                        context["amount"].toInt(),
                        "COMMAND_SET_EXP"
                    )
                    sender.sendLang("Set-Exp", context["player"], context["levelGroup"], context["amount"])
                }
            }
        }
        literal("check") {
            player("player").dynamic("levelGroup")
                .execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    sender.sendLang(
                        "Check-Exp",
                        context["player"],
                        context["levelGroup"],
                        PlayerAPI.getPlayerExp(context.player("player").toBukkitPlayer(), context["levelGroup"])
                    )
                }
        }
    }
}