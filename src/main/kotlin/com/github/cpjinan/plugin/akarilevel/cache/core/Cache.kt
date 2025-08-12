package com.github.cpjinan.plugin.akarilevel.cache.core

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache.core
 *
 * @author QwQ-dev
 * @since 2025/8/12 17:30
 */
interface Cache<K : Any, V : Any> {
    operator fun get(key: K): V?

    operator fun set(key: K, value: V)

    fun get(key: K, loader: (K) -> V?): V?

    fun getAll(keys: Iterable<K>): Map<K, V>

    fun setAll(entries: Map<K, V>)

    fun invalidate(key: K)

    fun invalidateAll(keys: Iterable<K>)

    fun invalidateAll()

    fun stats(): CacheStats

    fun size(): Long

    fun cleanup()
}

data class CacheStats(
    val hitCount: Long,
    val missCount: Long,
    val loadCount: Long,
    val loadExceptionCount: Long,
    val totalLoadTime: Long,
    val evictionCount: Long
) {
    val hitRate: Double get() = if (requestCount == 0L) 1.0 else hitCount.toDouble() / requestCount
    val missRate: Double get() = if (requestCount == 0L) 0.0 else missCount.toDouble() / requestCount
    val requestCount: Long get() = hitCount + missCount
    val averageLoadPenalty: Double get() = if (loadCount == 0L) 0.0 else totalLoadTime.toDouble() / loadCount
    
    fun format(): String {
        return "CacheStats[hits=$hitCount, misses=$missCount, hitRate=${String.format("%.2f", hitRate * 100)}%, loads=$loadCount, evictions=$evictionCount]"
    }
}