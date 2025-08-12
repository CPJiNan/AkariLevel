package com.github.cpjinan.plugin.akarilevel.cache

import com.github.benmanes.caffeine.cache.RemovalCause.EXPIRED
import com.github.benmanes.caffeine.cache.RemovalCause.SIZE
import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import com.github.cpjinan.plugin.akarilevel.manager.PersistenceManager
import com.google.gson.Gson
import taboolib.common.platform.function.submit
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
object MemberCache {
    val gson = Gson()

    val memberCache = EasyCache.builder<String, MemberData>()
        .maximumSize(500)
        .expireAfterWrite(Duration.ofMinutes(10))
        .expireAfterAccess(Duration.ofMinutes(30))
        .circuitBreaker(
            CircuitBreakerConfig(
                failureThreshold = 15,  // 15% 失败率阈值
                timeoutMs = 60_000,     // 60 秒熔断时间
                sampleSize = 30         // 30 个样本窗口
            )
        )
        .removalListener { key, value, cause ->
            when (cause) {
                EXPIRED, SIZE -> {
                    submit(async = true) {
                        with(Database.INSTANCE) {
                            set(memberTable, key, gson.toJson(value))
                        }
                    }
                }

                else -> {}
            }
        }
        .loader { key ->
            try {
                with(Database.INSTANCE) {
                    val dbValue = get(memberTable, key)

                    dbValue?.takeUnless { it.isBlank() }
                        ?.let { json ->
                            try {
                                val memberData = gson.fromJson(json, MemberData::class.java)
                                memberData
                            } catch (_: Exception) {
                                null
                            }
                        }
                }
            } catch (_: Exception) {
                null
            }
        }
        .build()

    fun cleanupMemberCache() {
        memberCache.cleanup()
    }

    fun warmUpMemberCache(members: List<String>) {
        if (members.isNotEmpty()) {
            val warmUpData = members.mapNotNull {
                try {
                    with(Database.INSTANCE) {
                        get(memberTable, it)?.let { json ->
                            it to gson.fromJson(json, MemberData::class.java)
                        }
                    }
                } catch (_: Exception) {
                    null
                }
            }.toMap()

            memberCache.setAll(warmUpData)
        }
    }

    fun invalidateAllSafely() {
        PersistenceManager.invalidateAllSafely()
    }

    fun forcePersistMember(member: String): Boolean {
        return PersistenceManager.forcePersist(member)
    }

    fun forcePersistAllMembers() {
        PersistenceManager.forcePersistAll()
    }
}