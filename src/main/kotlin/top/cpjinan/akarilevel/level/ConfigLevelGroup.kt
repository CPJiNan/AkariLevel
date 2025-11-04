package top.cpjinan.akarilevel.level

import org.bukkit.Bukkit.getOfflinePlayer
import taboolib.common.platform.function.submit
import taboolib.common5.util.replace
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored
import taboolib.module.configuration.Type
import taboolib.platform.compat.replacePlaceholder
import top.cpjinan.akarilevel.cache.MemberCache
import top.cpjinan.akarilevel.cache.MemberCache.memberCache
import top.cpjinan.akarilevel.database.Database
import top.cpjinan.akarilevel.entity.MemberData
import top.cpjinan.akarilevel.entity.MemberLevelData
import top.cpjinan.akarilevel.event.MemberChangeEvent
import top.cpjinan.akarilevel.level.LevelGroup.MemberChangeType
import top.maplex.arim.Arim
import top.maplex.arim.tools.folderreader.releaseResourceFolderAndRead
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * top.cpjinan.akarilevel.level
 *
 * [LevelGroup] 接口的实现，从配置文件获取等级组。
 *
 * @author 季楠
 * @since 2025/8/7 23:15
 */
@Suppress("DEPRECATION")
class ConfigLevelGroup(val config: ConfigurationSection) : LevelGroup {
    companion object {
        private var configLevelGroups: MutableMap<String, ConfigLevelGroup> = ConcurrentHashMap()

        /**
         * 获取配置等级组列表。
         *
         * @return 包含请求的所有键值对的 Map。
         */
        @JvmStatic
        fun getConfigLevelGroups(): Map<String, ConfigLevelGroup> {
            return configLevelGroups
        }

        /**
         * 新增配置等级组。
         *
         * @param name 等级组编辑名。
         * @param configLevelGroup 配置等级组实例。
         */
        @JvmStatic
        fun addConfigLevelGroup(name: String, configLevelGroup: ConfigLevelGroup) {
            configLevelGroups[name] = configLevelGroup
        }

        /**
         * 移除配置等级组。
         *
         * @param name 等级组编辑名。
         */
        @JvmStatic
        fun removeConfigLevelGroup(name: String) {
            configLevelGroups.remove(name)
        }

        /**
         * 重载配置等级组。
         */
        @JvmStatic
        fun reloadConfigLevelGroups() {
            configLevelGroups.forEach { (_, levelGroup) ->
                levelGroup.unregister()
            }
            releaseResourceFolderAndRead("level") {
                setReadType(Type.YAML, Type.JSON)
                walk {
                    getKeys(false).forEach {
                        ConfigLevelGroup(getConfigurationSection(it)!!).register()
                    }
                }
            }
        }
    }

    override val name: String = config.name
    override val display: String = config.getString("General.Display", name)!!

    override fun getLevelName(level: Long): String {
        return getLevelConfig(level).getString("Name")?.replace("{level}" to level)?.colored() ?: "$level"
    }

    override fun getLevelName(member: String, level: Long): String {
        return getLevelConfig(level).getString("Name")
            ?.replace("{level}" to level)
            ?.let {
                val offlinePlayer = getOfflinePlayer(member)
                if (offlinePlayer.hasPlayedBefore()) it.replacePlaceholder(offlinePlayer)
                else it
            }
            ?.colored() ?: "$level"
    }

    override fun getLevelExp(oldLevel: Long, newLevel: Long): Long {
        when {
            newLevel > getMaxLevel() -> return Long.MAX_VALUE
            oldLevel < getMinLevel() -> return getLevelExp(getMinLevel(), newLevel)
            oldLevel == newLevel -> return 0
        }
        return (oldLevel + 1..newLevel).sumOf {
            Arim.fixedCalculator.evaluate(
                getLevelConfig(it).getString("Exp").orEmpty().replace("{level}" to it)
            ).toLong()
        }
    }

    override fun getLevelExp(member: String, oldLevel: Long, newLevel: Long): Long {
        when {
            newLevel > getMaxLevel() -> return Long.MAX_VALUE
            oldLevel < getMinLevel() -> return getLevelExp(member, getMinLevel(), newLevel)
            oldLevel == newLevel -> return 0
        }
        return (oldLevel + 1..newLevel).sumOf {
            Arim.fixedCalculator.evaluate(
                getLevelConfig(it).getString("Exp").orEmpty()
                    .let {
                        val offlinePlayer = getOfflinePlayer(member)
                        if (offlinePlayer.hasPlayedBefore()) it.replacePlaceholder(offlinePlayer)
                        else it
                    }
                    .replace("{level}" to it)
            ).toLong()
        }
    }

    override fun getMinLevel(): Long {
        return config.getLong("Level.Min")
    }

    override fun getMaxLevel(): Long {
        return config.getLong("Level.Max")
    }

    override fun addMember(member: String, source: String) {
        if (hasMember(member)) return

        val event = MemberChangeEvent(member, name, MemberChangeType.JOIN, source)
        event.call()
        if (event.isCancelled) return

        val data = memberCache.asMap().compute(event.member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                levelGroups.putIfAbsent(event.levelGroup, MemberLevelData(level = getMinLevel()))
            }
        }

        submit(async = true) {
            val json = MemberCache.gson.toJson(data)
            Database.instance.set(Database.instance.memberTable, event.member, json)
        }

        onMemberChange(event.member, event.type, event.source)
    }

    override fun getMemberLevel(member: String): Long {
        return try {
            val memberData = memberCache[member]
            memberData?.levelGroups[name]?.level ?: getMinLevel()
        } catch (e: Exception) {
            e.printStackTrace()
            getMinLevel()
        }
    }

    override fun setMemberLevel(member: String, amount: Long, source: String) {
        super.setMemberLevel(member, amount.coerceIn(getMinLevel(), getMaxLevel()), source)
    }

    override fun addMemberExp(member: String, amount: Long, source: String) {
        // 检查等级组是否订阅经验来源。
        val subscribeSources = config.getConfigurationSection("Source.Subscribe")?.getKeys(false) ?: return
        if (source !in subscribeSources) return

        // 检查是否可以继续获得经验。
        if (config.getBoolean("Level.Exp-Limit") && getMemberLevel(member) >= getMaxLevel()) return

        super.addMemberExp(member, (amount * config.getDouble("Source.Subscribe.$source")).toLong(), source)
    }

    /**
     * 升级成员。
     *
     * @param member 成员。
     */
    fun levelUpMember(member: String) {
        val currentLevel = getMemberLevel(member)
        val currentExp = getMemberExp(member)

        if (currentLevel >= getMaxLevel()) return

        var targetLevel = currentLevel

        if (config.getBoolean("Level.Auto-LevelUp", true) &&
            getLevelConfig(currentLevel).getBoolean("Auto-LevelUp", true)
        ) {
            while (currentExp >= getLevelExp(member, currentLevel, targetLevel + 1)) {
                if (checkLevelCondition(member, targetLevel + 1)) targetLevel++ else break
            }
        } else {
            if (currentExp >= getLevelExp(member, currentLevel, targetLevel + 1) &&
                checkLevelCondition(member, targetLevel + 1)
            ) targetLevel++
        }

        if (targetLevel > currentLevel) {
            for (level in currentLevel + 1..targetLevel) {
                setMemberLevel(member, level, "LEVEL_UP")
            }
            removeMemberExp(member, getLevelExp(member, currentLevel, targetLevel), "LEVEL_UP")
        }
    }

    /**
     * 检查升级条件。
     *
     * @param member 成员。
     * @param level 等级。
     * @return 是否满足升级条件。
     */
    fun checkLevelCondition(member: String, level: Long): Boolean {
        return ConfigLevelCondition.getConfigLevelConditions().values.all {
            it.check(member, name, level, getLevelConfig(level).getConfigurationSection("Condition")!!)
        }
    }

    /**
     * 执行升级动作。
     *
     * @param member 成员。
     * @param level 等级。
     */
    fun runLevelAction(member: String, level: Long) {
        ConfigLevelAction.getConfigLevelActions().values.forEach {
            it.run(member, name, level, getLevelConfig(level).getConfigurationSection("Action")!!)
        }
    }

    /**
     * 获取等级配置。
     *
     * @param level 等级。
     * @return 要获取的等级配置。
     */
    fun getLevelConfig(level: Long): ConfigurationSection {
        return (getKeyLevelConfigs() as TreeMap).floorEntry(level).value
    }

    /**
     * 获取关键等级配置列表。
     *
     * @return 包含请求的所有键值对的 Map。
     */
    fun getKeyLevelConfigs(): Map<Long, ConfigurationSection> {
        return config.getConfigurationSection("Level.Key")!!.getKeys(false)
            .associateBy(
                { it.toDoubleOrNull()?.toLong()!! },
                { config.getConfigurationSection("Level.Key.$it")!! }
            ).toSortedMap()
    }

    override fun onRegister() {
        addConfigLevelGroup(name, this)
    }

    override fun onUnregister() {
        removeConfigLevelGroup(name)
    }

    override fun onMemberLevelChange(member: String, oldLevel: Long, newLevel: Long, source: String) {
        runLevelAction(member, newLevel)
    }

    override fun onMemberExpChange(member: String, expAmount: Long, source: String) {
        // 自动升级。
        if (config.getBoolean("Level.Auto-LevelUp", true) &&
            getLevelConfig(getMemberLevel(member)).getBoolean("Auto-LevelUp", true)
        ) levelUpMember(member)
    }
}