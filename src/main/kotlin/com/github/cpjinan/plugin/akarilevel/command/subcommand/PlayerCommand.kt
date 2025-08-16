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
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]

                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }

                if (group.hasMember("player:${context["player"]}")) {
                    sender.sendLang("PlayerHas", context["levelGroup"], context["player"])
                } else {
                    sender.sendLang("PlayerNotFound", context["levelGroup"], context["player"])
                }
            }
        }

        // 增加玩家命令。
        literal("add").dynamic("player") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]

                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }

                if (group.hasMember("player:${context["player"]}")) {
                    sender.sendLang("PlayerHas", context["levelGroup"], context["player"])
                    return@execute
                }

                group.addMember("player:${context["player"]}", "COMMAND_ADD_MEMBER")
                sender.sendLang("PlayerAdd", context["levelGroup"], context["player"])
            }
        }

        // 移除玩家命令。
        literal("remove").dynamic("player") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]

                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }

                if (!group.hasMember("player:${context["player"]}")) {
                    sender.sendLang("PlayerNotFound", context["levelGroup"], context["player"])
                    return@execute
                }

                group.removeMember("player:${context["player"]}", "COMMAND_REMOVE_MEMBER")
                sender.sendLang("PlayerRemove", context["levelGroup"], context["player"])
            }
        }
    }
}