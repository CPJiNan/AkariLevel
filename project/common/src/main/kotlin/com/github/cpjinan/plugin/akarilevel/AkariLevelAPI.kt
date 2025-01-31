package com.github.cpjinan.plugin.akarilevel

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/24 09:54
 */
interface AkariLevelAPI {
    /** 获取数据接口 **/
    fun getDatabase(): AkariLevelDatabase

    /** 获取语言文件接口 **/
    fun getLanguage(): AkariLevelLanguage

    /** 获取脚本拓展接口 **/
    fun getScript(): AkariLevelScript

    /** 获取属性接口 **/
    fun getAttribute(): AkariLevelAttribute?
}