package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache.gson
import com.github.cpjinan.plugin.akarilevel.cache.MemberCache.memberCache
import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import taboolib.common.platform.function.submit
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.manager
 *
 * 缓存管理器。
 *
 * @author QwQ-dev
 * @since 2025/8/12 16:50
 */
object CacheManager {
    private val dirtyMembers = ConcurrentHashMap<String, Long>() // 成员 -> 最后修改时间

    /**
     * 初始化缓存管理器。
     */
    fun initialize() {
        submit(async = true, period = 20 * 60 * 5) { // 20 ticks * 60 * 5 = 5 min
            if (dirtyMembers.isEmpty()) return@submit

            val cutoffTime = System.currentTimeMillis() - Duration.ofMinutes(5).toMillis()
            val membersToSave = mutableListOf<String>()

            dirtyMembers.entries.removeIf { (member, lastModified) ->
                if (lastModified < cutoffTime) {
                    membersToSave.add(member)
                    true
                } else false
            }

            if (membersToSave.isNotEmpty()) {
                membersToSave.forEach { member ->
                    try {
                        val memberData = memberCache[member] ?: memberCache.getWithBuiltInLoader(member)

                        if (memberData != null) {
                            with(Database.INSTANCE) {
                                set(memberTable, member, gson.toJson(memberData))
                            }
                        } else {
                            // 数据库中无数据，创建默认数据。
                            val defaultData = MemberData()
                            memberCache[member] = defaultData
                            with(Database.INSTANCE) {
                                set(memberTable, member, gson.toJson(defaultData))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        markDirty(member)
                    }
                }
            }
        }
    }

    /**
     * 关闭缓存管理器并保存所有数据。
     */
    fun shutdown() {
        if (dirtyMembers.isEmpty()) return

        dirtyMembers.keys.forEach { member ->
            val memberData = memberCache[member]

            if (memberData != null) {
                try {
                    val json = gson.toJson(memberData)
                    with(Database.INSTANCE) {
                        set(memberTable, member, json)
                    }
                } catch (e: Exception) {
                    throw e
                }
            } else {
                val loadedData = memberCache.getWithBuiltInLoader(member)
                if (loadedData != null) {
                    try {
                        val json = gson.toJson(loadedData)
                        with(Database.INSTANCE) {
                            set(memberTable, member, json)
                        }
                    } catch (e: Exception) {
                        throw e
                    }
                }
            }
        }

        dirtyMembers.clear()
    }

    /**
     * 标记成员数据为脏数据。
     *
     * @param member 要标记的成员。
     */
    fun markDirty(member: String) {
        val currentTime = System.currentTimeMillis()
        dirtyMembers[member] = currentTime
    }

    /**
     * 强制持久化成员数据。
     *
     * @param member 要持久化数据的成员。
     * @throws NullPointerException 如果无法获取成员数据。
     */
    fun forcePersist(member: String) {
        val memberData = memberCache[member]

        if (memberData != null) {
            try {
                val json = gson.toJson(memberData)
                with(Database.INSTANCE) {
                    set(memberTable, member, json)
                }
            } catch (e: Exception) {
                throw e
            }
        } else {
            val loadedData = memberCache.getWithBuiltInLoader(member)
            if (loadedData != null) {
                try {
                    val json = gson.toJson(loadedData)
                    with(Database.INSTANCE) {
                        set(memberTable, member, json)
                    }
                } catch (e: Exception) {
                    throw e
                }
            } else {
                throw NullPointerException(member)
            }
        }

        dirtyMembers.remove(member)
    }
}