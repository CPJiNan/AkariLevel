package com.github.cpjinan.plugin.akarilevel.command.subcommand

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.module.lang.sendLang

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.command.subcommand
 *
 * 成员子命令。
 *
 * @author 季楠
 * @since 2025/8/16 18:23
 */
object MemberCommand {
    val member = subCommand {
        // 检查成员命令。
        literal("has").dynamic("member") {
            suggestUncheck { listOf("player:") }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]

                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }

                if (group.hasMember(context["member"])) {
                    sender.sendLang("MemberHas", context["levelGroup"], context["member"])
                } else {
                    sender.sendLang("MemberNotFound", context["levelGroup"], context["member"])
                }
            }
        }

        // 增加成员命令。
        literal("add").dynamic("member") {
            suggestUncheck { listOf("player:") }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]

                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }

                if (group.hasMember(context["member"])) {
                    sender.sendLang("MemberHas", context["levelGroup"], context["member"])
                    return@execute
                }

                group.addMember(context["member"], "COMMAND_ADD_MEMBER")
                sender.sendLang("MemberAdd", context["levelGroup"], context["member"])
            }
        }

        // 移除成员命令。
        literal("remove").dynamic("member") {
            suggestUncheck { listOf("player:") }
        }.dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]

                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }

                if (!group.hasMember(context["member"])) {
                    sender.sendLang("MemberNotFound", context["levelGroup"], context["member"])
                    return@execute
                }

                group.removeMember(context["member"], "COMMAND_REMOVE_MEMBER")
                sender.sendLang("MemberRemove", context["levelGroup"], context["member"])
            }
        }
    }
}