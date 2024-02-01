package com.github.cpjinan.plugin.akarilevel.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * time util
 * @author CPJiNan
 * @since 2024/01/15
 */
object TimeUtil {
    /**
     * parse time to seconds
     * @param [second] second unit
     * @param [minute] minute unit
     * @param [hour] hour unit
     * @param [day] day unit
     * @param [week] week unit
     * @param [month] month unit
     * @param [year] year unit
     * @return [Long] result
     */
    fun String?.parseTime(
        second: String = "s",
        minute: String = "m",
        hour: String = "H",
        day: String = "d",
        week: String = "w",
        month: String = "M",
        year: String = "y"
    ): Long {
        return this?.takeIf { it.isNotBlank() }
            ?.let {
                val (value, unit) = Regex("^(\\d+)($second|$minute|$hour|$day|$week|$month|$year)$")
                    .matchEntire(it)?.destructured
                    ?: return 0
                val unitMultiplier = when (unit) {
                    second -> 1
                    minute -> 60
                    hour -> 3600
                    day -> 86400
                    week -> 86400 * 7
                    month -> 86400 * 30
                    year -> 86400 * 365
                    else -> throw IllegalArgumentException("Invalid time unit: $unit")
                }
                value.toLong() * unitMultiplier
            } ?: 0
    }

    /**
     * LocalDate format to String
     * @param [pattern]
     * @return [String]
     */
    fun LocalDate?.formatToString(pattern: String = "yyyy-MM-dd"): String {
        return this?.format(DateTimeFormatter.ofPattern(pattern)) ?: ""
    }

    /**
     * LocalDateTime format to String
     * @param [pattern]
     * @return [String]
     */
    fun LocalDateTime?.formatToString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        return this?.format(DateTimeFormatter.ofPattern(pattern)) ?: ""
    }

    /**
     * String format to LocalDate
     * @param [pattern]
     * @return [LocalDate?]
     */
    fun String?.formatToLocalDate(pattern: String = "yyyy-MM-dd"): LocalDate? {
        return this?.takeIf { it.isNotBlank() }
            ?.runCatching { LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern)) }
            ?.getOrNull()
    }

    /**
     * String format to LocalDateTime
     * @param [pattern]
     * @return [LocalDateTime?]
     */
    fun String?.formatToLocalDateTime(pattern: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime? {
        return this?.takeIf { it.isNotBlank() }
            ?.runCatching { LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern)) }
            ?.getOrNull()
    }
}