package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache.gson
import com.github.cpjinan.plugin.akarilevel.cache.MemberCache.memberCache
import com.github.cpjinan.plugin.akarilevel.database.Database
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
     *
     * @throws NullPointerException 如果成员数据为 null。
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
                membersToSave.forEach {
                    try {
                        with(Database.INSTANCE) {
                            set(memberTable, it, gson.toJson(memberCache[it] ?: throw NullPointerException()))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        markDirty(it)
                    }
                }
            }
        }
    }

    /**
     * 关闭缓存管理器并保存所有数据。
     *
     * @throws NullPointerException 如果成员数据为 null。
     */
    fun shutdown() {
        if (dirtyMembers.isEmpty()) return

        dirtyMembers.keys.forEach {
            val memberData = memberCache[it]
            if (memberData != null) {
                try {
                    val json = gson.toJson(memberData)
                    with(Database.INSTANCE) {
                        set(memberTable, it, json)
                    }
                } catch (e: Exception) {
                    throw e
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
     * 检查成员数据是否为脏数据。
     *
     * @param member 要检查的成员。
     * @return 如果成员数据为脏数据，则返回 true。
     */
    fun isDirty(member: String): Boolean {
        return dirtyMembers.containsKey(member)
    }

    /**
     * 强制持久化成员数据。
     *
     * @param member 要持久化数据的成员。
     * @throws NullPointerException 如果成员数据为 null。
     */
    fun forcePersist(member: String) {
        with(Database.INSTANCE) {
            set(memberTable, member, gson.toJson(memberCache[member] ?: throw NullPointerException()))
        }
        dirtyMembers.remove(member)
    }

    /**
     * 强制持久化所有成员数据。
     */
    fun forcePersistAll() {
        if (dirtyMembers.isEmpty()) return

        val members = dirtyMembers.keys.toList()

        members.forEach {
            forcePersist(it)
        }
    }

    /**
     * 持久化成员数据并使缓存失效。
     *
     * @param member 要持久化数据的成员。
     */
    fun invalidateSafely(member: String) {
        forcePersist(member)
        memberCache.invalidate(member)
    }

    /**
     * 持久化所有成员数据并使缓存失效。
     */
    fun invalidateAllSafely() {
        forcePersistAll()
        memberCache.invalidateAll()
    }
}