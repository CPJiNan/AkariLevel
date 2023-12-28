package com.github.cpjinan.plugin.playerlevel.internal.command.subcommand

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import com.github.cpjinan.plugin.playerlevel.internal.command.toBukkitPlayer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.sendLang

object Level {
    val level = subCommand {
        // 添加等级
        literal("add") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                        LevelAPI.addPlayerLevel(context.player("player").toBukkitPlayer(), context["amount"].toInt())
                        sender.sendLang("add-level", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 移除等级
        literal("remove") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                        LevelAPI.removePlayerLevel(context.player("player").toBukkitPlayer(), context["amount"].toInt())
                        sender.sendLang("remove-level", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 设置等级
        literal("set") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                        val player = context.player("player").toBukkitPlayer()
                        val level = context["amount"].toInt()
                        LevelAPI.setPlayerLevel(player, level)
                        sender.sendLang("set-level", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 查询等级
        literal("check") {
            player("player").execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                    sender.sendLang(
                        "check-level", context["player"], LevelAPI.getPlayerLevel(context.player("player").toBukkitPlayer())
                    )
                } else sender.sendLang("no-permission")
            }
        }
    }
}