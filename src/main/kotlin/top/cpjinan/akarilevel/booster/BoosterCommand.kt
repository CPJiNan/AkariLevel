package top.cpjinan.akarilevel.booster

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang
import taboolib.platform.util.onlinePlayers
import top.cpjinan.akarilevel.utils.CommandUtils.parseCommandArgs
import top.cpjinan.akarilevel.utils.TimeUtils.formatToDate
import top.cpjinan.akarilevel.utils.TimeUtils.formatToDuration
import java.util.*

/**
 * AkariLevel
 * top.cpjinan.akarilevel.booster
 *
 * 经验加成器命令。
 *
 * @author 季楠
 * @since 2025/12/7 14:32
 */
object BoosterCommand {
    val booster = subCommand {
        // 查看经验加成器信息命令。
        literal("info").dynamic("member") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("booster") {
            execute<ProxyCommandSender> { sender, context, _ ->
                val member = context["member"]
                Booster.refreshMemberBoosters(member)
                val id = context["booster"]
                val booster = Booster.getMemberBoosters(member)[id]
                if (booster == null) {
                    sender.sendLang("BoosterNotFound", member, id)
                    return@execute
                }
                sender.sendLang(
                    "BoosterInfo",
                    member,
                    booster.id,
                    booster.name,
                    booster.type,
                    booster.multiplier,
                    console().asLangText(
                        if (Booster.isMemberBoosterEnabled(member, booster.id)) "BoosterInfoEnabled"
                        else "BoosterInfoDisabled"
                    ),
                    if (booster.start != -1L) formatToDate(booster.start) else "",
                    if (booster.start != -1L && booster.duration != -1L) formatToDate(booster.start + booster.duration) else "",
                    if (booster.duration != -1L) formatToDuration(booster.duration) else console().asLangText("BoosterInfoPermanent"),
                    booster.levelGroup,
                    booster.source
                )
            }
        }

        // 查看经验加成器列表命令。
        literal("list").dynamic("member") {
            suggestUncheck { onlinePlayers.map { it.name } }
            execute<ProxyCommandSender> { sender, _, argument ->
                val member = argument.substringBefore(" ")
                Booster.refreshMemberBoosters(member)
                val pageSize = 10
                val boosters = Booster.getMemberBoosters(member).values.sortedBy { it.name }
                val totalPages = (boosters.size + pageSize - 1) / pageSize
                val currentPage =
                    argument.substringAfter(" ").takeIf { it != argument }?.toIntOrNull()?.coerceIn(1, totalPages) ?: 1
                with(sender) {
                    sendLang("BoosterListHeader", member, boosters.size)
                    boosters
                        .subList((currentPage - 1) * pageSize, (currentPage * pageSize).coerceAtMost(boosters.size))
                        .forEach { sendLang("BoosterListFormat", member, it.id, it.name) }
                    sendLang(
                        "BoosterListFooter",
                        member,
                        currentPage,
                        totalPages,
                        (currentPage - 1).coerceAtLeast(1),
                        (currentPage + 1).coerceAtMost(totalPages)
                    )
                }
            }
        }

        // 新增经验加成器命令。
        literal("add").dynamic("member") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("name").dynamic("multiplier") {
            execute<ProxyCommandSender> { sender, context, argument ->
                val member = context["member"]
                Booster.refreshMemberBoosters(member)
                var args = argument.substringAfter(" ").let(::parseCommandArgs)
                val booster = Booster(
                    id = args["id"] ?: UUID.randomUUID().toString().take(8),
                    name = context["name"].colored(),
                    type = args["type"].orEmpty(),
                    multiplier = argument.substringBefore(" ").toDoubleOrNull() ?: 1.0,
                    start = System.currentTimeMillis(),
                    duration = args["duration"]?.let(::formatToDuration) ?: -1,
                    levelGroup = args["levelGroup"]?.split(",") ?: emptyList(),
                    source = args["source"]?.split(",")
                        ?: listOf("COMMAND_ADD_EXP", "MYTHICMOBS_DROP_EXP", "TEAM_SHARE_EXP", "VANILLA_EXP_CHANGE")
                )
                Booster.addMemberBooster(member, booster)
                sender.sendLang("BoosterAdd", member, booster.id)
            }
        }

        // 移除经验加成器命令。
        literal("remove").dynamic("member") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("booster") {
            execute<ProxyCommandSender> { sender, context, _ ->
                val member = context["member"]
                Booster.refreshMemberBoosters(member)
                val id = context["booster"]
                val booster = Booster.getMemberBoosters(member)[id]
                if (booster == null) {
                    sender.sendLang("BoosterNotFound", member, id)
                    return@execute
                }
                Booster.removeMemberBooster(member, id)
                sender.sendLang("BoosterRemove", member, id)
            }
        }

        // 启用经验加成器命令。
        literal("enable").dynamic("member") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("booster") {
            execute<ProxyCommandSender> { sender, context, _ ->
                val member = context["member"]
                Booster.refreshMemberBoosters(member)
                val id = context["booster"]
                val booster = Booster.getMemberBoosters(member)[id]
                if (booster == null) {
                    sender.sendLang("BoosterNotFound", member, id)
                    return@execute
                }
                if (!Booster.isMemberBoosterEnabled(member, id)) Booster.enableMemberBooster(member, id)
                sender.sendLang("BoosterEnabled", member, id)
            }
        }

        // 禁用经验加成器命令。
        literal("disable").dynamic("member") {
            suggestUncheck { onlinePlayers.map { it.name } }
        }.dynamic("booster") {
            execute<ProxyCommandSender> { sender, context, _ ->
                val member = context["member"]
                Booster.refreshMemberBoosters(member)
                val id = context["booster"]
                val booster = Booster.getMemberBoosters(member)[id]
                if (booster == null) {
                    sender.sendLang("BoosterNotFound", member, id)
                    return@execute
                }
                if (Booster.isMemberBoosterEnabled(member, id)) Booster.disableMemberBooster(member, id)
                sender.sendLang("BoosterDisabled", member, id)
            }
        }
    }
}