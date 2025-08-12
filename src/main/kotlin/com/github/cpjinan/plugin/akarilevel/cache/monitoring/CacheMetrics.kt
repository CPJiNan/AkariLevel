package com.github.cpjinan.plugin.akarilevel.cache.monitoring

import java.util.concurrent.atomic.AtomicLong

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache.monitoring
 *
 * @author QwQ-dev
 * @since 2025/8/12 17:40
 */
interface CacheMetrics {
    fun recordHit()
    fun recordMiss() 
    fun recordLoad(loadTime: Long)
    fun recordLoadException()
    fun recordEviction()
    fun recordError(operation: String, exception: Exception)
    fun recordCircuitBreakerReject()
    
    fun getSnapshot(): MetricsSnapshot
    fun exportForMonitoring(): Map<String, Any>
    fun reset()
}

data class MetricsSnapshot(
    val hitCount: Long,
    val missCount: Long,
    val loadCount: Long,
    val loadExceptionCount: Long,
    val totalLoadTime: Long,
    val evictionCount: Long,
    val errorCount: Long,
    val circuitBreakerRejects: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    val hitRate: Double get() = if (requestCount == 0L) 1.0 else hitCount.toDouble() / requestCount
    val requestCount: Long get() = hitCount + missCount
    val averageLoadTime: Double get() = if (loadCount == 0L) 0.0 else totalLoadTime.toDouble() / loadCount
}

class FastCacheMetrics : CacheMetrics {
    private val hitCount = AtomicLong(0)
    private val missCount = AtomicLong(0)
    private val loadCount = AtomicLong(0)
    private val loadExceptionCount = AtomicLong(0)
    private val totalLoadTime = AtomicLong(0)
    private val evictionCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)
    private val circuitBreakerRejects = AtomicLong(0)
    
    override fun recordHit() {
        hitCount.incrementAndGet()
    }
    
    override fun recordMiss() {
        missCount.incrementAndGet()
    }
    
    override fun recordLoad(loadTime: Long) {
        loadCount.incrementAndGet()
        totalLoadTime.addAndGet(loadTime)
    }
    
    override fun recordLoadException() {
        loadExceptionCount.incrementAndGet()
    }
    
    override fun recordEviction() {
        evictionCount.incrementAndGet()
    }
    
    override fun recordError(operation: String, exception: Exception) {
        errorCount.incrementAndGet()
        // more logs?
    }
    
    override fun recordCircuitBreakerReject() {
        circuitBreakerRejects.incrementAndGet()
    }
    
    override fun getSnapshot(): MetricsSnapshot {
        return MetricsSnapshot(
            hitCount = hitCount.get(),
            missCount = missCount.get(),
            loadCount = loadCount.get(),
            loadExceptionCount = loadExceptionCount.get(),
            totalLoadTime = totalLoadTime.get(),
            evictionCount = evictionCount.get(),
            errorCount = errorCount.get(),
            circuitBreakerRejects = circuitBreakerRejects.get()
        )
    }
    
    override fun exportForMonitoring(): Map<String, Any> {
        val snapshot = getSnapshot()
        return mapOf(
            "cache.hits" to snapshot.hitCount,
            "cache.misses" to snapshot.missCount,
            "cache.hit_rate" to snapshot.hitRate,
            "cache.loads" to snapshot.loadCount,
            "cache.load_exceptions" to snapshot.loadExceptionCount,
            "cache.average_load_time_ms" to snapshot.averageLoadTime,
            "cache.evictions" to snapshot.evictionCount,
            "cache.errors" to snapshot.errorCount,
            "cache.circuit_breaker_rejects" to snapshot.circuitBreakerRejects,
            "cache.requests_total" to snapshot.requestCount,
            "timestamp" to snapshot.timestamp
        )
    }
    
    override fun reset() {
        hitCount.set(0)
        missCount.set(0)
        loadCount.set(0)
        loadExceptionCount.set(0)
        totalLoadTime.set(0)
        evictionCount.set(0)
        errorCount.set(0)
        circuitBreakerRejects.set(0)
    }

    fun enhanceWithCaffeineStats(caffeineStats: com.github.benmanes.caffeine.cache.stats.CacheStats): MetricsSnapshot {
        return MetricsSnapshot(
            hitCount = caffeineStats.hitCount(),
            missCount = caffeineStats.missCount(),
            loadCount = caffeineStats.loadCount(),
            loadExceptionCount = 0,
            totalLoadTime = caffeineStats.totalLoadTime(),
            evictionCount = caffeineStats.evictionCount(),
            errorCount = errorCount.get(),
            circuitBreakerRejects = circuitBreakerRejects.get()
        )
    }
}