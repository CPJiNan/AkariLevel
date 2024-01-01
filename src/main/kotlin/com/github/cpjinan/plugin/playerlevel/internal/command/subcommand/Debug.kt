package com.github.cpjinan.plugin.playerlevel.internal.command.subcommand

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import com.github.cpjinan.plugin.playerlevel.internal.command.toBukkitPlayer
import com.github.cpjinan.plugin.playerlevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.playerlevel.internal.manager.DebugManager.replaceSpace
import com.github.cpjinan.plugin.playerlevel.internal.module.KetherModule.evalKether
import com.github.cpjinan.plugin.playerlevel.internal.module.KetherModule.runKether
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.int
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.util.replaceWithOrder
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
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
                                content.replaceSpace().colored().runKether(Bukkit.getPlayer(sender.name))
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
                                sender.sendMessage(" §3§l‹ ›§r §bResult: §f${content.replaceSpace().colored().evalKether(Bukkit.getPlayer(sender.name))}")
                            } else sender.sendLang("no-permission")
                        }
                    }
                }
            }
            // 模板相关
            literal("template") {
                // 等级模板
                literal("levelConfig"){
                    literal("addLevel").int("fromLvl").int("toLvl").dynamic("name").dynamic("expKether") {
                        execute<CommandSender> { sender, context, _ ->
                            if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.debug") || sender.hasPermission(
                                    "playerlevel.debug.template"
                                )
                            ) {
                                for (i in context["fromLvl"].toInt() until context["toLvl"].toInt() + 1){
                                    // 等级名称替换
                                    ConfigManager.level["${i}.name"] = context["name"].replaceSpace().replaceWithOrder(i).colored()
                                    // 等级经验替换
                                    ConfigManager.level["${i}.exp"] = context["expKether"].replaceSpace().replaceWithOrder(i).colored().evalKether(Bukkit.getPlayer(sender.name)).toString().toInt()
                                    // 等级动作添加
                                    ConfigManager.level["${i}.action"] = ConfigManager.level.getStringList("${i}.action")
                                }
                                // 等级配置保存
                                ConfigManager.levelConfig.saveToFile(ConfigManager.levelConfig.file)
                                ConfigManager.levelConfig.reload()
                                sender.sendLang("debug-template")
                            } else sender.sendLang("no-permission")
                        }
                    }
                    literal("addAction").int("fromLvl").int("toLvl").dynamic("action") {
                        execute<CommandSender> { sender, context, _ ->
                            if (sender.isOp || sender.hasPermission("playerlevel.admin") || sender.hasPermission("playerlevel.debug") || sender.hasPermission(
                                    "playerlevel.debug.template"
                                )
                            ) {
                                for (i in context["fromLvl"].toInt() until context["toLvl"].toInt() + 1){
                                    // 等级动作添加
                                    val action = ConfigManager.level.getStringList("${i}.action").toMutableList()
                                    action.add(context["action"].replaceSpace().replaceWithOrder(i).colored())
                                    ConfigManager.level["${i}.action"] = action
                                }
                                // 等级配置保存
                                ConfigManager.levelConfig.saveToFile(ConfigManager.levelConfig.file)
                                ConfigManager.levelConfig.reload()
                                sender.sendLang("debug-template")
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