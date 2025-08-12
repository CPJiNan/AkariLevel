package com.github.cpjinan.plugin.akarilevel.level

import com.github.cpjinan.plugin.akarilevel.cache.SmartPersistenceManager
import com.github.cpjinan.plugin.akarilevel.cache.memberCache
import com.github.cpjinan.plugin.akarilevel.entity.MemberLevelData
import com.github.cpjinan.plugin.akarilevel.event.*
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.level
 *
 * 等级组接口。
 *
 * @author 季楠
 * @since 2025/8/7 23:15
 */
interface LevelGroup {
    companion object {
        private var levelGroups: MutableMap<String, LevelGroup> = ConcurrentHashMap()

        /** 获取等级组列表 **/
        @JvmStatic
        fun getLevelGroups(): Map<String, LevelGroup> {
            return levelGroups
        }

        /** 新增等级组 **/
        @JvmStatic
        fun addLevelGroup(name: String, levelGroup: LevelGroup) {
            levelGroups[name] = levelGroup
        }

        /** 移除等级组 **/
        @JvmStatic
        fun removeLevelGroup(name: String) {
            levelGroups.remove(name)
        }
    }

    /** 成员变更类型 **/
    enum class MemberChangeType {
        JOIN,
        QUIT
    }

    /** 编辑名 **/
    val name: String

    /** 展示名 **/
    val display: String

    /** 注册等级组 **/
    fun register() {
        val event = LevelGroupRegisterEvent(name)
        event.call()
        if (event.isCancelled) return
        addLevelGroup(event.levelGroup, this)
        onRegister()
    }

    /** 取消注册等级组 **/
    fun unregister() {
        val event = LevelGroupUnregisterEvent(name)
        event.call()
        if (event.isCancelled) return
        removeLevelGroup(event.levelGroup)
        onUnregister()
    }

    /** 获取等级名称 **/
    fun getLevelName(level: Long): String

    /** 获取等级经验 **/
    fun getLevelExp(level: Long): Long

    /** 获取等级名称 **/
    fun getLevelName(member: String, level: Long): String {
        return getLevelName(level)
    }

    /** 获取等级经验 **/
    fun getLevelExp(member: String, level: Long): Long {
        return getLevelExp(level)
    }

    /** 是否包含成员 **/
    fun hasMember(member: String): Boolean {
        return try {
            // 使用内置loader触发加载器
            val memberData = memberCache.getWithBuiltInLoader(member)
            memberData?.levelGroups?.keys?.contains(name) ?: false
        } catch (e: Exception) {
            false
        }
    }

    /** 增加成员 **/
    fun addMember(member: String, source: String) {
        if (hasMember(member)) return
        val event = MemberChangeEvent(member, name, MemberChangeType.JOIN, source)
        event.call()
        if (event.isCancelled) return
        memberCache.asMap()
            .compute(event.member) { _: String, memberData: com.github.cpjinan.plugin.akarilevel.entity.MemberData? ->
                val data = memberData ?: com.github.cpjinan.plugin.akarilevel.entity.MemberData()
                data.apply {
                    levelGroups.putIfAbsent(event.levelGroup, MemberLevelData())
                }
                data
            }

        // 标记玩家数据为脏数据，需要持久化
        SmartPersistenceManager.markDirty(event.member)
        onMemberChange(event.member, event.type, event.source)
    }

    /** 移除成员 **/
    fun removeMember(member: String, source: String) {
        if (!hasMember(member)) return
        val event = MemberChangeEvent(member, name, MemberChangeType.QUIT, source)
        event.call()
        if (event.isCancelled) return
        memberCache.asMap()
            .compute(event.member) { _: String, memberData: com.github.cpjinan.plugin.akarilevel.entity.MemberData? ->
                memberData?.apply {
                    levelGroups.remove(name)
                }
            }
        onMemberChange(event.member, event.type, event.source)
    }

    /** 获取成员等级 **/
    fun getMemberLevel(member: String): Long {
        return try {
            // 使用内置loader触发加载器
            val memberData = memberCache.getWithBuiltInLoader(member)
            memberData?.levelGroups[name]?.level ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /** 获取成员经验 **/
    fun getMemberExp(member: String): Long {
        return try {
            // 使用内置loader触发加载器
            val memberData = memberCache.getWithBuiltInLoader(member)
            memberData?.levelGroups[name]?.exp ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /** 设置成员等级 **/
    fun setMemberLevel(member: String, amount: Long, source: String) {
        if (!hasMember(member)) return
        val event = MemberLevelChangeEvent(member, name, getMemberLevel(member), amount, source)
        event.call()
        if (event.isCancelled) return
        memberCache.asMap()
            .compute(event.member) { _: String, memberData: com.github.cpjinan.plugin.akarilevel.entity.MemberData? ->
                val data = memberData ?: com.github.cpjinan.plugin.akarilevel.entity.MemberData()
                data.apply {
                    levelGroups.getOrPut(event.levelGroup) { MemberLevelData() }.level = event.newLevel
                }
                data  // 🔧 显式返回data对象
            }

        // 标记玩家数据为脏数据，需要持久化
        SmartPersistenceManager.markDirty(event.member)
        onMemberLevelChange(event.member, event.oldLevel, event.newLevel, event.source)
    }

    /** 设置成员经验 **/
    fun setMemberExp(member: String, amount: Long, source: String) {
        if (!hasMember(member)) return
        val event = MemberExpChangeEvent(member, name, amount - getMemberExp(member), source)
        event.call()
        if (event.isCancelled) return
        memberCache.asMap()
            .compute(event.member) { _: String, memberData: com.github.cpjinan.plugin.akarilevel.entity.MemberData? ->
                val data = memberData ?: com.github.cpjinan.plugin.akarilevel.entity.MemberData()
                data.apply {
                    levelGroups.getOrPut(event.levelGroup) { MemberLevelData() }.exp += event.expAmount
                }
                data  // 🔧 显式返回data对象
            }

        // 标记玩家数据为脏数据，需要持久化
        SmartPersistenceManager.markDirty(event.member)
        onMemberExpChange(event.member, event.expAmount, event.source)
    }

    /** 增加成员等级 **/
    fun addMemberLevel(member: String, amount: Long, source: String) {
        setMemberLevel(member, getMemberLevel(member) + amount, source)
    }

    /** 增加成员经验 **/
    fun addMemberExp(member: String, amount: Long, source: String) {
        setMemberExp(member, getMemberExp(member) + amount, source)
    }

    /** 移除成员等级 **/
    fun removeMemberLevel(member: String, amount: Long, source: String) {
        setMemberLevel(member, getMemberLevel(member) - amount, source)
    }

    /** 移除成员经验 **/
    fun removeMemberExp(member: String, amount: Long, source: String) {
        setMemberExp(member, getMemberExp(member) - amount, source)
    }

    /** 等级组注册回调 **/
    fun onRegister() {}

    /** 等级组取消注册回调 **/
    fun onUnregister() {}

    /** 成员变更回调 **/
    fun onMemberChange(member: String, type: MemberChangeType, source: String) {}

    /** 成员等级变更回调 **/
    fun onMemberLevelChange(member: String, oldLevel: Long, newLevel: Long, source: String) {}

    /** 成员经验变更回调 **/
    fun onMemberExpChange(member: String, expAmount: Long, source: String) {}
}