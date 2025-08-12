package com.github.cpjinan.plugin.akarilevel.cache

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
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

    fun size(): Long

    fun cleanup()
}