package com.github.cpjinan.plugin.akarilevel

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/6/21 19:39
 */
interface AkariLevelAPI {
    /** 获取语言文件接口 **/
    fun getLanguage(): AkariLevelLanguage

    /** 获取数据库接口 **/
    fun getDatabase(): AkariLevelDatabase
}