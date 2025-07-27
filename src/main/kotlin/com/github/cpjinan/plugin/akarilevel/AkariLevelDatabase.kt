package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.data.Database

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/7/27 19:25
 */
interface AkariLevelDatabase {
    /** 获取默认数据库实例 **/
    fun getDefault(): Database
}