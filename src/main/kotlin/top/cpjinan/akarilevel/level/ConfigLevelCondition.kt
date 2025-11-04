package top.cpjinan.akarilevel.level

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * top.cpjinan.akarilevel.level
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

        /**
         * 插件启用事件。
         */
        @Awake(LifeCycle.ENABLE)
        fun onEnable() {
            // 注册 Kether 脚本升级条件。
            registerConfigLevelCondition("Kether", KetherLevelCondition)
        }
    }

    /**
     * 检查升级条件。
     *
     * @param member 成员。
     * @param levelGroup 等级组。
     * @param level 等级。
     * @param config 配置。
     * @return 是否满足升级条件。
     */
    fun check(member: String, levelGroup: String, level: Long, config: ConfigurationSection): Boolean
}