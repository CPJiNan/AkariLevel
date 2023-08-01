package com.github.cpjinan.command

import com.github.cpjinan.api.LevelAPI
import com.github.cpjinan.manager.ConfigManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

@CommandHeader(
    name = "PlayerLevel",
    aliases = ["level","exp"],
    permissionDefault = PermissionDefault.TRUE
)
object MainCommand {

    @CommandBody(
        permission = "playerlevel.admin",
        permissionDefault = PermissionDefault.OP
    )
    val main = mainCommand {
        createHelper()

        // 经验命令
        literal("exp") {
            // 添加经验
            literal("add"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        LevelAPI.addPlayerExp(context["player"],context["amount"])
                        sender.sendLang("add-exp",context["player"],context["amount"])
                    }
                }
            }
            // 移除经验
            literal("remove"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        LevelAPI.removePlayerExp(context["player"],context["amount"])
                        sender.sendLang("remove-exp",context["player"],context["amount"])
                    }
                }
            }
            // 设置经验
            literal("set"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        LevelAPI.setPlayerExp(context["player"],context["amount"])
                        sender.sendLang("set-exp",context["player"],context["amount"])
                    }
                }
            }
            // 查询经验
            literal("check"){
                dynamic("player"){
                    suggestPlayers()
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        sender.sendLang("check-exp", context["player"], LevelAPI.getPlayerExp(context["player"]))
                    }
                }
            }
        }

        // 等级命令
        literal("level") {
            // 添加等级
            literal("add"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        LevelAPI.addPlayerLevel(context["player"],context["amount"])
                        sender.sendLang("add-level",context["player"],context["amount"])
                    }
                }
            }
            // 移除等级
            literal("remove"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        LevelAPI.removePlayerLevel(context["player"],context["amount"])
                        sender.sendLang("remove-level",context["player"],context["amount"])
                    }
                }
            }
            // 设置等级
            literal("set"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        LevelAPI.setPlayerLevel(context["player"],context["amount"])
                        sender.sendLang("set-level",context["player"],context["amount"])
                    }
                }
            }
            // 查询等级
            literal("check"){
                dynamic("player"){
                    suggestPlayers()
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        sender.sendLang("check-level", context["player"], LevelAPI.getPlayerLevel(context["player"]))
                    }
                }
            }
        }

    }

    @CommandBody(
        permission = "playerlevel.default",
        permissionDefault = PermissionDefault.TRUE
    )
    val levelup = subCommand {
        createHelper()
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
        LevelAPI.playerLevelUP(sender.name)
        }
    }

}