package com.github.cpjinan.plugin.akarilevel.database.lock

import com.github.benmanes.caffeine.cache.stats.CacheStats
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database.lock
 *
 * @author QwQ-dev
 * @since 2025/8/12 04:43
 */
class EasyMetrics {
    private val errorCount = AtomicLong(0)
    private val errorsByType = ConcurrentHashMap<String, AtomicLong>()

    private val circuitBreakerRejects = AtomicLong(0)
    private val maintenanceCount = AtomicLong(0)
    private val warmUpCount = AtomicLong(0)
    private val warmUpItemsCount = AtomicLong(0)

    private val recentOperationTimes = ConcurrentLinkedQueue<OperationTiming>()
    private val maxRecentOperations = 1000

    @Volatile
    private var lastStatsCalculation = 0L
    private val statsUpdateIntervalMs = 5000L

    private val cachedStats = AtomicReference<EasyMetricsSnapshot?>()

    fun recordError(operationType: String, error: Throwable) {
        errorCount.incrementAndGet()

        val errorType = "${operationType}_${error.javaClass.simpleName}"
        errorsByType.computeIfAbsent(errorType) { AtomicLong(0) }.incrementAndGet()

        recordOperationTiming("ERROR_$operationType", System.nanoTime())
    }

    fun recordCircuitBreakerReject() {
        circuitBreakerRejects.incrementAndGet()
        recordOperationTiming("CIRCUIT_BREAKER_REJECT", System.nanoTime())
    }

    fun recordMaintenance() {
        maintenanceCount.incrementAndGet()
        recordOperationTiming("MAINTENANCE", System.nanoTime())
    }

    fun recordWarmUp(itemCount: Int) {
        warmUpCount.incrementAndGet()
        warmUpItemsCount.addAndGet(itemCount.toLong())
        recordOperationTiming("WARMUP", System.nanoTime(), itemCount)
    }

    fun getEnhancedSnapshot(caffeineStats: CacheStats): EasyMetricsSnapshot {
        val currentTime = System.currentTimeMillis()
        val cached = cachedStats.get()

        if (cached != null && (currentTime - lastStatsCalculation) < statsUpdateIntervalMs) {
            return cached
        }

        val snapshot = calculateEnhancedSnapshot(caffeineStats, currentTime)
        cachedStats.set(snapshot)
        lastStatsCalculation = currentTime

        return snapshot
    }

    private fun calculateEnhancedSnapshot(caffeineStats: CacheStats, currentTime: Long): EasyMetricsSnapshot {
        return EasyMetricsSnapshot(
            timestamp = currentTime,

            hitCount = caffeineStats.hitCount(),
            missCount = caffeineStats.missCount(),
            loadCount = caffeineStats.loadCount(),
            evictionCount = caffeineStats.evictionCount(),
            averageLoadTime = caffeineStats.averageLoadPenalty() / 1_000_000.0, // 转换为毫秒

            hitRate = if (caffeineStats.requestCount() > 0)
                (caffeineStats.hitRate() * 100.0) else 0.0,

            errorCount = errorCount.get(),
            circuitBreakerRejects = circuitBreakerRejects.get(),
            maintenanceCount = maintenanceCount.get(),
            warmUpCount = warmUpCount.get(),
            warmUpItemsCount = warmUpItemsCount.get(),

            operationsPerSecond = getOperationsPerSecond(),
            errorRate = getErrorRate(caffeineStats),
            errorBreakdown = getErrorBreakdown()
        )
    }

    private fun getOperationsPerSecond(): Double {
        val now = System.currentTimeMillis()
        val recentOps = recentOperationTimes.filter { now - it.timestamp < 60_000 }

        return if (recentOps.isNotEmpty()) {
            val timeSpanMs = now - recentOps.minOf { it.timestamp }
            if (timeSpanMs > 0) (recentOps.size.toDouble() / timeSpanMs) * 1000.0 else 0.0
        } else 0.0
    }

    private fun getErrorRate(caffeineStats: CacheStats): Double {
        val errors = errorCount.get()
        val totalOps = caffeineStats.requestCount() + errors
        return if (totalOps > 0) (errors.toDouble() / totalOps) * 100.0 else 0.0
    }

    private fun getErrorBreakdown(): Map<String, Long> {
        return errorsByType.mapValues { it.value.get() }.toMap()
    }

    fun exportForMonitoring(caffeineStats: CacheStats): Map<String, Any> {
        val snapshot = getEnhancedSnapshot(caffeineStats)

        return mapOf(
            "cache_hits_total" to snapshot.hitCount,
            "cache_misses_total" to snapshot.missCount,
            "cache_loads_total" to snapshot.loadCount,
            "cache_evictions_total" to snapshot.evictionCount,
            "cache_hit_rate_percent" to snapshot.hitRate,
            "cache_operations_per_second" to snapshot.operationsPerSecond,
            "cache_average_load_time_ms" to snapshot.averageLoadTime,
            "cache_error_rate_percent" to snapshot.errorRate,
            "cache_errors_total" to snapshot.errorCount,
            "cache_circuit_breaker_rejects_total" to snapshot.circuitBreakerRejects,
            "cache_maintenance_operations_total" to snapshot.maintenanceCount,
            "cache_warmup_operations_total" to snapshot.warmUpCount,
            "cache_warmup_items_total" to snapshot.warmUpItemsCount
        )
    }

    private fun recordOperationTiming(operationType: String, timeNanos: Long, count: Int = 1) {
        val timing = OperationTiming(operationType, System.currentTimeMillis(), timeNanos, count)
        recentOperationTimes.offer(timing)

        while (recentOperationTimes.size > maxRecentOperations) {
            recentOperationTimes.poll()
        }
    }

    fun reset() {
        errorCount.set(0)
        errorsByType.clear()
        circuitBreakerRejects.set(0)
        maintenanceCount.set(0)
        warmUpCount.set(0)
        warmUpItemsCount.set(0)
        recentOperationTimes.clear()
        cachedStats.set(null)
    }
}

data class EasyMetricsSnapshot(
    val timestamp: Long,

    val hitCount: Long,
    val missCount: Long,
    val loadCount: Long,
    val evictionCount: Long,
    val averageLoadTime: Double,

    val hitRate: Double,

    val errorCount: Long,
    val circuitBreakerRejects: Long,
    val maintenanceCount: Long,
    val warmUpCount: Long,
    val warmUpItemsCount: Long,
    val operationsPerSecond: Double,
    val errorRate: Double,
    val errorBreakdown: Map<String, Long>
) {
    fun format(): String {
        return buildString {
            appendLine("=== Easy Cache Metrics ===")
            appendLine("Timestamp: ${java.time.Instant.ofEpochMilli(timestamp)}")
            appendLine("Hit Count: $hitCount")
            appendLine("Miss Count: $missCount")
            appendLine("Hit Rate: ${String.format("%.2f", hitRate)}%")
            appendLine("Load Count: $loadCount")
            appendLine("Operations/Second: ${String.format("%.2f", operationsPerSecond)}")
            appendLine("Average Load Time: ${String.format("%.2f", averageLoadTime)}ms")
            appendLine("Error Count: $errorCount")
            appendLine("Error Rate: ${String.format("%.2f", errorRate)}%")
            appendLine("Circuit Breaker Rejects: $circuitBreakerRejects")
            appendLine("Maintenance Operations: $maintenanceCount")
            appendLine("Warm-up Operations: $warmUpCount ($warmUpItemsCount items)")

            if (errorBreakdown.isNotEmpty()) {
                appendLine("Error Breakdown:")
                errorBreakdown.entries.sortedByDescending { it.value }.take(10).forEach { (type, count) ->
                    appendLine("  $type: $count")
                }
            }
        }
    }
}

private data class OperationTiming(
    val operationType: String,
    val timestamp: Long,
    val durationNanos: Long,
    val count: Int = 1
)