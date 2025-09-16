package com.github.cpjinan.plugin.akarilevel.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import com.google.gson.Gson

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

    val memberCache = Caffeine.newBuilder()
        .build<String, MemberData> { key ->
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
}