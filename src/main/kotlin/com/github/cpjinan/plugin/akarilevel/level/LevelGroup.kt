package com.github.cpjinan.plugin.akarilevel.level

import com.github.cpjinan.plugin.akarilevel.AkariLevel.database
import com.github.cpjinan.plugin.akarilevel.event.*
import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.level
 *
 * @author 季楠
 * @since 2025/7/28 19:36
 */
interface LevelGroup {
    companion object {
        private var levelGroupMap: ConcurrentHashMap<String, LevelGroup> = ConcurrentHashMap()

        /** 获取等级组列表 **/
        fun getLevelGroup(): Map<String, LevelGroup> {
            return levelGroupMap
        }

        /** 注册等级组 **/
        fun registerLevelGroup(name: String, levelGroup: LevelGroup) {
            levelGroupMap[name] = levelGroup
        }

        /** 取消注册等级组 **/
        fun unregisterLevelGroup(name: String) {
            levelGroupMap.remove(name)
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
    val member: MutableList<String>

    /** 注册等级组 **/
    fun register() {
        GroupRegisterEvent(name).run {
            call()
            if (isCancelled) return
        }
        if (!onRegister()) return
        registerLevelGroup(name, this)
    }

    /** 取消注册等级组 **/
    fun unregister() {
        GroupUnregisterEvent(name).run {
            call()
            if (isCancelled) return
        }
        if (!onUnregister()) return
        unregisterLevelGroup(name)
    }

    /** 等级组注册回调 **/
    fun onRegister(): Boolean {
        return true
    }

    /** 等级组取消注册回调 **/
    fun onUnregister(): Boolean {
        return true
    }

    /** 成员变更回调 **/
    fun onMemberChange(member: String, type: MemberChangeType, source: String): Boolean {
        return true
    }

    /** 成员等级变更回调 **/
    fun onMemberLevelChange(member: String, oldLevel: Long, newLevel: Long, source: String): Boolean {
        return true
    }

    /** 成员经验变更回调 **/
    fun onMemberExpChange(member: String, expAmount: Long, source: String): Boolean {
        return true
    }

    /** 获取成员列表 **/
    fun getMember(): List<String> {
        return this.member
    }

    /** 增加成员 **/
    fun addMember(member: String, source: String) {
        MemberChangeEvent(member, name, MemberChangeType.JOIN, source).run {
            call()
            if (isCancelled) return
        }
        if (!onMemberChange(member, MemberChangeType.JOIN, source)) return
        this.member.add(member)
    }

    /** 移除成员 **/
    fun removeMember(member: String, source: String) {
        MemberChangeEvent(member, name, MemberChangeType.QUIT, source).run {
            call()
            if (isCancelled) return
        }
        if (!onMemberChange(member, MemberChangeType.QUIT, source)) return
        this.member.remove(member)
    }

    /** 获取成员等级 **/
    fun getMemberLevel(member: String): Long {
        return database["LevelGroup.${name}.Member.${member}.Level"]?.toDoubleOrNull()?.toLong() ?: 0
    }

    /** 获取成员经验 **/
    fun getMemberExp(member: String): Long {
        return database["LevelGroup.${name}.Member.${member}.Exp"]?.toDoubleOrNull()?.toLong() ?: 0
    }

    /** 设置成员等级 **/
    fun setMemberLevel(member: String, amount: Long, source: String) {
        MemberLevelChangeEvent(member, name, getMemberLevel(member), amount, source).run {
            call()
            if (isCancelled) return
        }
        if (!onMemberLevelChange(member, getMemberLevel(member), amount, source)) return
        database["LevelGroup.${name}.Member.${member}.Level"] = amount.toString()
    }

    /** 设置成员经验 **/
    fun setMemberExp(member: String, amount: Long, source: String) {
        MemberExpChangeEvent(member, name, amount - getMemberExp(member), source).run {
            call()
            if (isCancelled) return
        }
        if (!onMemberExpChange(member, amount - getMemberExp(member), source)) return
        database["LevelGroup.${name}.Member.${member}.Exp"] = amount.toString()
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
}