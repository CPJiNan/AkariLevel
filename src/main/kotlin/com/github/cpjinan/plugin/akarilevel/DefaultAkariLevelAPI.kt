package com.github.cpjinan.plugin.akarilevel

import taboolib.common.platform.PlatformFactory

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/6/21 19:40
 */
class DefaultAkariLevelAPI : AkariLevelAPI {
    var localLanguage = PlatformFactory.getAPI<AkariLevelLanguage>()

    override fun getLanguage(): AkariLevelLanguage {
        return localLanguage
    }
}