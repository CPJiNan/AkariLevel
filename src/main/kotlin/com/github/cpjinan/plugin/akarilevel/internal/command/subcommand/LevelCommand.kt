package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupData
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil
import org.bukkit.command.CommandSender
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.common.platform.function.pluginVersion
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object LevelCommand {
    val level = subCommand {
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            sender.sendLang("Command-Help-Level", pluginVersion)
        }

        literal("add") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .dynamic("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.addPlayerLevel(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            "COMMAND_ADD_LEVEL"
                        )
                        sender.sendLang(
                            "Add-Level",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }.dynamic("options") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf("--silent", "--noAction", "--source=")
                    }
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false
                        var noAction = false
                        var source = "COMMAND_ADD_LEVEL"

                        for ((k, v) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                                "noaction" -> noAction = true
                                "source" -> source = v ?: "COMMAND_ADD_LEVEL"
                            }
                        }

                        if (!noAction) PlayerAPI.addPlayerLevel(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        ) else PlayerAPI.addPlayerLevelWithoutAction(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        )

                        if (!silent) sender.sendLang(
                            "Add-Level",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }
        }

        literal("remove") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .dynamic("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.removePlayerLevel(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            "COMMAND_REMOVE_LEVEL"
                        )
                        sender.sendLang(
                            "Remove-Level",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }.dynamic("options") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf("--silent", "--noAction", "--source=")
                    }
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false
                        var noAction = false
                        var source = "COMMAND_REMOVE_LEVEL"

                        for ((k, v) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                                "noaction" -> noAction = true
                                "source" -> source = v ?: "COMMAND_REMOVE_LEVEL"
                            }
                        }

                        if (!noAction) PlayerAPI.removePlayerLevel(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        ) else PlayerAPI.removePlayerLevelWithoutAction(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        )

                        if (!silent) sender.sendLang(
                            "Remove-Level",
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
                        PlayerAPI.setPlayerLevel(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            "COMMAND_SET_LEVEL"
                        )
                        sender.sendLang(
                            "Set-Level",
                            context["player"],
                            context["levelGroup"],
                            getLevelGroupData(context["levelGroup"]).display.colored(),
                            context["amount"]
                        )
                    }
                }.dynamic("options") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf("--silent", "--noAction", "--source=")
                    }
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false
                        var noAction = false
                        var source = "COMMAND_SET_LEVEL"

                        for ((k, v) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                                "noaction" -> noAction = true
                                "source" -> source = v ?: "COMMAND_SET_LEVEL"
                            }
                        }

                        if (!noAction) PlayerAPI.setPlayerLevel(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        ) else PlayerAPI.setPlayerLevelWithoutAction(
                            context.player("player").cast(),
                            context["levelGroup"],
                            context["amount"].toLong(),
                            source
                        )

                        if (!silent) sender.sendLang(
                            "Set-Level",
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
                        "Check-Level",
                        context["player"],
                        context["levelGroup"],
                        getLevelGroupData(context["levelGroup"]).display.colored(),
                        PlayerAPI.getPlayerLevel(context.player("player").cast(), context["levelGroup"])
                    )
                }
        }
    }

    val levelup = subCommand {
        createHelper()
        dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
            .execute<ProxyCommandSender> { _: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                PlayerAPI.levelupPlayer(context.player().cast(), context["levelGroup"])
            }
        execute<ProxyCommandSender> { _: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            PlayerAPI.levelupPlayer(context.player().cast())
        }
    }

}