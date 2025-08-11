package com.github.cpjinan.plugin.akarilevel.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.entity.LevelGroupData
import com.google.gson.Gson
import taboolib.common.platform.function.submit
import java.util.concurrent.TimeUnit

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * 等级组数据缓存。
 *
 * @author 季楠
 * @since 2025/8/10 11:17
 */
private val gson = Gson()

val levelGroupCache = Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterWrite(5, TimeUnit.MINUTES)
    .removalListener<String, LevelGroupData> { key, value, cause ->
        if (key != null && value != null && cause == RemovalCause.EXPIRED) {
            submit(async = true) {
                with(Database.INSTANCE) {
                    set(levelGroupTable, key, gson.toJson(value))
                }
            }
        }
    }
    .build<String, LevelGroupData> {
        with(Database.INSTANCE) {
            get(levelGroupTable, it).takeUnless { it.isNullOrBlank() }
                ?.let { gson.fromJson(it, LevelGroupData::class.java) } ?: LevelGroupData()
        }
    }