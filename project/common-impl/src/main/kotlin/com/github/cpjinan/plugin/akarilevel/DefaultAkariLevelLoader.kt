package com.github.cpjinan.plugin.akarilevel

import taboolib.common.util.unsafeLazy

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/24 10:09
 */
object DefaultAkariLevelLoader {
    val api by unsafeLazy { DefaultAkariLevelAPI() }

    /** 启动 AkariLevel 服务 **/
    fun startup() {
        AkariLevel.register(api)
    }
}