package com.github.cpjinan.plugin.akarilevel.cache

import com.github.benmanes.caffeine.cache.RemovalCause.EXPIRED
import com.github.benmanes.caffeine.cache.RemovalCause.SIZE
import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import com.github.cpjinan.plugin.akarilevel.manager.CacheManager
import com.google.gson.Gson
import taboolib.common.platform.function.submit

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
                        var retryCount = 0
                        val maxRetries = 3

                        while (retryCount < maxRetries) {
                            try {
                                with(Database.INSTANCE) {
                                    set(memberTable, key, gson.toJson(value))
                                }
                                break
                            } catch (e: Exception) {
                                retryCount++
                                e.printStackTrace()

                                if (retryCount >= maxRetries) {
                                    e.printStackTrace()
                                    CacheManager.markDirty(key)
                                } else {
                                    Thread.sleep(1000L * retryCount)
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
        .loader { key ->
            try {
                with(Database.INSTANCE) {
                    get(memberTable, key)?.takeUnless { it.isBlank() }
                        ?.let { json ->
                            try {
                                val memberData = gson.fromJson(json, MemberData::class.java)
                                memberData
                            } catch (e: Exception) {
                                e.printStackTrace()
                                MemberData()
                            }
                        } ?: MemberData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                MemberData()
            }
        }
        .build()
}