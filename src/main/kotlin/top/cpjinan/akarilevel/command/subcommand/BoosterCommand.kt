package top.cpjinan.akarilevel.command.subcommand

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang
import taboolib.platform.util.onlinePlayers
import top.cpjinan.akarilevel.booster.Booster
import java.text.SimpleDateFormat
import java.util.*

/**
 * AkariLevel
 * top.cpjinan.akarilevel.command
 *
 * 经验加成器命令。
 *
 * @author 季楠
 * @since 2025/12/7 14:32
 */
object BoosterCommand {
    val booster = subCommand {
        // 查看经验加成器信息命令。
        literal("info") {
            dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
            }.dynamic("booster") {
                execute<ProxyCommandSender> { sender, context, _ ->
                    val member = context["member"]
                    val id = UUID.fromString(context["booster"])
                    Booster.refreshMemberBoosters(member)
                    val booster = Booster.getMemberBoosters(member)[id]
                    if (booster == null) {
                        sender.sendLang("BoosterNotFound", "$id")
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
                            if (Booster.isMemberBoosterEnabled(member, id)) "BoosterInfoEnabled"
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
        }

        // 查看经验加成器列表命令。
        literal("list") {
            dynamic("member") {
                suggestUncheck { onlinePlayers.map { it.name } }
                execute<ProxyCommandSender> { sender, _, content ->
                    val member = content.substringBefore(" ")
                    val pageSize = 10
                    Booster.refreshMemberBoosters(member)
                    val boosters = Booster.getMemberBoosters(member).values.sortedBy { it.name }
                    val totalPages = (boosters.size + pageSize - 1) / pageSize
                    val currentPage = content.substringAfter(" ").toIntOrNull()?.coerceIn(1, totalPages) ?: 1
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
}