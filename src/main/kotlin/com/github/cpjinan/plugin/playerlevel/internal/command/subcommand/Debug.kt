package com.github.cpjinan.plugin.playerlevel.internal.command.subcommand

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import com.github.cpjinan.plugin.playerlevel.internal.command.toBukkitPlayer
import com.github.cpjinan.plugin.playerlevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.runActions
import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.toKetherScript
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptCommandSender
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

object Debug {
    val debug = subCommand {
        if (ConfigManager.options.getBoolean("debug")) {
            createHelper()
            // 依赖检查
            literal("checkDependencies") {
                execute<ProxyCommandSender> { sender, _, _ ->
                    if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.debug") || sender.hasPermission(
                            "playerlevel.debug.checkdependencies"
                        )
                    ) {
                        if (Bukkit.getPluginManager()
                                .isPluginEnabled("PlaceholderAPI")
                        ) sender.sendMessage(("&7软依赖 &aPlaceholderAPI &7已找到！").colored())
                        else sender.sendMessage(("&7软依赖 &7PlaceholderAPI &7未找到！").colored())
                        if (Bukkit.getPluginManager()
                                .isPluginEnabled("MythicMobs")
                        ) sender.sendMessage(("&7软依赖 &aMythicMobs &7已找到！").colored())
                        else sender.sendMessage(("&7软依赖 &7MythicMobs &7未找到！").colored())
                    } else sender.sendLang("no-permission")
                }
            }
            // 数据修改
            literal("data").literal("set") {
                player("player").int("levelAmount").int("expAmount") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.debug") || sender.hasPermission(
                                "playerlevel.debug.data"
                            )
                        ) {
                            val player = context.player("player").toBukkitPlayer()
                            val level = context["levelAmount"].toInt()
                            val exp = context["expAmount"].toInt()
                            LevelAPI.setPlayerLevel(player, level)
                            LevelAPI.setPlayerExp(player, exp)
                            sender.sendLang("set-level", context["player"], context["levelAmount"])
                            sender.sendLang("set-exp", context["player"], context["expAmount"])
                        } else sender.sendLang("no-permission")
                    }
                }
            }
            literal("kether") {
                // 运行Kether脚本
                literal("run") {
                    dynamic("action") {
                        execute<CommandSender> { sender, _, content ->
                            if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.debug") || sender.hasPermission(
                                    "playerlevel.debug.kether"
                                )
                            ) {
                                try {
                                    val script = if (content.startsWith("def")) {
                                        content
                                    } else {
                                        "def main = { $content }"
                                    }
                                    script.toKetherScript().runActions {
                                        this.sender = adaptCommandSender(sender)
                                        if (sender is Player) {
                                            set("player", sender)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printKetherErrorMessage()
                                }
                            } else sender.sendLang("no-permission")
                        }
                    }
                }
                // 运行Kether脚本并返回结果
                literal("eval") {
                    dynamic("action") {
                        execute<CommandSender> { sender, _, content ->
                            if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.debug") || sender.hasPermission(
                                    "playerlevel.debug.kether"
                                )
                            ) {
                                try {
                                    val script = if (content.startsWith("def")) {
                                        content
                                    } else {
                                        "def main = { $content }"
                                    }

                                    script.toKetherScript().runActions {
                                        this.sender = adaptCommandSender(sender)
                                        if (sender is Player) {
                                            set("player", sender)
                                        }
                                    }.thenAccept {
                                        sender.sendMessage(" §3§l‹ ›§r §bResult: §f$it")
                                    }
                                } catch (e: Exception) {
                                    e.printKetherErrorMessage()
                                }
                            } else sender.sendLang("no-permission")
                        }
                    }
                }
            }
        } else execute<ProxyCommandSender> { sender, _, _ ->
            if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.debug")) {
                sender.sendLang("debug-not-enabled")
            } else sender.sendLang("no-permission")
        }
    }
}