package com.github.cpjinan.plugin.akarilevel.database.lock

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.benmanes.caffeine.cache.RemovalListener
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import java.time.Duration

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database.lock
 *
 * @author QwQ-dev
 * @since 2025/8/12 04:43
 */
class EasyCache<K : Any, V : Any> internal constructor(
    private val caffeineCache: Cache<K, V>,
    private val loadingCache: LoadingCache<K, V>?,
    config: EasyCacheConfig<K, V>
) : AutoCloseable {
    private val metrics = EasyMetrics()
    private val circuitBreaker = config.circuitBreakerConfig?.let { CacheCircuitBreaker(it) }

    companion object {
        fun <K : Any, V : Any> builder(): EasyCacheBuilder<K, V> {
            return EasyCacheBuilder()
        }
    }

    operator fun get(key: K): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = caffeineCache.getIfPresent(key)
            if (result != null) {
                circuitBreaker?.recordSuccess()
            }
            result
        } catch (e: Exception) {
            metrics.recordError("GET", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    fun get(key: K, loader: (K) -> V?): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = caffeineCache.get(key) { loader(it) }
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics.recordError("GET_WITH_LOADER", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    fun put(key: K, value: V) {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return
        }

        try {
            caffeineCache.put(key, value)
            circuitBreaker?.recordSuccess()
        } catch (e: Exception) {
            metrics.recordError("PUT", e)
            circuitBreaker?.recordFailure()
        }
    }

    fun putAll(entries: Map<K, V>) {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return
        }

        try {
            caffeineCache.putAll(entries)
            circuitBreaker?.recordSuccess()
        } catch (e: Exception) {
            metrics.recordError("PUT_ALL", e)
            circuitBreaker?.recordFailure()
        }
    }

    fun invalidate(key: K) {
        try {
            caffeineCache.invalidate(key)
        } catch (e: Exception) {
            metrics.recordError("INVALIDATE", e)
        }
    }

    fun invalidateAll(keys: Iterable<K>) {
        try {
            caffeineCache.invalidateAll(keys)
        } catch (e: Exception) {
            metrics.recordError("INVALIDATE_ALL", e)
        }
    }

    fun invalidateAll() {
        try {
            caffeineCache.invalidateAll()
        } catch (e: Exception) {
            metrics.recordError("INVALIDATE_ALL_KEYS", e)
        }
    }

    fun getAll(keys: Iterable<K>): Map<K, V> {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return emptyMap()
        }

        return try {
            val result = caffeineCache.getAllPresent(keys)
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics.recordError("GET_ALL", e)
            circuitBreaker?.recordFailure()
            emptyMap()
        }
    }

    fun getAll(keys: Iterable<K>, loader: (Set<K>) -> Map<K, V>): Map<K, V> {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return emptyMap()
        }

        return try {
            val result = loadingCache?.getAll(keys) ?: run {
                val missingKeys = keys.toSet() - caffeineCache.getAllPresent(keys).keys
                if (missingKeys.isNotEmpty()) {
                    val loaded = loader(missingKeys)
                    caffeineCache.putAll(loaded)
                }
                caffeineCache.getAllPresent(keys)
            }
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics.recordError("GET_ALL_WITH_LOADER", e)
            circuitBreaker?.recordFailure()
            emptyMap()
        }
    }

    fun putIfAbsent(key: K, value: V): Boolean {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return false
        }

        return try {
            val existing = caffeineCache.getIfPresent(key)
            if (existing == null) {
                caffeineCache.put(key, value)
                circuitBreaker?.recordSuccess()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            metrics.recordError("PUT_IF_ABSENT", e)
            circuitBreaker?.recordFailure()
            false
        }
    }

    fun warmUp(dataLoader: () -> Map<K, V>) {
        try {
            val data = dataLoader()
            metrics.recordWarmUp(data.size)

            submit(async = true) {
                try {
                    putAll(data)
                } catch (e: Exception) {
                    warning("Cache warm-up failed", e)
                    metrics.recordError("WARMUP", e)
                }
            }
        } catch (e: Exception) {
            metrics.recordError("WARMUP", e)
        }
    }

    fun size(): Long = caffeineCache.estimatedSize()

    fun stats(): EasyMetricsSnapshot {
        return metrics.getEnhancedSnapshot(caffeineCache.stats())
    }

    fun asMap(): MutableMap<K, V> {
        return caffeineCache.asMap()
    }

    fun compute(key: K, remappingFunction: (K, V?) -> V?): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = caffeineCache.asMap().compute(key, remappingFunction)
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics.recordError("COMPUTE", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    fun computeIfPresent(key: K, remappingFunction: (K, V) -> V?): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = caffeineCache.asMap().computeIfPresent(key, remappingFunction)
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            metrics.recordError("COMPUTE_IF_PRESENT", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    fun cleanUp() {
        try {
            caffeineCache.cleanUp()
            metrics.recordMaintenance()
        } catch (e: Exception) {
            metrics.recordError("CLEANUP", e)
        }
    }

    fun exportForMonitoring(): Map<String, Any> {
        return metrics.exportForMonitoring(caffeineCache.stats())
    }

    fun getWithBuiltInLoader(key: K): V? {
        if (circuitBreaker?.canExecute() == false) {
            metrics.recordCircuitBreakerReject()
            return null
        }

        return try {
            val result = loadingCache?.get(key)
            if (result != null) {
                circuitBreaker?.recordSuccess()
            }
            result
        } catch (e: Exception) {
            metrics.recordError("GET_WITH_BUILT_IN_LOADER", e)
            circuitBreaker?.recordFailure()
            null
        }
    }

    override fun close() {
        try {
            cleanUp()
            caffeineCache.invalidateAll()
        } catch (e: Exception) {
            warning("Error closing Easy cache", e)
        }
    }
}

class EasyCacheBuilder<K : Any, V : Any> {
    private var maximumSize: Long = 10_000
    private var expireAfterWrite: Duration? = null
    private var expireAfterAccess: Duration? = null
    private var refreshAfterWrite: Duration? = null
    private var recordStats = true
    private var circuitBreakerConfig: CircuitBreakerConfig? = null
    private var removalListener: RemovalListener<K, V>? = null
    private var loader: ((K) -> V?)? = null

    fun maximumSize(size: Long): EasyCacheBuilder<K, V> {
        this.maximumSize = size
        return this
    }

    fun expireAfterWrite(duration: Duration): EasyCacheBuilder<K, V> {
        this.expireAfterWrite = duration
        return this
    }

    fun expireAfterAccess(duration: Duration): EasyCacheBuilder<K, V> {
        this.expireAfterAccess = duration
        return this
    }

    fun refreshAfterWrite(duration: Duration): EasyCacheBuilder<K, V> {
        this.refreshAfterWrite = duration
        return this
    }

    fun recordStats(enabled: Boolean): EasyCacheBuilder<K, V> {
        this.recordStats = enabled
        return this
    }

    fun circuitBreaker(config: CircuitBreakerConfig): EasyCacheBuilder<K, V> {
        this.circuitBreakerConfig = config
        return this
    }

    fun removalListener(listener: RemovalListener<K, V>): EasyCacheBuilder<K, V> {
        this.removalListener = listener
        return this
    }

    fun loader(loader: (K) -> V?): EasyCacheBuilder<K, V> {
        this.loader = loader
        return this
    }

    fun build(): EasyCache<K, V> {
        val caffeineBuilder = Caffeine.newBuilder()
            .maximumSize(maximumSize)

        if (recordStats) {
            caffeineBuilder.recordStats()
        }

        expireAfterWrite?.let { caffeineBuilder.expireAfterWrite(it) }
        expireAfterAccess?.let { caffeineBuilder.expireAfterAccess(it) }
        refreshAfterWrite?.let { caffeineBuilder.refreshAfterWrite(it) }
        removalListener?.let { caffeineBuilder.removalListener(it) }

        val config = EasyCacheConfig<K, V>(
            maximumSize = maximumSize,
            expireAfterWrite = expireAfterWrite,
            expireAfterAccess = expireAfterAccess,
            refreshAfterWrite = refreshAfterWrite,
            circuitBreakerConfig = circuitBreakerConfig
        )

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

        return EasyCache(cache, loadingCache, config)
    }
}

data class EasyCacheConfig<K : Any, V : Any>(
    val maximumSize: Long,
    val expireAfterWrite: Duration?,
    val expireAfterAccess: Duration?,
    val refreshAfterWrite: Duration?,
    val circuitBreakerConfig: CircuitBreakerConfig?
)
