package top.cpjinan.akarilevel.utils

import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText
import java.text.SimpleDateFormat
import java.util.*

/**
 * AkariLevel
 * top.cpjinan.akarilevel.utils
 *
 * 时间工具类。
 *
 * @author 季楠
 * @since 2025/12/14 09:53
 */
object TimeUtils {
    fun formatToDate(date: Long): String {
        return SimpleDateFormat(console().asLangText("DateFormatPattern"), Locale.getDefault())
            .apply { timeZone = TimeZone.getDefault() }.format(Date(date))
    }

    fun formatToDuration(duration: Long): String {
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

    fun formatToDuration(duration: String): Long {
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