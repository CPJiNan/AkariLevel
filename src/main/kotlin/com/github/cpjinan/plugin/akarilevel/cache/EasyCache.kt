package com.github.cpjinan.plugin.akarilevel.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.time.Duration

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * @author QwQ-dev
 * @since 2025/8/12 17:45
 */
class EasyCache<K : Any, V : Any> private constructor(
    private val caffeineCache: Cache<K, V>,
    private val loadingCache: LoadingCache<K, V>?,
    private val circuitBreaker: CircuitBreaker?,
    private val metrics: CacheMetrics?
) : com.github.cpjinan.plugin.akarilevel.cache.Cache<K, V> {
    override fun get(key: K): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics?.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = caffeineCache.getIfPresent(key)
            if (result != null) {
                metrics?.recordHit()
                circuitBreaker?.recordSuccess()
            } else {
                metrics?.recordMiss()
            }
            result
        } catch (e: Exception) {
            metrics?.recordError("GET", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    override fun set(key: K, value: V) {
        if (circuitBreaker?.canExecute() == false) {
            metrics?.recordCircuitBreakerReject()
            return
        }

        try {
            caffeineCache.put(key, value)
            circuitBreaker?.recordSuccess()
        } catch (e: Exception) {
            metrics?.recordError("PUT", e)
            circuitBreaker?.recordFailure()
        }
    }

    override fun get(key: K, loader: (K) -> V?): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics?.recordCircuitBreakerReject()
            return null
        }

        return try {
            val startTime = System.nanoTime()
            val result = caffeineCache.get(key) { loader(it) }
            val loadTime = (System.nanoTime() - startTime) / 1_000_000

            metrics?.recordLoad(loadTime)
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics?.recordLoadException()
            metrics?.recordError("GET_WITH_LOADER", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    override fun getAll(keys: Iterable<K>): Map<K, V> {
        if (circuitBreaker?.canExecute() == false) {
            metrics?.recordCircuitBreakerReject()
            return emptyMap()
        }

        return try {
            val result = caffeineCache.getAllPresent(keys)
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics?.recordError("GET_ALL", e)
            circuitBreaker?.recordFailure()
            emptyMap()
        }
    }

    override fun setAll(entries: Map<K, V>) {
        if (circuitBreaker?.canExecute() == false) {
            metrics?.recordCircuitBreakerReject()
            return
        }

        try {
            caffeineCache.putAll(entries)
            circuitBreaker?.recordSuccess()
        } catch (e: Exception) {
            metrics?.recordError("PUT_ALL", e)
            circuitBreaker?.recordFailure()
        }
    }

    override fun invalidate(key: K) {
        try {
            caffeineCache.invalidate(key)
        } catch (e: Exception) {
            metrics?.recordError("INVALIDATE", e)
        }
    }

    override fun invalidateAll(keys: Iterable<K>) {
        try {
            caffeineCache.invalidateAll(keys)
        } catch (e: Exception) {
            metrics?.recordError("INVALIDATE_ALL", e)
        }
    }

    override fun invalidateAll() {
        try {
            caffeineCache.invalidateAll()
        } catch (e: Exception) {
            metrics?.recordError("INVALIDATE_ALL_KEYS", e)
        }
    }

    override fun stats(): CacheStats {
        val caffeineStats = caffeineCache.stats()
        return CacheStats(
            hitCount = caffeineStats.hitCount(),
            missCount = caffeineStats.missCount(),
            loadCount = caffeineStats.loadCount(),
            loadExceptionCount = 0,
            totalLoadTime = caffeineStats.totalLoadTime(),
            evictionCount = caffeineStats.evictionCount()
        )
    }

    override fun size(): Long = caffeineCache.estimatedSize()

    override fun cleanup() {
        try {
            caffeineCache.cleanUp()
        } catch (e: Exception) {
            metrics?.recordError("CLEANUP", e)
        }
    }

    fun getMetrics(): CacheMetrics? = metrics

    fun getCircuitBreaker(): CircuitBreaker? = circuitBreaker

    fun getWithBuiltInLoader(key: K): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics?.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = loadingCache?.get(key)
            if (result != null) {
                circuitBreaker?.recordSuccess()
            }
            result
        } catch (e: Exception) {
            metrics?.recordError("GET_WITH_BUILT_IN_LOADER", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    fun asMap(): MutableMap<K, V> = caffeineCache.asMap()

    fun compute(key: K, remappingFunction: (K, V?) -> V?): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics?.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = caffeineCache.asMap().compute(key, remappingFunction)
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics?.recordError("COMPUTE", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    class Builder<K : Any, V : Any> {
        private var maximumSize: Long = 10_000
        private var expireAfterWrite: Duration? = null
        private var expireAfterAccess: Duration? = null
        private var refreshAfterWrite: Duration? = null
        private var recordStats = true
        private var circuitBreakerConfig: CircuitBreakerConfig? = null
        private var removalListener: ((K, V, String) -> Unit)? = null
        private var loader: ((K) -> V?)? = null
        private var enableMetrics = true

        fun maximumSize(size: Long) = apply { this.maximumSize = size }
        fun expireAfterWrite(duration: Duration) = apply { this.expireAfterWrite = duration }
        fun expireAfterAccess(duration: Duration) = apply { this.expireAfterAccess = duration }
        fun refreshAfterWrite(duration: Duration) = apply { this.refreshAfterWrite = duration }
        fun recordStats(enabled: Boolean) = apply { this.recordStats = enabled }
        fun circuitBreaker(config: CircuitBreakerConfig) = apply { this.circuitBreakerConfig = config }
        fun removalListener(listener: (K, V, String) -> Unit) = apply { this.removalListener = listener }
        fun loader(loader: (K) -> V?) = apply { this.loader = loader }
        fun enableMetrics(enabled: Boolean) = apply { this.enableMetrics = enabled }

        fun build(): EasyCache<K, V> {
            val caffeineBuilder = Caffeine.newBuilder().maximumSize(maximumSize)

            if (recordStats) caffeineBuilder.recordStats()
            expireAfterWrite?.let { caffeineBuilder.expireAfterWrite(it) }
            expireAfterAccess?.let { caffeineBuilder.expireAfterAccess(it) }
            refreshAfterWrite?.let { caffeineBuilder.refreshAfterWrite(it) }

            removalListener?.let { listener ->
                caffeineBuilder.removalListener<K, V> { key, value, cause ->
                    if (key != null && value != null) {
                        listener(key, value, cause.name)
                    }
                }
            }

            val cache: Cache<K, V>
            val loadingCache: LoadingCache<K, V>?

            if (loader != null) {
                val loaderFunc = loader!!
                loadingCache = caffeineBuilder.build { key -> loaderFunc(key) }
                cache = loadingCache
            } else {
                cache = caffeineBuilder.build()
                loadingCache = null
            }

            val circuitBreaker = circuitBreakerConfig?.let { FastCircuitBreaker(it) }
            val metrics = if (enableMetrics) FastCacheMetrics() else null

            return EasyCache(cache, loadingCache, circuitBreaker, metrics)
        }
    }

    companion object {
        fun <K : Any, V : Any> builder(): Builder<K, V> = Builder()
    }
}