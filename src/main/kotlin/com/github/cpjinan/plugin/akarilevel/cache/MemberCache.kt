package com.github.cpjinan.plugin.akarilevel.cache

import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import com.google.gson.Gson
import taboolib.common.platform.function.console
import taboolib.module.lang.sendError
import taboolib.module.lang.sendInfo

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * 成员数据缓存。
 *
 * @author 季楠 & QwQ-dev
 * @since 2025/8/12 04:43
 */
object MemberCache {
    val gson = Gson()

    // 2025.8.25 - 没必要过期，只存储在线玩家
    val memberCache = EasyCache.builder<String, MemberData>()
        .circuitBreaker(
            CircuitBreakerConfig(
                failureThreshold = 15,  // 15% 失败率阈值
                timeoutMs = 60_000,     // 60 秒熔断时间
                sampleSize = 30         // 30 个样本窗口
            )
        )
        .removalListener { key, _, cause ->
            console().sendInfo("缓存条目被移除，成员: $key, 原因: $cause")
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
                                console().sendError("反序列化成员数据失败: $key", e)
                                MemberData()
                            }
                        } ?: run {
                        MemberData()
                    }
                }
            } catch (e: Exception) {
                console().sendError("从数据库加载成员数据失败: $key", e)
                MemberData()
            }
        }
        .build()
}