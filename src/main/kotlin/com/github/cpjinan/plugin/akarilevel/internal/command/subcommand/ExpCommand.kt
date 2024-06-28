package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupData
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object ExpCommand {
    val exp = subCommand {
        literal("add") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .int("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.addPlayerExpForce(
                            context.player("player").toBukkitPlayer(),
                            context["levelGroup"],
                            context["amount"].toInt(),
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
                }
        }
        literal("remove") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .int("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.removePlayerExp(
                            context.player("player").toBukkitPlayer(),
                            context["levelGroup"],
                            context["amount"].toInt(),
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
                }
        }
        literal("set") {
            player("player").dynamic("levelGroup") { suggest { LevelAPI.getLevelGroupNames().toList() } }
                .int("amount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        PlayerAPI.setPlayerExp(
                            context.player("player").toBukkitPlayer(),
                            context["levelGroup"],
                            context["amount"].toInt(),
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
                        PlayerAPI.getPlayerExp(context.player("player").toBukkitPlayer(), context["levelGroup"])
                    )
                }
        }
    }

    private fun ProxyPlayer.toBukkitPlayer(): Player = Bukkit.getPlayer(this.uniqueId)!!
}