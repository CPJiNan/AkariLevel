package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

object LevelCommand {
    val level = subCommand {
        literal("add") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .int("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.addPlayerLevel(
                            context.player("player").toBukkitPlayer(),
                            context["levelGroup"],
                            context["amount"].toInt(),
                            "COMMAND_ADD_LEVEL"
                        )
                        sender.sendLang("Add-Level", context["player"], context["levelGroup"], context["amount"])
                    }
                }
        }
        literal("remove") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .int("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.removePlayerLevel(
                            context.player("player").toBukkitPlayer(),
                            context["levelGroup"],
                            context["amount"].toInt(),
                            "COMMAND_REMOVE_LEVEL"
                        )
                        sender.sendLang("Remove-Level", context["player"], context["levelGroup"], context["amount"])
                    }
                }
        }
        literal("set") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .int("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.setPlayerLevel(
                            context.player("player").toBukkitPlayer(),
                            context["levelGroup"],
                            context["amount"].toInt(),
                            "COMMAND_SET_LEVEL"
                        )
                        sender.sendLang("Set-Level", context["player"], context["levelGroup"], context["amount"])
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
                        PlayerAPI.getPlayerLevel(context.player("player").toBukkitPlayer(), context["levelGroup"])
                    )
                }
        }
    }
    val levelup = subCommand {
        createHelper()
        dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
            .execute<ProxyCommandSender> { _: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                PlayerAPI.levelupPlayer(context.player().toBukkitPlayer(), context["levelGroup"])
            }
        execute<ProxyCommandSender> { _: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            PlayerAPI.levelupPlayer(context.player().toBukkitPlayer())
        }
    }

    private fun ProxyPlayer.toBukkitPlayer(): Player = Bukkit.getPlayer(this.uniqueId)!!
}