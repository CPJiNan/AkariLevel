package com.github.cpjinan.plugin.akarilevel.level

import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.level
 *
 * 升级条件接口。
 *
 * @author 季楠
 * @since 2025/10/29 23:27
 */
interface ConfigLevelCondition {
    companion object {
        private var configLevelConditions: MutableMap<String, ConfigLevelCondition> = ConcurrentHashMap()

        /**
         * 获取升级条件列表。
         *
         * @return 包含请求的所有键值对的 Map。
         */
        @JvmStatic
        fun getConfigLevelConditions(): Map<String, ConfigLevelCondition> {
            return configLevelConditions
        }

        /**
         * 注册升级条件。
         *
         * @param name 升级条件名称。
         * @param configLevelCondition 升级条件实例。
         */
        @JvmStatic
        fun registerConfigLevelCondition(name: String, configLevelCondition: ConfigLevelCondition) {
            configLevelConditions[name] = configLevelCondition
        }

        /**
         * 取消注册升级条件。
         *
         * @param name 升级条件名称。
         */
        @JvmStatic
        fun unregisterConfigLevelCondition(name: String) {
            configLevelConditions.remove(name)
        }
    }

    /**
     * 检查升级条件。
     *
     * @param member 成员。
     * @param level 等级。
     * @param config 配置。
     * @return 是否满足升级条件。
     */
    fun check(member: String, level: Long, config: ConfigurationSection): Boolean
}