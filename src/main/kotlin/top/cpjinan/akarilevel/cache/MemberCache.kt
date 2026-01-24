package top.cpjinan.akarilevel.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import top.cpjinan.akarilevel.database.Database
import top.cpjinan.akarilevel.entity.MemberData

/**
 * AkariLevel
 * top.cpjinan.akarilevel.cache
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
                Database.instance.get(Database.instance.memberTable, key)?.takeUnless { it.isBlank() }
                    ?.let { json ->
                        try {
                            gson.fromJson(json, MemberData::class.java)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            MemberData()
                        }
                    } ?: MemberData()
            } catch (e: Exception) {
                e.printStackTrace()
                MemberData()
            }
        }
}