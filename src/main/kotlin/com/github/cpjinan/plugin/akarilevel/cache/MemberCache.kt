package com.github.cpjinan.plugin.akarilevel.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import com.github.cpjinan.plugin.akarilevel.manager.DatabaseManager
import com.google.gson.Gson
import taboolib.common.platform.function.submit
import java.util.concurrent.TimeUnit

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * 成员数据缓存。
 *
 * @author 季楠
 * @since 2025/8/8 19:15
 */
private val gson: Gson = Gson()

val memberCache = Caffeine.newBuilder()
    .maximumSize(100)
    .refreshAfterWrite(5, TimeUnit.MINUTES)
    .removalListener<String, MemberData> { key, value, _ ->
        submit(async = true) {
            if (key != null && value != null) {
                DatabaseManager.getDatabase()[key] = gson.toJson(value)
            }
        }
    }
    .build<String, MemberData> {
        DatabaseManager.getDatabase()[it].takeUnless { it.isNullOrBlank() }
            ?.let { gson.fromJson(it, MemberData::class.java) } ?: MemberData()
    }