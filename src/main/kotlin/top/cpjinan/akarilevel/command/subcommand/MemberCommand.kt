package top.cpjinan.akarilevel.command.subcommand

import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.module.lang.sendLang
import taboolib.platform.util.onlinePlayers
import top.cpjinan.akarilevel.level.ConfigLevelGroup
import top.cpjinan.akarilevel.level.LevelGroup
import top.cpjinan.akarilevel.utils.CommandUtils.parseCommandArgs

/**
 * AkariLevel
 * top.cpjinan.akarilevel.command.subcommand
 *
 * 成员子命令。
 *
 * @author 季楠
 * @since 2025/8/16 18:23
 */
@Suppress("DEPRECATION")
object MemberCommand {
    val member = subCommand {
        // 检查成员命令。
        literal("has").dynamic("member") {
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
                val memberName = context["member"]
                if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                    sender.sendLang("PlayerNotFound", memberName)
                    return@execute
                }
                if (group.hasMember(memberName)) {
                    sender.sendLang("MemberHas", groupName, memberName)
                } else {
                    sender.sendLang("MemberNotFound", groupName, memberName)
                }
            }
        }

        // 增加成员命令。
        literal("add").dynamic("member") {
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
                val memberName = context["member"]
                if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                    sender.sendLang("PlayerNotFound", memberName)
                    return@execute
                }
                if (group.hasMember(memberName)) {
                    sender.sendLang("MemberHas", groupName, memberName)
                    return@execute
                }
                group.addMember(memberName, "COMMAND_ADD_MEMBER")
                sender.sendLang("MemberAdd", groupName, memberName)
            }
        }

        // 移除成员命令。
        literal("remove").dynamic("member") {
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
                val memberName = context["member"]
                if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                    sender.sendLang("PlayerNotFound", memberName)
                    return@execute
                }
                if (!group.hasMember(memberName)) {
                    sender.sendLang("MemberNotFound", groupName, memberName)
                    return@execute
                }
                group.removeMember(memberName, "COMMAND_REMOVE_MEMBER")
                sender.sendLang("MemberRemove", groupName, memberName)
            }
        }

        // 查看成员信息命令。
        literal("info").dynamic("member") {
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
                val memberName = context["member"]
                if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                    sender.sendLang("PlayerNotFound", memberName)
                    return@execute
                }
                if (!group.hasMember(memberName)) {
                    sender.sendLang("MemberNotFound", groupName, memberName)
                    return@execute
                }
                val level = group.getMemberLevel(memberName)
                sender.sendLang(
                    "MemberInfo",
                    memberName,                                                     // 0 成员名称。
                    group.name,                                                             // 1 等级组编辑名。
                    group.display,                                                          // 2 等级组展示名
                    level,                                                                  // 3 当前等级。
                    group.getLevelName(memberName, level),                                  // 4 当前等级名称。
                    group.getMemberExp(memberName),                                         // 5 当前经验。
                )
            }
        }

        // 成员等级命令。
        literal("level") {
            // 设置成员等级命令。
            literal("set").dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val memberName = context["member"]
                    if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                        sender.sendLang("PlayerNotFound", memberName)
                        return@execute
                    }
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("MemberNotFound", groupName, memberName)
                        return@execute
                    }
                    val amount = argument.substringBefore(" ").toLongOrNull()
                    if (amount == null) {
                        sender.sendLang("IllegalNumberFormat", context["amount"].substringBefore(" "))
                        return@execute
                    }
                    var args = argument.substringAfter(" ").let(::parseCommandArgs)
                    group.setMemberLevel(memberName, amount, args["source"] ?: "COMMAND_SET_LEVEL")
                    sender.sendLang("MemberLevelSet", memberName, groupName, amount)
                }
            }
            // 增加成员等级命令。
            literal("add").dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val memberName = context["member"]
                    if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                        sender.sendLang("PlayerNotFound", memberName)
                        return@execute
                    }
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("MemberNotFound", groupName, memberName)
                        return@execute
                    }
                    val amount = argument.substringBefore(" ").toLongOrNull()
                    if (amount == null) {
                        sender.sendLang("IllegalNumberFormat", context["amount"].substringBefore(" "))
                        return@execute
                    }
                    var args = argument.substringAfter(" ").let(::parseCommandArgs)
                    group.addMemberLevel(memberName, amount, args["source"] ?: "COMMAND_ADD_LEVEL")
                    sender.sendLang("MemberLevelAdd", memberName, groupName, amount)
                }
            }
            // 移除成员等级命令。
            literal("remove").dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val memberName = context["member"]
                    if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                        sender.sendLang("PlayerNotFound", memberName)
                        return@execute
                    }
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("MemberNotFound", groupName, memberName)
                        return@execute
                    }
                    val amount = argument.substringBefore(" ").toLongOrNull()
                    if (amount == null) {
                        sender.sendLang("IllegalNumberFormat", context["amount"].substringBefore(" "))
                        return@execute
                    }
                    var args = argument.substringAfter(" ").let(::parseCommandArgs)
                    group.removeMemberLevel(memberName, amount, args["source"] ?: "COMMAND_REMOVE_LEVEL")
                    sender.sendLang("MemberLevelRemove", memberName, groupName, amount)
                }
            }
        }

        // 成员经验命令。
        literal("exp") {
            // 设置成员经验命令。
            literal("set").dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val memberName = context["member"]
                    if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                        sender.sendLang("PlayerNotFound", memberName)
                        return@execute
                    }
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("MemberNotFound", groupName, memberName)
                        return@execute
                    }
                    val amount = argument.substringBefore(" ").toLongOrNull()
                    if (amount == null) {
                        sender.sendLang("IllegalNumberFormat", context["amount"].substringBefore(" "))
                        return@execute
                    }
                    var args = argument.substringAfter(" ").let(::parseCommandArgs)
                    group.setMemberExp(memberName, amount, args["source"] ?: "COMMAND_SET_EXP")
                    sender.sendLang("MemberExpSet", memberName, groupName, amount)
                }
            }
            // 增加成员经验命令。
            literal("add").dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val memberName = context["member"]
                    if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                        sender.sendLang("PlayerNotFound", memberName)
                        return@execute
                    }
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("MemberNotFound", groupName, memberName)
                        return@execute
                    }
                    val amount = argument.substringBefore(" ").toLongOrNull()
                    if (amount == null) {
                        sender.sendLang("IllegalNumberFormat", context["amount"].substringBefore(" "))
                        return@execute
                    }
                    var args = argument.substringAfter(" ").let(::parseCommandArgs)
                    group.addMemberExp(memberName, amount, args["source"] ?: "COMMAND_ADD_EXP")
                    sender.sendLang("MemberExpAdd", memberName, groupName, amount)
                }
            }
            // 移除成员经验命令。
            literal("remove").dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("levelGroup") {
                suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            }.dynamic("amount") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val groupName = context["levelGroup"]
                    val group = LevelGroup.getLevelGroups()[groupName]
                    if (group == null) {
                        sender.sendLang("LevelGroupNotFound", groupName)
                        return@execute
                    }
                    val memberName = context["member"]
                    if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                        sender.sendLang("PlayerNotFound", memberName)
                        return@execute
                    }
                    if (!group.hasMember(memberName)) {
                        sender.sendLang("MemberNotFound", groupName, memberName)
                        return@execute
                    }
                    val amount = argument.substringBefore(" ").toLongOrNull()
                    if (amount == null) {
                        sender.sendLang("IllegalNumberFormat", context["amount"].substringBefore(" "))
                        return@execute
                    }
                    var args = argument.substringAfter(" ").let(::parseCommandArgs)
                    group.removeMemberExp(memberName, amount, args["source"] ?: "COMMAND_REMOVE_EXP")
                    sender.sendLang("MemberExpRemove", memberName, groupName, amount)
                }
            }
        }

        // 成员升级命令。
        literal("levelUp").dynamic("levelGroup") {
            suggestUncheck { ConfigLevelGroup.getConfigLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, argument ->
                val groupName = context["levelGroup"].substringBefore(" ")
                if (LevelGroup.getLevelGroups()[groupName] == null) {
                    sender.sendLang("LevelGroupNotFound", groupName)
                    return@execute
                }
                val group = ConfigLevelGroup.getConfigLevelGroups()[groupName]
                if (group == null) {
                    sender.sendLang("MemberLevelUpNotSupport", groupName)
                    return@execute
                }
                val memberName = if (argument.contains(" ")) argument.substringAfter(" ")
                else sender.name
                if (!Bukkit.getOfflinePlayer(memberName).isOnline) {
                    sender.sendLang("PlayerNotFound", memberName)
                    return@execute
                }
                if (!group.hasMember(memberName)) {
                    sender.sendLang("MemberNotFound", groupName, memberName)
                    return@execute
                }
                group.levelUpMember(memberName)
                sender.sendLang("MemberLevelUp", memberName, groupName, group.display)
            }
        }
    }
}