package com.github.cpjinan.plugin.akarilevel.level

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
            levelGroupMap[name] = levelGroup.apply { onRegister() }
        }

        /** 取消注册等级组 **/
        fun unregisterLevelGroup(name: String) {
            levelGroupMap.remove(name)?.also { it.onUnregister() }
        }

        /** 成员变更类型 **/
        enum class MemberChangeType {
            JOIN,
            QUIT
        }
    }

    /** 编辑名 **/
    val name: String

    /** 展示名 **/
    val display: String

    /** 成员 **/
    val member: MutableList<String>

    /** 注册等级组 **/
    fun register() {
        registerLevelGroup(name, this)
    }

    /** 取消注册等级组 **/
    fun unregister() {
        unregisterLevelGroup(name)
    }

    /** 等级组注册事件 **/
    fun onRegister() {}

    /** 等级组取消注册事件 **/
    fun onUnregister() {}

    /** 成员变更事件 **/
    fun onMemberChange(member: String, type: MemberChangeType, source: String) {}

    /** 成员等级变更事件 **/
    fun onMemberLevelChange(member: String, oldLevel: Long, newLevel: Long, source: String) {}

    /** 成员经验变更事件 **/
    fun onMemberExpChange(member: String, expAmount: Long, source: String) {}

    /** 获取成员列表 **/
    fun getMember(): List<String> {
        return this.member
    }

    /** 增加成员 **/
    fun addMember(member: String, source: String) {
        onMemberChange(member, MemberChangeType.JOIN, source)
        this.member.add(member)
    }

    /** 移除成员 **/
    fun removeMember(member: String, source: String) {
        onMemberChange(member, MemberChangeType.QUIT, source)
        this.member.remove(member)
    }

    /** 获取成员等级 **/
    fun getMemberLevel(member: String): Long

    /** 获取成员经验 **/
    fun getMemberExp(member: String): Long

    /** 设置成员等级 **/
    fun setMemberLevel(member: String, amount: Long, source: String)

    /** 设置成员经验 **/
    fun setMemberExp(member: String, amount: Long, source: String)

    /** 增加成员等级 **/
    fun addMemberLevel(member: String, amount: Long, source: String)

    /** 增加成员经验 **/
    fun addMemberExp(member: String, amount: Long, source: String)

    /** 移除成员等级 **/
    fun removeMemberLevel(member: String, amount: Long, source: String)

    /** 移除成员经验 **/
    fun removeMemberExp(member: String, amount: Long, source: String)
}