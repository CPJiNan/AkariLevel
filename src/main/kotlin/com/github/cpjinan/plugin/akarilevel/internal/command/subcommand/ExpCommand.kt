package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupData
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.utils.CommandUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object ExpCommand {
    val exp = subCommand {
        literal("add") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .dynamic("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.addPlayerExpForce(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            "COMMAND_ADD_EXP"
                        )
                        sender.sendLang(
                            "Add-Exp",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }.dynamic("options") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false
                        var sourceCheck = false
                        var toAllLevelGroup = false
                        var source = "COMMAND_ADD_EXP"

                        for ((k, v) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                                "sourcecheck" -> sourceCheck = true
                                "toalllevelgroup" -> toAllLevelGroup = true
                                "source" -> source = v ?: "COMMAND_ADD_EXP"
                            }
                        }

                        if (!toAllLevelGroup) {
                            if (!sourceCheck) PlayerAPI.addPlayerExpForce(
                                context.player("player").cast(),
                                context["levelGroup"],
                                context["amount"].toLong(),
                                source
                            ) else PlayerAPI.addPlayerExp(
                                context.player("player").cast(),
                                context["levelGroup"],
                                context["amount"].toLong(),
                                source
                            )
                        } else PlayerAPI.addPlayerExp(
                            context.player("player").cast(),
                            context["amount"].toLong(),
                            source
                        )

                        if (!silent) {
                            if (!toAllLevelGroup) sender.sendLang(
                                "Add-Exp",
                                context["player"],
                                context["levelGroup"],
                                getLevelGroupData(context["levelGroup"]).display.colored(),
                                context["amount"]
                            ) else sender.sendLang(
                                "Add-Exp",
                                context["player"],
                                "All",
                                "All",
                                context["amount"]
                            )
                        }
                    }
                }
        }

        literal("remove") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .dynamic("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.removePlayerExp(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            "COMMAND_REMOVE_EXP"
                        )
                        sender.sendLang(
                            "Remove-Exp",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }.dynamic("options") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false
                        var source = "COMMAND_REMOVE_EXP"

                        for ((k, v) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                                "source" -> source = v ?: "COMMAND_REMOVE_EXP"
                            }
                        }

                        PlayerAPI.removePlayerExp(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        )

                        if (!silent) sender.sendLang(
                            "Remove-Exp",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }
        }

        literal("set") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .dynamic("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.setPlayerExp(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            "COMMAND_SET_EXP"
                        )
                        sender.sendLang(
                            "Set-Exp",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }.dynamic("options") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false
                        var source = "COMMAND_SET_EXP"

                        for ((k, v) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                                "source" -> source = v ?: "COMMAND_SET_EXP"
                            }
                        }

                        PlayerAPI.setPlayerExp(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        )

                        if (!silent) sender.sendLang(
                            "Set-Exp",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }
        }

        literal("check") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    sender.sendLang(
                        "Check-Exp",
                        context["player"],
                        context["levelGroup"],
                        getLevelGroupData(context["levelGroup"]).display.colored(),
                        PlayerAPI.getPlayerExp(context.player("player").cast(), context["levelGroup"])
                    )
                }
        }
    }
}