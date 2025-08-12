package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache.gson
import com.github.cpjinan.plugin.akarilevel.cache.MemberCache.memberCache
import com.github.cpjinan.plugin.akarilevel.database.Database
import taboolib.common.platform.function.submit
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.manager
 *
 * @author QwQ-dev
 * @since 2025/8/12 16:50
 */
object PersistenceManager {
    private val dirtyMembers = ConcurrentHashMap<String, Long>()
    private val persistedCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)

    private val checkpointInterval = Duration.ofMinutes(5).toMillis()

    fun markDirty(member: String) {
        val currentTime = System.currentTimeMillis()
        dirtyMembers[member] = currentTime
    }

    fun isDirty(member: String): Boolean {
        return dirtyMembers.containsKey(member)
    }

    fun getDirtyMembersCount(): Int {
        return dirtyMembers.size
    }

    fun createCheckpoint() {
        if (dirtyMembers.isEmpty()) return

        submit(async = true) {
            val cutoffTime = System.currentTimeMillis() - checkpointInterval
            val membersToSave = mutableListOf<String>()

            dirtyMembers.entries.removeIf { (member, lastModified) ->
                if (lastModified < cutoffTime) {
                    membersToSave.add(member)
                    true
                } else false
            }

            if (membersToSave.isNotEmpty()) {
                var savedCount = 0
                membersToSave.forEach {
                    try {
                        persistMemberData(it)
                        savedCount++
                    } catch (_: Exception) {
                        errorCount.incrementAndGet()
                        markDirty(it)
                    }
                }
            }
        }
    }

    fun forcePersist(member: String): Boolean {
        return try {
            persistMemberData(member)
            dirtyMembers.remove(member)
            true
        } catch (_: Exception) {
            errorCount.incrementAndGet()
            false
        }
    }

    fun forcePersistAll() {
        if (dirtyMembers.isEmpty()) return

        val members = dirtyMembers.keys.toList()

        members.forEach {
            forcePersist(it)
        }
    }

    fun invalidateAllSafely() {
        forcePersistAll()
        memberCache.invalidateAll()
    }

    fun onServerShutdown() {
        if (dirtyMembers.isEmpty()) return

        dirtyMembers.keys.forEach {
            persistMemberData(it)
        }

        dirtyMembers.clear()
    }

    fun onPlayerQuit(member: String) {
        if (isDirty(member)) {
            submit(async = true) {
                try {
                    persistMemberData(member)
                    dirtyMembers.remove(member)
                } catch (_: Exception) {
                    errorCount.incrementAndGet()
                }
            }
        }
    }

    private fun persistMemberData(member: String) {
        val memberData = memberCache[member]
        if (memberData == null) throw NullPointerException()

        val json = gson.toJson(memberData)
        Database.Companion.INSTANCE.set(
            Database.Companion.INSTANCE.memberTable,
            member,
            json
        )
        persistedCount.incrementAndGet()
    }
}