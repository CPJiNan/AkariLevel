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
 * 持久化管理器。
 *
 * @author QwQ-dev
 * @since 2025/8/12 16:50
 */
object PersistenceManager {
    private val dirtyMembers = ConcurrentHashMap<String, Long>()

    fun initialize() {
        submit(async = true, period = 20 * 60 * 5) {  // 20 ticks * 60 * 5 = 5 min
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
                    } catch (_: Exception) {
                        markDirty(it)
                    }
                }
            }
        }
    }

    fun shutdown() {
        if (dirtyMembers.isEmpty()) return

        dirtyMembers.keys.forEach {
            with(Database.INSTANCE) {
                set(memberTable, it, gson.toJson(memberCache[it] ?: throw NullPointerException()))
            }
        }

        dirtyMembers.clear()
    }

    fun markDirty(member: String) {
        val currentTime = System.currentTimeMillis()
        dirtyMembers[member] = currentTime
    }

    fun isDirty(member: String): Boolean {
        return dirtyMembers.containsKey(member)
    }

    fun forcePersist(member: String) {
        with(Database.INSTANCE) {
            set(memberTable, member, gson.toJson(memberCache[member] ?: throw NullPointerException()))
        }
        dirtyMembers.remove(member)
    }

    fun forcePersistAll() {
        if (dirtyMembers.isEmpty()) return

        val members = dirtyMembers.keys.toList()

        members.forEach {
            forcePersist(it)
        }
    }

    fun invalidateSafely(member: String) {
        forcePersist(member)
        memberCache.invalidate(member)
    }

    fun invalidateAllSafely() {
        forcePersistAll()
        memberCache.invalidateAll()
    }
}