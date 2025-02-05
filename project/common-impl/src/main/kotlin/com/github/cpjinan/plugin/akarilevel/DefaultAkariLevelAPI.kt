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
    /** 数据接口 **/
    var localDatabase = PlatformFactory.getAPI<AkariLevelData>()

    /** 脚本拓展接口 **/
    var localLanguage = PlatformFactory.getAPI<AkariLevelLanguage>()

    /** 脚本拓展接口 **/
    var localScript = PlatformFactory.getAPI<AkariLevelScript>()

    /** 获取数据接口 **/
    override fun getData(): AkariLevelData {
        return localDatabase
    }

    /** 获取语言文件接口 **/
    override fun getLanguage(): AkariLevelLanguage {
        return localLanguage
    }

    /** 获取脚本拓展接口 **/
    override fun getScript(): AkariLevelScript {
        return localScript
    }
}