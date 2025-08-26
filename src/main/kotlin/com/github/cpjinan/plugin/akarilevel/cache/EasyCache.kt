package com.github.cpjinan.plugin.akarilevel.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.benmanes.caffeine.cache.RemovalCause
import java.time.Duration
import com.github.benmanes.caffeine.cache.Cache as CaffeineCache

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * [Cache] 接口的实现。
 *
 * @author QwQ-dev
 * @since 2025/8/12 17:45
 */
class EasyCache<K : Any, V : Any> private constructor(
    private val caffeineCache: CaffeineCache<K, V>,
    private val loadingCache: LoadingCache<K, V>?,
    private val circuitBreaker: CircuitBreaker?,
) : Cache<K, V> {
    override fun get(key: K): V? {
        if (circuitBreaker?.canExecute() == false) return null

        return try {
            val result = caffeineCache.getIfPresent(key)
            if (result != null) circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            circuitBreaker?.recordFailure()
            null
        }
    }

    override fun set(key: K, value: V) {
        if (circuitBreaker?.canExecute() == false) return

        try {
            caffeineCache.put(key, value)
            circuitBreaker?.recordSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            circuitBreaker?.recordFailure()
        }
    }

    override fun get(key: K, loader: (K) -> V?): V? {
        if (circuitBreaker?.canExecute() == false) return null

        return try {
            val result = caffeineCache.get(key) { loader(it) }
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            circuitBreaker?.recordFailure()
            null
        }
    }

    override fun getAll(keys: Iterable<K>): Map<K, V> {
        if (circuitBreaker?.canExecute() == false) return emptyMap()

        return try {
            val result = caffeineCache.getAllPresent(keys)
            circuitBreaker?.recordSuccess()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            circuitBreaker?.recordFailure()
            emptyMap()
        }
    }

    override fun setAll(entries: Map<K, V>) {
        if (circuitBreaker?.canExecute() == false) return

        try {
            caffeineCache.putAll(entries)
            circuitBreaker?.recordSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            circuitBreaker?.recordFailure()
        }
    }

    override fun invalidate(key: K) {
        try {
            caffeineCache.invalidate(key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun invalidateAll(keys: Iterable<K>) {
        try {
            caffeineCache.invalidateAll(keys)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun invalidateAll() {
        try {
            caffeineCache.invalidateAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun size(): Long = caffeineCache.estimatedSize()

    override fun cleanup() {
        try {
            caffeineCache.cleanUp()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCircuitBreaker(): CircuitBreaker? = circuitBreaker

    fun getWithBuiltInLoader(key: K): V? {
        if (circuitBreaker?.canExecute() == false) return null

        return try {
            val result = loadingCache?.get(key)
            if (result != null) {
                circuitBreaker?.recordSuccess()
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            circuitBreaker?.recordFailure()
            null
        }
    }

    fun asMap(): MutableMap<K, V> = caffeineCache.asMap()

    fun compute(key: K, remappingFunction: (K, V?) -> V?): V? {
        if (circuitBreaker?.canExecute() == false) return null

        return try {
            val currentValue = if (loadingCache != null) {
                loadingCache.get(key)
            } else {
                caffeineCache.getIfPresent(key)
            }

            val newValue = remappingFunction(key, currentValue)

            if (newValue != null) {
                caffeineCache.put(key, newValue)
            } else if (currentValue != null) {
                caffeineCache.invalidate(key)
            }

            circuitBreaker?.recordSuccess()
            newValue
        } catch (e: Exception) {
            e.printStackTrace()
            circuitBreaker?.recordFailure()
            null
        }
    }

    class Builder<K : Any, V : Any> {
        private var maximumSize: Long = 10_000
        private var expireAfterWrite: Duration? = null
        private var expireAfterAccess: Duration? = null
        private var refreshAfterWrite: Duration? = null
        private var circuitBreakerConfig: CircuitBreakerConfig? = null
        private var removalListener: ((K, V, RemovalCause) -> Unit)? = null
        private var loader: ((K) -> V?)? = null

        fun maximumSize(size: Long) = apply { this.maximumSize = size }
        fun expireAfterWrite(duration: Duration) = apply { this.expireAfterWrite = duration }
        fun expireAfterAccess(duration: Duration) = apply { this.expireAfterAccess = duration }
        fun refreshAfterWrite(duration: Duration) = apply { this.refreshAfterWrite = duration }
        fun circuitBreaker(config: CircuitBreakerConfig) = apply { this.circuitBreakerConfig = config }
        fun removalListener(listener: (K, V, RemovalCause) -> Unit) = apply { this.removalListener = listener }
        fun loader(loader: (K) -> V?) = apply { this.loader = loader }

        fun build(): EasyCache<K, V> {
            val caffeineBuilder = Caffeine.newBuilder().maximumSize(maximumSize)

            expireAfterWrite?.let { caffeineBuilder.expireAfterWrite(it) }
            expireAfterAccess?.let { caffeineBuilder.expireAfterAccess(it) }
            refreshAfterWrite?.let { caffeineBuilder.refreshAfterWrite(it) }

            removalListener?.let { listener ->
                caffeineBuilder.removalListener<K, V> { key, value, cause ->
                    if (key != null && value != null) {
                        listener(key, value, cause)
                    }
                }
            }

            val cache: CaffeineCache<K, V>
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

            return EasyCache(cache, loadingCache, circuitBreaker)
        }
    }

    companion object {
        fun <K : Any, V : Any> builder(): Builder<K, V> = Builder()
    }
}