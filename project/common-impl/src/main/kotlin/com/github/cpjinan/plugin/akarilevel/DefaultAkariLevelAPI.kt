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
    var localDatabase = PlatformFactory.getAPI<AkariLevelDatabase>()

    /** 脚本拓展接口 **/
    var localLanguage = PlatformFactory.getAPI<AkariLevelLanguage>()

    /** 脚本拓展接口 **/
    var localScript = PlatformFactory.getAPI<AkariLevelScript>()

    /** 属性接口 **/
    var localAttribute =
        AkariLevelSettings.attributePlugin.takeIf { it.isNotEmpty() }?.let {
            PlatformFactory.getAPI<AkariLevelAttribute>()
        }

    /** 获取数据接口 **/
    override fun getDatabase(): AkariLevelDatabase {
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

    /** 获取属性接口 **/
    override fun getAttribute(): AkariLevelAttribute? {
        return localAttribute
    }
}