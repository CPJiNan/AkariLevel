package com.github.cpjinan.plugin.akarilevel.command.subcommand

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.module.lang.sendLang
import taboolib.platform.util.onlinePlayers

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.command.subcommand
 *
 * 玩家子命令。
 *
 * @author 季楠
 * @since 2025/8/16 18:23
 */
object PlayerCommand {
    val player = subCommand {
        // 检查玩家命令。
        literal("has").dynamic("player") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val groupName = context["levelGroup"]
                val group = LevelGroup.getLevelGroups()[groupName]

                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", groupName)
                    return@execute
                }
                val playerName = context["player"]
                val memberName = "player:$playerName"
                if (group.hasMember(memberName)) {
                    sender.sendLang("PlayerHas", groupName, playerName)
                } else {
                    sender.sendLang("PlayerNotFound", groupName, playerName)
                }
            }
        }

        // 增加玩家命令。
        literal("add").dynamic("player") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val groupName = context["levelGroup"]
                val group = LevelGroup.getLevelGroups()[groupName]
                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", groupName)
                    return@execute
                }
                val playerName = context["player"]
                val memberName = "player:$playerName"
                if (group.hasMember(memberName)) {
                    sender.sendLang("PlayerHas", groupName, playerName)
                    return@execute
                }
                group.addMember(memberName, "COMMAND_ADD_PLAYER")
                sender.sendLang("PlayerAdd", groupName, playerName)
            }
        }

        // 移除玩家命令。
        literal("remove").dynamic("player") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val groupName = context["levelGroup"]
                val group = LevelGroup.getLevelGroups()[groupName]
                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", groupName)
                    return@execute
                }
                val playerName = context["player"]
                val memberName = "player:$playerName"
                if (!group.hasMember(memberName)) {
                    sender.sendLang("PlayerNotFound", groupName, playerName)
                    return@execute
                }
                group.removeMember(memberName, "COMMAND_REMOVE_PLAYER")
                sender.sendLang("PlayerRemove", groupName, playerName)
            }
        }

        // 查看玩家信息命令。
        literal("info").dynamic("player") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val groupName = context["levelGroup"]
                val group = LevelGroup.getLevelGroups()[groupName]
                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", groupName)
                    return@execute
                }
                val playerName = context["player"]
                val memberName = "player:$playerName"
                if (!group.hasMember(memberName)) {
                    sender.sendLang("PlayerNotFound", groupName, playerName)
                    return@execute
                }
                val level = group.getMemberLevel(memberName)
                sender.sendLang(
                    "PlayerInfo",
                    playerName,                                                     // 0 玩家名称。
                    group.name,                                                             // 1 等级组编辑名。
                    group.display,                                                          // 2 等级组展示名
                    level,                                                                  // 3 当前等级。
                    group.getLevelName(memberName, level),                                  // 4 当前等级名称。
                    group.getMemberExp(memberName),                                         // 5 当前经验。
                )
            }
        }

        // 玩家等级命令。
        literal("level") {
            // 设置玩家等级命令。
            literal("set").dynamic("player") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, _ ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val playerName = context["player"]
                    val memberName = "player:$playerName"
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("PlayerNotFound", groupName, playerName)
                        return@execute
                    }
                    val amount = context["amount"].substringBefore(" ").toLong()
                    group.setMemberLevel(memberName, amount, "COMMAND_SET_LEVEL")
                    sender.sendLang("PlayerLevelSet", playerName, groupName, amount)
                }
            }
            // 增加玩家等级命令。
            literal("add").dynamic("player") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, _ ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val playerName = context["player"]
                    val memberName = "player:$playerName"
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("PlayerNotFound", groupName, playerName)
                        return@execute
                    }
                    val amount = context["amount"].substringBefore(" ").toLong()
                    group.addMemberLevel(memberName, amount, "COMMAND_ADD_LEVEL")
                    sender.sendLang("PlayerLevelAdd", playerName, groupName, amount)
                }
            }
            // 移除玩家等级命令。
            literal("remove").dynamic("player") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, _ ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val playerName = context["player"]
                    val memberName = "player:$playerName"
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("PlayerNotFound", groupName, playerName)
                        return@execute
                    }
                    val amount = context["amount"].substringBefore(" ").toLong()
                    group.removeMemberLevel(memberName, amount, "COMMAND_REMOVE_LEVEL")
                    sender.sendLang("PlayerLevelRemove", playerName, groupName, amount)
                }
            }
        }
    }
}