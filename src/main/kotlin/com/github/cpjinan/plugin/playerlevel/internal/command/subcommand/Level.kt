package com.github.cpjinan.plugin.playerlevel.internal.command.subcommand

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import com.github.cpjinan.plugin.playerlevel.internal.command.toBukkitPlayer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

/**
 * 等级命令
 * @author CPJiNan
 * @date 2024/01/06
 */
object Level {
    val level = subCommand {
        // 添加等级
        literal("add") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.level") || sender.hasPermission(
                            "playerlevel.level.add"
                        )
                    ) {
                        LevelAPI.addPlayerLevel(context.player("player").toBukkitPlayer(), context["amount"].toInt(), "LEVEL_COMMAND")
                        sender.sendLang("add-level", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 移除等级
        literal("remove") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.level") || sender.hasPermission(
                            "playerlevel.level.remove"
                        )
                    ) {
                        LevelAPI.removePlayerLevel(context.player("player").toBukkitPlayer(), context["amount"].toInt(), "LEVEL_COMMAND")
                        sender.sendLang("remove-level", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 设置等级
        literal("set") {
            player("player").int("amount") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.level") || sender.hasPermission(
                            "playerlevel.level.set"
                        )
                    ) {
                        val player = context.player("player").toBukkitPlayer()
                        val level = context["amount"].toInt()
                        LevelAPI.setPlayerLevel(player, level, "LEVEL_COMMAND")
                        sender.sendLang("set-level", context["player"], context["amount"])
                    } else sender.sendLang("no-permission")
                }
            }
        }
        // 查询等级
        literal("check") {
            player("player").execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.level") || sender.hasPermission(
                        "playerlevel.level.check"
                    )
                ) {
                    sender.sendLang(
                        "check-level",
                        context["player"],
                        LevelAPI.getPlayerLevel(context.player("player").toBukkitPlayer())
                    )
                } else sender.sendLang("no-permission")
            }
        }
    }
    val levelUp = subCommand {
        createHelper()
        execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if (sender.hasPermission("playerlevel.default") || sender.hasPermission("playerlevel.levelup")) LevelAPI.playerLevelUP(
                context.player().toBukkitPlayer(), "LEVEL_COMMAND"
            )
            else sender.sendLang("no-permission")
        }
    }
}