package com.github.cpjinan.plugin.akarilevel

import taboolib.common.LifeCycle
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.common.platform.function.registerLifeCycleTask
import taboolib.platform.BukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/6/21 19:40
 */
object AkariLevel : Plugin() {
    val plugin by lazy { BukkitPlugin.getInstance() }

    val database by lazy { api().getDatabase().getDefault() }

    private var api: AkariLevelAPI? = null

    init {
        registerLifeCycleTask(LifeCycle.INIT) {
            try {
                AkariLevelLoader.startup()
            } catch (ex: Throwable) {
                ex.printStackTrace()
                disablePlugin()
            }
        }
    }

    /** 获取开发者接口 **/
    fun api(): AkariLevelAPI {
        return api ?: error("AkariLevelAPI has not finished loading, or failed to load!")
    }

    /** 注册开发者接口 **/
    fun register(api: AkariLevelAPI) {
        AkariLevel.api = api
    }
}