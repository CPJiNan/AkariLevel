package com.github.cpjinan.plugin.akarilevel

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/24 13:18
 */
object DefaultAkariLevelAttribute : AkariLevelAttribute {
    @Awake(LifeCycle.CONST)
    fun onConst() {
        // 注册服务
        PlatformFactory.registerAPI<AkariLevelAttribute>(DefaultAkariLevelAttribute)
    }
}