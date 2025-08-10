package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.cache.levelGroupCache
import com.github.cpjinan.plugin.akarilevel.cache.memberCache

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.manager
 *
 * 缓存管理器。
 *
 * @author 季楠
 * @since 2025/8/10 11:31
 */
object CacheManager {
    /**
     * 清空数据缓存。
     */
    fun invalidateAll() {
        // 清空成员数据缓存。
        memberCache.invalidateAll()
        // 清空等级组数据缓存。
        levelGroupCache.invalidateAll()
    }
}