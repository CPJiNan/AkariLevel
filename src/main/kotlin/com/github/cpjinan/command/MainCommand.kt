package com.github.cpjinan.command

import com.github.cpjinan.manager.ConfigManager
import com.github.cpjinan.manager.LevelManager
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
                        ConfigManager.player["${context["player"]}.exp"] = ConfigManager.player.getInt("${context["player"]}.exp") + context["amount"].toInt()
                        ConfigManager.dataConfig.saveToFile()
                        ConfigManager.dataConfig.reload()
                        sender.sendLang("add-exp",context["player"],context["amount"])
                        LevelManager.refreshPlayerLevel(Bukkit.getPlayerExact(context["player"])!!)
                    }
                }
            }
            // 移除经验
            literal("remove"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        ConfigManager.player["${context["player"]}.exp"] = ConfigManager.player.getInt("${context["player"]}.exp") - context["amount"].toInt()
                        ConfigManager.dataConfig.saveToFile()
                        ConfigManager.dataConfig.reload()
                        sender.sendLang("remove-exp",context["player"],context["amount"])
                        LevelManager.refreshPlayerLevel(Bukkit.getPlayerExact(context["player"])!!)
                    }
                }
            }
            // 设置经验
            literal("set"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        ConfigManager.player["${context["player"]}.exp"] = context["amount"]
                        ConfigManager.dataConfig.saveToFile()
                        ConfigManager.dataConfig.reload()
                        sender.sendLang("set-exp",context["player"],context["amount"])
                        LevelManager.refreshPlayerLevel(Bukkit.getPlayerExact(context["player"])!!)
                    }
                }
            }
            // 查询经验
            literal("check"){
                dynamic("player"){
                    suggestPlayers()
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        sender.sendLang("check-exp",context["player"], ConfigManager.player.getInt("${context["player"]}.exp"))
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
                        ConfigManager.player["${context["player"]}.level"] = ConfigManager.player.getInt("${context["player"]}.level") + context["amount"].toInt()
                        ConfigManager.dataConfig.saveToFile()
                        ConfigManager.dataConfig.reload()
                        sender.sendLang("add-level",context["player"],context["amount"])
                        LevelManager.refreshPlayerLevel(Bukkit.getPlayerExact(context["player"])!!)
                    }
                }
            }
            // 移除等级
            literal("remove"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        ConfigManager.player["${context["player"]}.level"] = ConfigManager.player.getInt("${context["player"]}.level") - context["amount"].toInt()
                        ConfigManager.dataConfig.saveToFile()
                        ConfigManager.dataConfig.reload()
                        sender.sendLang("remove-level",context["player"],context["amount"])
                        LevelManager.refreshPlayerLevel(Bukkit.getPlayerExact(context["player"])!!)
                    }
                }
            }
            // 设置等级
            literal("set"){
                dynamic("player"){ suggestPlayers() }.int("amount"){
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        ConfigManager.player["${context["player"]}.level"] = context["amount"]
                        ConfigManager.dataConfig.saveToFile()
                        ConfigManager.dataConfig.reload()
                        sender.sendLang("set-level",context["player"],context["amount"])
                        LevelManager.refreshPlayerLevel(Bukkit.getPlayerExact(context["player"])!!)
                        LevelManager.runLevelAction(Bukkit.getPlayerExact(context["player"])!!)
                    }
                }
            }
            // 查询等级
            literal("check"){
                dynamic("player"){
                    suggestPlayers()
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        sender.sendLang("check-level",context["player"], ConfigManager.player.getInt("${context["player"]}.level"))
                    }
                }
            }
        }

    }

    @CommandBody(
        permission = "playerlevel.levelup",
        permissionDefault = PermissionDefault.TRUE
    )
    val levelup = subCommand {
        createHelper()
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            val level: Int = ConfigManager.player.getInt("${sender.name}.level")
            if( level < (ConfigManager.level.getString("max-level")!!.toInt()) ){
                val exp: Int = ConfigManager.player.getInt("${sender.name}.exp")
                val expToLevel: Int = ConfigManager.level.getString("${level + 1}.exp")?.toInt() ?: 0

                if (exp >= expToLevel){
                    ConfigManager.player["${sender.name}.exp"] = exp - expToLevel
                    ConfigManager.player["${sender.name}.level"] = ConfigManager.player.getInt("${sender.name}.level") + 1
                    LevelManager.refreshPlayerLevel(Bukkit.getPlayerExact(sender.name)!!)
                    LevelManager.runLevelAction(Bukkit.getPlayerExact(sender.name)!!)
                    sender.sendLang("level-up-success")
                }
                else sender.sendLang("level-up-fail")
            } else {
                sender.sendLang("max-level")
            }
        }
    }

}