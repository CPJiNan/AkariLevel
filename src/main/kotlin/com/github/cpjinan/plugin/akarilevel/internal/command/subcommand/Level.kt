package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager.toBukkitPlayer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

object Level {
    val level = subCommand {
        literal("add") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    AkariLevelAPI.addPlayerLevel(
                        context.player("player").toBukkitPlayer(),
                        context["amount"].toInt(),
                        "COMMAND_ADD_LEVEL"
                    )
                    sender.sendLang("Add-Level", context["player"], context["amount"])
                }
            }
        }
        literal("remove") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    AkariLevelAPI.removePlayerLevel(
                        context.player("player").toBukkitPlayer(),
                        context["amount"].toInt(),
                        "COMMAND_REMOVE_LEVEL"
                    )
                    sender.sendLang("Remove-Level", context["player"], context["amount"])
                }
            }
        }
        literal("set") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    val player = context.player("player").toBukkitPlayer()
                    val level = context["amount"].toInt()
                    AkariLevelAPI.setPlayerLevel(player, level, "COMMAND_SET_LEVEL")
                    sender.sendLang("Set-Level", context["player"], context["amount"])
                }
            }
        }
        literal("check") {
            player("player").execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                sender.sendLang(
                    "Check-Level",
                    context["player"],
                    AkariLevelAPI.getPlayerLevel(context.player("player").toBukkitPlayer())
                )
            }
        }
    }
    val levelup = subCommand {
        createHelper()
        execute<ProxyCommandSender> { _: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            AkariLevelAPI.playerLevelUP(context.player().toBukkitPlayer(), "COMMAND_LEVELUP")
        }
    }
}