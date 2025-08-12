package com.github.cpjinan.plugin.akarilevel.cache

import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.database.DatabaseMySQL
import com.github.cpjinan.plugin.akarilevel.database.lock.CircuitBreakerConfig
import com.github.cpjinan.plugin.akarilevel.database.lock.EasyCache
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import com.google.gson.Gson
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import java.time.Duration

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * 成员数据缓存。
 *
 * @author 季楠, QwQ-dev
 * @since 2025/8/12 04:43
 */
private val gson = Gson()

val memberCache = EasyCache.builder<String, MemberData>()
    .maximumSize(500)
    .expireAfterWrite(Duration.ofMinutes(10))
    .expireAfterAccess(Duration.ofMinutes(30))
    .recordStats(true)
    .circuitBreaker(
        CircuitBreakerConfig(
            failureThreshold = 15,  // 15% 失败率阈值
            timeoutMs = 60_000,     // 60 秒熔断时间
            sampleSize = 30         // 30 个样本窗口
        )
    )
    .removalListener { key, value, cause ->
        if (key != null && value != null) {
            when (cause.name) {
                "EXPIRED", "SIZE" -> {
                    submit(async = true) {
                        try {
                            with(Database.INSTANCE) {
                                set(memberTable, key, gson.toJson(value))
                            }
                        } catch (e: Exception) {
                            warning("Failed to persist member data during eviction: $key", e)
                        }
                    }
                }

                else -> {}
            }
        }
    }
    .loader { key ->
        try {
            with(Database.INSTANCE) {
                get(memberTable, key).takeUnless { it.isNullOrBlank() }
                    ?.let { gson.fromJson(it, MemberData::class.java) } ?: MemberData()
            }
        } catch (e: Exception) {
            warning("Failed to load member data: $key", e)
            MemberData()
        }
    }
    .build()

fun getMemberCacheStats(): String {
    return memberCache.stats().format()
}

fun exportMemberCacheMetrics(): Map<String, Any> {
    return memberCache.exportForMonitoring()
}

fun cleanupMemberCache() {
    memberCache.cleanUp()
}

fun warmUpMemberCache(memberKeys: List<String>) {
    if (memberKeys.isNotEmpty()) {
        memberCache.warmUp {
            memberKeys.mapNotNull { key ->
                try {
                    with(Database.INSTANCE) {
                        get(memberTable, key)?.let { json ->
                            key to gson.fromJson(json, MemberData::class.java)
                        }
                    }
                } catch (e: Exception) {
                    warning("Failed to warm up member data: $key", e)
                    null
                }
            }.toMap()
        }
    }
}

fun getEasyDatabaseStats(): String? {
    return if (Database.INSTANCE is DatabaseMySQL) {
        (Database.INSTANCE as DatabaseMySQL).getCacheStats()
    } else {
        null
    }
}