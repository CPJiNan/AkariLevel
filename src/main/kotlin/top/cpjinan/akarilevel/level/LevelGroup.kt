package top.cpjinan.akarilevel.level

import top.cpjinan.akarilevel.cache.MemberCache
import top.cpjinan.akarilevel.cache.MemberCache.memberCache
import top.cpjinan.akarilevel.database.Database
import top.cpjinan.akarilevel.entity.MemberData
import top.cpjinan.akarilevel.entity.MemberLevelData
import top.cpjinan.akarilevel.event.*
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * top.cpjinan.akarilevel.level
 *
 * 等级组接口。
 *
 * @author 季楠, QwQ-dev
 * @since 2025/8/7 23:15
 */
interface LevelGroup {
    companion object {
        private var levelGroups: MutableMap<String, LevelGroup> = ConcurrentHashMap()

        /**
         * 获取等级组列表。
         *
         * @return 包含请求的所有键值对的 Map。
         */
        @JvmStatic
        fun getLevelGroups(): Map<String, LevelGroup> {
            return levelGroups
        }

        /**
         * 新增等级组。
         *
         * @param name 等级组编辑名。
         * @param levelGroup 等级组实例。
         */
        @JvmStatic
        fun addLevelGroup(name: String, levelGroup: LevelGroup) {
            levelGroups[name] = levelGroup
        }

        /**
         * 移除等级组。
         *
         * @param name 等级组编辑名。
         */
        @JvmStatic
        fun removeLevelGroup(name: String) {
            levelGroups.remove(name)
        }
    }

    /**
     * 成员变更类型枚举。
     */
    enum class MemberChangeType {
        JOIN,
        QUIT
    }

    /**
     * 编辑名。
     */
    val name: String

    /**
     * 展示名。
     */
    val display: String

    /**
     * 注册等级组。
     */
    fun register() {
        val event = LevelGroupRegisterEvent(name)
        event.call()
        if (event.isCancelled) return
        addLevelGroup(event.levelGroup, this)
        onRegister()
    }

    /**
     * 取消注册等级组。
     */
    fun unregister() {
        val event = LevelGroupUnregisterEvent(name)
        event.call()
        if (event.isCancelled) return
        removeLevelGroup(event.levelGroup)
        onUnregister()
    }

    /**
     * 获取等级名称。
     *
     * @param level 等级。
     * @return 要获取的等级名称。
     */
    fun getLevelName(level: Long): String

    /**
     * 获取等级名称。
     *
     * @param member 成员。
     * @param level 等级。
     * @return 要获取的等级名称。
     */
    fun getLevelName(member: String, level: Long): String {
        return getLevelName(level)
    }

    /**
     * 获取升级所需经验。
     *
     * @param oldLevel 旧等级。
     * @param newLevel 新等级。
     * @return 升级所需经验。
     */
    fun getLevelExp(oldLevel: Long, newLevel: Long): Long

    /**
     * 获取升级所需经验。
     *
     * @param member 成员。
     * @param oldLevel 旧等级。
     * @param newLevel 新等级。
     * @return 升级所需经验。
     */
    fun getLevelExp(member: String, oldLevel: Long, newLevel: Long): Long {
        return getLevelExp(oldLevel, newLevel)
    }

    /**
     * 获取最低等级。
     *
     * @return 等级组的最低等级。
     */
    fun getMinLevel(): Long

    /**
     * 获取最高等级。
     *
     * @return 等级组的最高等级。
     */
    fun getMaxLevel(): Long

    /**
     * 是否包含成员。
     *
     * @param member 成员。
     * @return 如果此等级组包含请求的成员，则返回 true。
     */
    fun hasMember(member: String): Boolean {
        return try {
            val memberData = memberCache[member]
            memberData?.levelGroups?.keys?.contains(name) ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 增加成员。
     *
     * @param member 成员。
     * @param source 来源。
     */
    fun addMember(member: String, source: String) {
        if (hasMember(member)) return

        val event = MemberChangeEvent(member, name, MemberChangeType.JOIN, source)
        event.call()
        if (event.isCancelled) return

        val data = memberCache.asMap().compute(event.member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                levelGroups.putIfAbsent(event.levelGroup, MemberLevelData())
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, event.member, json)

        onMemberChange(event.member, event.type, event.source)
    }

    /**
     * 移除成员。
     *
     * @param member 成员。
     * @param source 来源。
     */
    fun removeMember(member: String, source: String) {
        if (!hasMember(member)) return
        val event = MemberChangeEvent(member, name, MemberChangeType.QUIT, source)
        event.call()
        if (event.isCancelled) return

        val data = memberCache.asMap()
            .compute(event.member) { _, memberData ->
                (memberData ?: MemberData()).apply {
                    levelGroups.remove(name)
                }
            }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, event.member, json)

        onMemberChange(event.member, event.type, event.source)
    }

    /**
     * 获取成员等级。
     *
     * @param member 成员。
     * @return 要获取的成员等级。
     */
    fun getMemberLevel(member: String): Long {
        return try {
            val memberData = memberCache[member]
            memberData?.levelGroups[name]?.level ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 获取成员经验。
     *
     * @param member 成员。
     * @return 要获取的成员经验。
     */
    fun getMemberExp(member: String): Long {
        return try {
            val memberData = memberCache[member]
            memberData?.levelGroups[name]?.exp ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 设置成员等级。
     *
     * @param member 成员。
     * @param amount 等级。
     * @param source 来源。
     */
    fun setMemberLevel(member: String, amount: Long, source: String) {
        if (!hasMember(member)) return
        val event = MemberLevelChangeEvent(member, name, getMemberLevel(member), amount, source)
        event.call()
        if (event.isCancelled) return

        val data = memberCache.asMap().compute(event.member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                levelGroups.getOrPut(event.levelGroup) { MemberLevelData() }.level = event.newLevel
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, event.member, json)

        onMemberLevelChange(event.member, event.oldLevel, event.newLevel, event.source)
    }

    /**
     * 设置成员经验。
     *
     * @param member 成员。
     * @param amount 经验。
     * @param source 来源。
     */
    fun setMemberExp(member: String, amount: Long, source: String) {
        if (!hasMember(member)) return
        val event = MemberExpChangeEvent(member, name, amount - getMemberExp(member), source)
        event.call()
        if (event.isCancelled) return

        val data = memberCache.asMap().compute(event.member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                levelGroups.getOrPut(event.levelGroup) { MemberLevelData() }.exp += event.expAmount
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, event.member, json)

        onMemberExpChange(event.member, event.expAmount, event.source)
    }

    /**
     * 增加成员等级。
     *
     * @param member 成员。
     * @param amount 等级。
     * @param source 来源。
     */
    fun addMemberLevel(member: String, amount: Long, source: String) {
        setMemberLevel(member, getMemberLevel(member) + amount, source)
    }

    /**
     * 增加成员经验。
     *
     * @param member 成员。
     * @param amount 经验。
     * @param source 来源。
     */
    fun addMemberExp(member: String, amount: Long, source: String) {
        setMemberExp(member, getMemberExp(member) + amount, source)
    }

    /**
     * 移除成员等级。
     *
     * @param member 成员。
     * @param amount 等级。
     * @param source 来源。
     */
    fun removeMemberLevel(member: String, amount: Long, source: String) {
        setMemberLevel(member, getMemberLevel(member) - amount, source)
    }

    /**
     * 移除成员经验。
     *
     * @param member 成员。
     * @param amount 经验。
     * @param source 来源。
     */
    fun removeMemberExp(member: String, amount: Long, source: String) {
        setMemberExp(member, getMemberExp(member) - amount, source)
    }

    /**
     * 等级组注册回调。
     */
    fun onRegister() {}

    /**
     * 等级组取消注册回调。
     */
    fun onUnregister() {}

    /**
     * 成员变更回调。
     *
     * @param member 成员。
     * @param type 类型。
     * @param source 来源。
     */
    fun onMemberChange(member: String, type: MemberChangeType, source: String) {}

    /**
     * 成员等级变更回调。
     *
     * @param member 成员。
     * @param oldLevel 旧等级。
     * @param newLevel 新等级。
     * @param source 来源。
     */
    fun onMemberLevelChange(member: String, oldLevel: Long, newLevel: Long, source: String) {}

    /**
     * 成员经验变更回调。
     *
     * @param member 成员。
     * @param expAmount 经验变化量。
     * @param source 来源。
     */
    fun onMemberExpChange(member: String, expAmount: Long, source: String) {}
}