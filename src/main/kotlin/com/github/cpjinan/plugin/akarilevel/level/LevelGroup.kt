package com.github.cpjinan.plugin.akarilevel.level

import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.level
 *
 * @author 季楠
 * @since 2025/7/28 19:36
 */
abstract class LevelGroup {
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
    abstract val name: String

    /** 展示名 **/
    abstract val display: String

    /** 成员 **/
    abstract val member: MutableList<String>

    /** 注册等级组 **/
    open fun register() {
        registerLevelGroup(name, this)
    }

    /** 取消注册等级组 **/
    open fun unregister() {
        unregisterLevelGroup(name)
    }

    /** 等级组注册事件 **/
    open fun onRegister() {}

    /** 等级组取消注册事件 **/
    open fun onUnregister() {}

    /** 成员变更事件 **/
    open fun onMemberChange(member: String, type: MemberChangeType, source: String) {}

    /** 成员等级变更事件 **/
    open fun onMemberLevelChange(member: String, oldLevel: Long, newLevel: Long, source: String) {}

    /** 成员经验变更事件 **/
    open fun onMemberExpChange(member: String, expAmount: Long, source: String) {}

    /** 获取成员列表 **/
    open fun getMember(): List<String> {
        return this.member
    }

    /** 增加成员 **/
    open fun addMember(member: String, source: String) {
        onMemberChange(member, MemberChangeType.JOIN, source)
        this.member.add(member)
    }

    /** 移除成员 **/
    open fun removeMember(member: String, source: String) {
        onMemberChange(member, MemberChangeType.QUIT, source)
        this.member.remove(member)
    }

    /** 获取成员等级 **/
    abstract fun getMemberLevel(member: String): Long

    /** 获取成员经验 **/
    abstract fun getMemberExp(member: String): Long

    /** 设置成员等级 **/
    abstract fun setMemberLevel(member: String, amount: Long, source: String)

    /** 设置成员经验 **/
    abstract fun setMemberExp(member: String, amount: Long, source: String)

    /** 增加成员等级 **/
    abstract fun addMemberLevel(member: String, amount: Long, source: String)

    /** 增加成员经验 **/
    abstract fun addMemberExp(member: String, amount: Long, source: String)

    /** 移除成员等级 **/
    abstract fun removeMemberLevel(member: String, amount: Long, source: String)

    /** 移除成员经验 **/
    abstract fun removeMemberExp(member: String, amount: Long, source: String)
}