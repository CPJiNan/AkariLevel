package com.github.cpjinan.plugin.akarilevel

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/24 09:54
 */
object AkariLevel {
    private var api: AkariLevelAPI? = null

    /** 获取开发者接口 **/
    fun api(): AkariLevelAPI {
        return api ?: error("AkariLevelAPI has not finished loading, or failed to load!")
    }

    /** 注册开发者接口 **/
    fun register(api: AkariLevelAPI) {
        AkariLevel.api = api
    }
}