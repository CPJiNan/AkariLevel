package top.cpjinan.akarilevel.level

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * top.cpjinan.akarilevel.level
 *
 * 升级动作接口。
 *
 * @author 季楠
 * @since 2025/10/29 23:27
 */
interface ConfigLevelAction {
    companion object {
        private var configLevelActions: MutableMap<String, ConfigLevelAction> = ConcurrentHashMap()

        /**
         * 获取升级动作列表。
         *
         * @return 包含请求的所有键值对的 Map。
         */
        @JvmStatic
        fun getConfigLevelActions(): Map<String, ConfigLevelAction> {
            return configLevelActions
        }

        /**
         * 注册升级动作。
         *
         * @param name 升级动作名称。
         * @param configLevelAction 升级动作实例。
         */
        @JvmStatic
        fun registerConfigLevelAction(name: String, configLevelAction: ConfigLevelAction) {
            configLevelActions[name] = configLevelAction
        }

        /**
         * 取消注册升级动作。
         *
         * @param name 升级动作名称。
         */
        @JvmStatic
        fun unregisterConfigLevelAction(name: String) {
            configLevelActions.remove(name)
        }

        /**
         * 插件启用事件。
         */
        @Awake(LifeCycle.ENABLE)
        fun onEnable() {
            // 注册 Kether 脚本升级动作。
            registerConfigLevelAction("Kether", KetherLevelAction)
        }
    }

    /**
     * 执行升级动作。
     *
     * @param member 成员。
     * @param levelGroup 等级组。
     * @param level 等级。
     * @param config 配置。
     */
    fun run(member: String, levelGroup: String, level: Long, config: ConfigurationSection)
}