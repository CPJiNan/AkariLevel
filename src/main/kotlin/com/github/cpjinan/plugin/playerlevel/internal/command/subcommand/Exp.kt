package com.github.cpjinan.plugin.playerlevel.internal.command.subcommand

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import com.github.cpjinan.plugin.playerlevel.internal.command.toBukkitPlayer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.sendLang

object Exp {
    val exp = subCommand {
        // 添加经验
        literal("add") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                        LevelAPI.addPlayerExp(context.player("player").toBukkitPlayer(), context["amount"].toInt())
                        sender.sendLang("add-exp", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 移除经验
        literal("remove") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                        LevelAPI.removePlayerExp(context.player("player").toBukkitPlayer(), context["amount"].toInt())
                        sender.sendLang("remove-exp", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 设置经验
        literal("set") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                        LevelAPI.setPlayerExp(context.player("player").toBukkitPlayer(), context["amount"].toInt())
                        sender.sendLang("set-exp", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 查询经验
        literal("check") {
            player("player").execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                    sender.sendLang(
                        "check-exp", context["player"], LevelAPI.getPlayerExp(context.player("player").toBukkitPlayer())
                    )
                } else sender.sendLang("no-permission")
            }
        }
    }
}