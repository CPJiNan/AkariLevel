package com.github.cpjinan.plugin.akarilevel

import taboolib.common.platform.PlatformFactory

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/24 10:04
 */
class DefaultAkariLevelAPI : AkariLevelAPI {
    /** 脚本拓展接口 **/
    var localScript = PlatformFactory.getAPI<AkariLevelScript>()

    /** 属性接口 **/
    var localAttribute = PlatformFactory.getAPI<AkariLevelAttribute>()

    /** 获取脚本拓展接口 **/
    override fun getScript(): AkariLevelScript {
        return localScript
    }

    /** 获取属性接口 **/
    override fun getAttribute(): AkariLevelAttribute {
        return localAttribute
    }
}