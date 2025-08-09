package com.github.cpjinan.plugin.akarilevel.level

import com.github.cpjinan.plugin.akarilevel.cache.memberCache
import com.github.cpjinan.plugin.akarilevel.entity.MemberData
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
        fun getLevelGroups(): Map<String, LevelGroup> {
            return levelGroups
        }

        /** 注册等级组 **/
        fun registerLevelGroup(name: String, levelGroup: LevelGroup) {
            levelGroups[name] = levelGroup
        }

        /** 取消注册等级组 **/
        fun unregisterLevelGroup(name: String) {
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

    /** 成员 **/
    val members: MutableList<String>

    /** 注册等级组 **/
    fun register() {
        GroupRegisterEvent(name).run {
            call()
            if (isCancelled) return
        }
        var isCancelled = false
        onRegister {
            isCancelled = it
        }
        if (isCancelled) return
        registerLevelGroup(name, this)
    }

    /** 取消注册等级组 **/
    fun unregister() {
        GroupUnregisterEvent(name).run {
            call()
            if (isCancelled) return
        }
        var isCancelled = false
        onUnregister {
            isCancelled = it
        }
        if (isCancelled) return
        unregisterLevelGroup(name)
    }

    /** 获取等级名称 **/
    fun getLevelName(level: Long): String

    /** 获取等级经验 **/
    fun getLevelExp(level: Long): Long

    /** 增加成员 **/
    fun addMember(member: String, source: String) {
        MemberChangeEvent(member, name, MemberChangeType.JOIN, source).run {
            call()
            if (isCancelled) return
        }
        var isCancelled = false
        onMemberChange(member, MemberChangeType.JOIN, source) {
            isCancelled = it
        }
        if (isCancelled) return
        this.members.add(member)
    }

    /** 移除成员 **/
    fun removeMember(member: String, source: String) {
        MemberChangeEvent(member, name, MemberChangeType.QUIT, source).run {
            call()
            if (isCancelled) return
        }
        var isCancelled = false
        onMemberChange(member, MemberChangeType.QUIT, source) {
            isCancelled = it
        }
        if (isCancelled) return
        this.members.remove(member)
    }

    /** 获取成员等级 **/
    fun getMemberLevel(member: String): Long {
        return memberCache[member]?.levelGroups[name]?.level ?: 0
    }

    /** 获取成员经验 **/
    fun getMemberExp(member: String): Long {
        return memberCache[member]?.levelGroups[name]?.exp ?: 0
    }

    /** 设置成员等级 **/
    fun setMemberLevel(member: String, amount: Long, source: String) {
        MemberLevelChangeEvent(member, name, getMemberLevel(member), amount, source).run {
            call()
            if (isCancelled) return
        }
        var isCancelled = false
        onMemberLevelChange(member, getMemberLevel(member), amount, source) {
            isCancelled = it
        }
        if (isCancelled) return

        memberCache.asMap().compute(member) { _, data ->
            (data ?: MemberData()).apply {
                levelGroups.getOrPut(name) { MemberLevelData() }.level = amount
            }
        }
    }

    /** 设置成员经验 **/
    fun setMemberExp(member: String, amount: Long, source: String) {
        MemberExpChangeEvent(member, name, amount - getMemberExp(member), source).run {
            call()
            if (isCancelled) return
        }
        var isCancelled = false
        onMemberExpChange(member, amount - getMemberExp(member), source) {
            isCancelled = it
        }
        if (isCancelled) return

        memberCache.asMap().compute(member) { _, data ->
            (data ?: MemberData()).apply {
                levelGroups.getOrPut(name) { MemberLevelData() }.exp = amount
            }
        }
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
    fun onRegister(onCancel: (Boolean) -> Unit = { }) {}

    /** 等级组取消注册回调 **/
    fun onUnregister(onCancel: (Boolean) -> Unit = { }) {}

    /** 成员变更回调 **/
    fun onMemberChange(member: String, type: MemberChangeType, source: String, onCancel: (Boolean) -> Unit = { }) {}

    /** 成员等级变更回调 **/
    fun onMemberLevelChange(
        member: String,
        oldLevel: Long,
        newLevel: Long,
        source: String,
        onCancel: (Boolean) -> Unit = { }
    ) {
    }

    /** 成员经验变更回调 **/
    fun onMemberExpChange(member: String, expAmount: Long, source: String, onCancel: (Boolean) -> Unit = { }) {}
}