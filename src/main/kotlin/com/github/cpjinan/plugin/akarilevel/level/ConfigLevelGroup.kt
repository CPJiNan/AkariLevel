@file:Suppress("DEPRECATION")

package com.github.cpjinan.plugin.akarilevel.level

import com.github.cpjinan.plugin.akarilevel.cache.MemberCache.memberCache
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
import com.github.cpjinan.plugin.akarilevel.entity.MemberLevelData
import com.github.cpjinan.plugin.akarilevel.event.MemberChangeEvent
import com.github.cpjinan.plugin.akarilevel.level.LevelGroup.MemberChangeType
import com.github.cpjinan.plugin.akarilevel.manager.CacheManager
import org.bukkit.Bukkit.getOfflinePlayer
import taboolib.common5.util.replace
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored
import taboolib.module.configuration.Type
import taboolib.platform.compat.replacePlaceholder
import top.maplex.arim.Arim
import top.maplex.arim.tools.folderreader.releaseResourceFolderAndRead
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.level
 *
 * [LevelGroup] 接口的实现，从配置文件获取等级组。
 *
 * @author 季楠
 * @since 2025/8/7 23:15
 */
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
            configLevelGroups.forEach { _, levelGroup ->
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
        return getLevelName(level).let {
            if (member.startsWith("player:")) it.replacePlaceholder(getOfflinePlayer(member.substringAfter("player:")))
            else it
        }
    }

    override fun getLevelExp(oldLevel: Long, newLevel: Long): Long {
        return when (config.getString("Level.Exp-Type", "Absolute")) {
            "Absolute" -> getLevelExpConfig(newLevel) - getLevelExpConfig(oldLevel)
            "Relative" -> (oldLevel + 1..newLevel).sumOf { getLevelExpConfig(it) }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getLevelExp(member: String, oldLevel: Long, newLevel: Long): Long {
        return when (config.getString("Level.Exp-Type", "Absolute")) {
            "Absolute" -> getLevelExpConfig(member, newLevel) - getLevelExpConfig(member, oldLevel)
            "Relative" -> (oldLevel + 1..newLevel).sumOf { getLevelExpConfig(member, it) }
            else -> throw IllegalArgumentException()
        }
    }

    override fun addMember(member: String, source: String) {
        if (hasMember(member)) return
        val event = MemberChangeEvent(member, name, MemberChangeType.JOIN, source)
        event.call()
        if (event.isCancelled) return
        memberCache.asMap()
            .compute(event.member) { _, memberData ->
                (memberData ?: MemberData()).apply {
                    levelGroups.putIfAbsent(event.levelGroup, MemberLevelData(level = getMinLevel()))
                }
            }

        CacheManager.markDirty(event.member)
        onMemberChange(event.member, event.type, event.source)
    }

    override fun getMemberLevel(member: String): Long {
        return try {
            val memberData = memberCache.getWithBuiltInLoader(member)
            memberData?.levelGroups[name]?.level ?: getMinLevel()
        } catch (_: Exception) {
            getMinLevel()
        }
    }

    override fun setMemberLevel(member: String, amount: Long, source: String) {
        when (config.getString("Level.Exp-Type", "Absolute")) {
            "Absolute" -> {
                super.setMemberLevel(member, amount.coerceIn(getMinLevel(), getMaxLevel()), source)
                setMemberExp(member, getLevelExp(member, 0, amount), source)
            }

            "Relative" -> {
                super.setMemberLevel(member, amount.coerceIn(getMinLevel(), getMaxLevel()), source)
            }
        }
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

        when (config.getString("Level.Exp-Type", "Absolute")) {
            "Absolute" -> {
                while (currentExp >= getLevelExp(member, 0, targetLevel + 1)) targetLevel++
                if (targetLevel > currentLevel) setMemberLevel(member, targetLevel, "LEVEL_UP")
            }

            "Relative" -> {
                while (currentExp >= getLevelExp(member, currentLevel, targetLevel + 1)) targetLevel++
                if (targetLevel > currentLevel) {
                    setMemberLevel(member, targetLevel, "LEVEL_UP")
                    removeMemberExp(member, getLevelExp(member, currentLevel, targetLevel), "LEVEL_UP")
                }
            }
        }
    }

    /**
     * 获取最低等级。
     *
     * @return 等级组的最低等级。
     */
    fun getMinLevel(): Long {
        return config.getLong("Level.Min")
    }

    /**
     * 获取最高等级。
     *
     * @return 等级组的最高等级。
     */
    fun getMaxLevel(): Long {
        return config.getLong("Level.Max")
    }

    /**
     * 获取等级配置。
     *
     * @param level 等级。
     * @return 要获取的等级配置。
     * @throws IllegalArgumentException 如果未找到指定等级配置。
     */
    fun getLevelConfig(level: Long): ConfigurationSection {
        return getKeyLevelConfigs()
            .filter { level >= it.key }
            .maxByOrNull { it.key }
            ?.value ?: throw IllegalArgumentException()
    }

    /**
     * 获取等级经验配置。
     *
     * @param level 等级。
     * @return 要获取的等级经验配置。
     */
    fun getLevelExpConfig(level: Long): Long {
        return Arim.fixedCalculator.evaluate(
            getLevelConfig(level).getString("Exp").orEmpty().replace("{level}" to level)
        ).toLong()
    }

    /**
     * 获取等级经验配置。
     *
     * @param member 成员。
     * @param level 等级。
     * @return 要获取的等级经验配置。
     */
    fun getLevelExpConfig(member: String, level: Long): Long {
        return Arim.fixedCalculator.evaluate(
            getLevelConfig(level).getString("Exp").orEmpty()
                .replace("{level}" to level)
                .let {
                    if (member.startsWith("player:")) {
                        it.replacePlaceholder(getOfflinePlayer(member.substringAfter("player:")))
                    } else it
                }
        ).toLong()
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

    override fun onMemberExpChange(member: String, expAmount: Long, source: String) {
        if (config.getBoolean("Level.Auto-LevelUp")) levelUpMember(member)
    }
}