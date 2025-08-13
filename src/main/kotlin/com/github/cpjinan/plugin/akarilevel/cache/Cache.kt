package com.github.cpjinan.plugin.akarilevel.cache

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * 缓存接口。
 *
 * @author QwQ-dev
 * @since 2025/8/12 17:30
 */
interface Cache<K : Any, V : Any> {
    /**
     * 通过键获取请求的对象。
     *
     * 如果对象不存在，则返回 null。
     *
     * @param key 要获取的对象的键。
     * @return 请求的对象。
     */
    operator fun get(key: K): V?

    /**
     * 将指定键设置为给定值。
     *
     * @param key 要设置的对象的键。
     * @param value 要设置的新值。
     */
    operator fun set(key: K, value: V)

    /**
     * 通过键获取请求的对象。
     *
     * 如果对象不存在，则通过加载器获取。
     *
     * @param key 要获取的对象的键。
     * @param loader 加载器。
     * @return 请求的对象。
     */
    fun get(key: K, loader: (K) -> V?): V?

    /**
     * 通过键获取请求的对象。
     *
     * @param keys 要获取的对象的键集合。
     * @return 包含请求的所有键值对的 Map。
     */
    fun getAll(keys: Iterable<K>): Map<K, V>

    /**
     * 将指定键设置为给定值。
     *
     * @param entries 包含要设置的所有键值对的 Map。
     */
    fun setAll(entries: Map<K, V>)

    /**
     * 使指定键的缓存失效。
     *
     * @param key 要失效的键。
     */
    fun invalidate(key: K)

    /**
     * 使指定键的缓存失效。
     *
     * @param keys 要失效的键的集合。
     */
    fun invalidateAll(keys: Iterable<K>)

    /**
     * 使所有键的缓存失效。
     */
    fun invalidateAll()

    /**
     * 获取缓存中条目的数量。
     *
     * @return 缓存中条目的数量。
     */
    fun size(): Long

    /**
     * 清理缓存。
     */
    fun cleanup()
}