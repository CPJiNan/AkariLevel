package top.cpjinan.akarilevel.booster

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.decimal
import taboolib.common.platform.command.double
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang
import taboolib.platform.util.onlinePlayers
import java.text.SimpleDateFormat
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
                val currentPage = argument.substringAfter(" ").toIntOrNull()?.coerceIn(1, totalPages) ?: 1
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
        }.dynamic("name").decimal("multiplier").dynamic("options", optional = true) {
            execute<ProxyCommandSender> { sender, context, argument ->
                val member = context["member"]
                Booster.refreshMemberBoosters(member)
                var args = parseCommandArgs(argument.substringAfter(" "))
                val duration = args["duration"]
                val booster = Booster(
                    id = args["id"] ?: "${UUID.randomUUID()}".substringBefore("-"),
                    name = context["name"],
                    type = args["type"] ?: "",
                    multiplier = context.double("multiplier"),
                    start = System.currentTimeMillis(),
                    duration = if (duration != null) formatToDuration(duration) else -1,
                    levelGroup = args["levelGroup"] ?: "",
                    source = args["source"] ?: "COMMAND_ADD_EXP"
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

    private fun parseCommandArgs(args: String): Map<String, String> {
        return args.split(' ')
            .filter { it.startsWith("-") }
            .associate {
                it.removePrefix("-").removePrefix("-").split('=', limit = 2)
                    .run { this[0] to getOrElse(1) { "" } }
            }
    }

    private fun formatToDate(date: Long): String {
        return SimpleDateFormat(console().asLangText("DateFormatPattern"), Locale.getDefault())
            .apply { timeZone = TimeZone.getDefault() }.format(Date(date))
    }

    private fun formatToDuration(duration: Long): String {
        val totalSeconds = duration / 1000
        val days = totalSeconds / 86400
        val hours = (totalSeconds % 86400) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return buildString {
            if (days > 0) append("${days}${console().asLangText("TimeUnitDay")}")
            if (hours > 0) append("${hours}${console().asLangText("TimeUnitHour")}")
            if (minutes > 0) append("${minutes}${console().asLangText("TimeUnitMinute")}")
            if (seconds > 0 || isEmpty()) append("${seconds}${console().asLangText("TimeUnitSecond")}")
        }
    }

    private fun formatToDuration(duration: String): Long {
        return Regex("""(\d+)([dDhHmMsS]?)""")
            .findAll(duration)
            .sumOf {
                val value = it.groupValues[1].toLong()
                when (it.groupValues[2].lowercase()) {
                    "d" -> value * 24 * 60 * 60 * 1000
                    "h" -> value * 60 * 60 * 1000
                    "m" -> value * 60 * 1000
                    "s", "" -> value * 1000
                    else -> 0
                }
            }
    }
}