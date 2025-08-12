package com.github.cpjinan.plugin.akarilevel.cache

import com.github.cpjinan.plugin.akarilevel.database.Database
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * @author QwQ-dev
 * @since 2025/8/12 16:50
 */
object SmartPersistenceManager {
    private val dirtyPlayers = ConcurrentHashMap<String, Long>() // 玩家 -> 最后修改时间
    private val persistedCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)

    private val checkpointInterval = Duration.ofMinutes(5).toMillis()

    fun markDirty(player: String) {
        val currentTime = System.currentTimeMillis()
        dirtyPlayers[player] = currentTime
    }

    fun isDirty(player: String): Boolean {
        return dirtyPlayers.containsKey(player)
    }

    fun getDirtyPlayersCount(): Int {
        return dirtyPlayers.size
    }

    fun onPlayerQuit(player: String) {
        if (isDirty(player)) {
            submit(async = true) {
                try {
                    persistPlayerData(player, "PLAYER_QUIT")
                    dirtyPlayers.remove(player)
                    info("Persisted data for player $player on quit")
                } catch (e: Exception) {
                    warning("Failed to persist data for player $player on quit", e)
                    errorCount.incrementAndGet()
                }
            }
        }
    }

    fun createCheckpoint() {
        if (dirtyPlayers.isEmpty()) return

        submit(async = true) {
            val cutoffTime = System.currentTimeMillis() - checkpointInterval
            val playersToSave = mutableListOf<String>()

            dirtyPlayers.entries.removeIf { (player, lastModified) ->
                if (lastModified < cutoffTime) {
                    playersToSave.add(player)
                    true
                } else false
            }

            if (playersToSave.isNotEmpty()) {
                var savedCount = 0
                playersToSave.forEach { player ->
                    try {
                        persistPlayerData(player, "CHECKPOINT")
                        savedCount++
                    } catch (e: Exception) {
                        warning("Failed to persist checkpoint data for player $player", e)
                        errorCount.incrementAndGet()
                        markDirty(player)
                    }
                }

                if (savedCount > 0) {
                    info("Checkpoint: persisted data for $savedCount players")
                }
            }
        }
    }

    fun onServerShutdown() {
        if (dirtyPlayers.isEmpty()) {
            info("No dirty data to persist on server shutdown")
            return
        }

        info("Server shutdown: persisting data for ${dirtyPlayers.size} players...")
        var savedCount = 0
        var failedCount = 0

        dirtyPlayers.keys.forEach { player ->
            try {
                persistPlayerData(player, "SERVER_SHUTDOWN")
                savedCount++
            } catch (e: Exception) {
                warning("Failed to persist shutdown data for player $player", e)
                failedCount++
            }
        }

        info("Server shutdown persistence completed: $savedCount saved, $failedCount failed")
        dirtyPlayers.clear()
    }

    fun forcePersist(player: String): Boolean {
        return try {
            persistPlayerData(player, "MANUAL")
            dirtyPlayers.remove(player)
            info("Manually persisted data for player $player")
            true
        } catch (e: Exception) {
            warning("Failed to manually persist data for player $player", e)
            errorCount.incrementAndGet()
            false
        }
    }

    fun forcePersistAll(): Int {
        if (dirtyPlayers.isEmpty()) return 0

        val players = dirtyPlayers.keys.toList()
        var savedCount = 0

        players.forEach { player ->
            if (forcePersist(player)) {
                savedCount++
            }
        }

        info("Manually persisted data for $savedCount/${players.size} players")
        return savedCount
    }

    fun safeInvalidateAll() {
        info("Safe invalidate: persisting ${dirtyPlayers.size} dirty players...")

        forcePersistAll()

        memberCache.invalidateAll()
        info("Safe invalidate completed")
    }

    private fun persistPlayerData(player: String, reason: String) {
        val memberData = memberCache[player]
        if (memberData != null) {
            try {
                val json = gson.toJson(memberData)
                Database.INSTANCE.set(
                    Database.INSTANCE.memberTable,
                    player,
                    json
                )
                persistedCount.incrementAndGet()
            } catch (e: Exception) {
                warning("Database persistence failed for player $player (reason: $reason)", e)
                throw e
            }
        } else {
            warning("No member data found for player $player during persistence (reason: $reason)")
        }
    }

    fun getStats(): String {
        return buildString {
            appendLine("=== Smart Persistence Manager Stats ===")
            appendLine("Dirty Players: ${dirtyPlayers.size}")
            appendLine("Total Persisted: ${persistedCount.get()}")
            appendLine("Total Errors: ${errorCount.get()}")
            appendLine("Checkpoint Interval: ${checkpointInterval / 1000}s")

            if (dirtyPlayers.isNotEmpty()) {
                appendLine("Oldest Dirty Data: ${System.currentTimeMillis() - dirtyPlayers.values.minOrNull()!!}ms ago")
            }
        }
    }

    fun resetStats() {
        persistedCount.set(0)
        errorCount.set(0)
        info("Persistence statistics reset")
    }
}