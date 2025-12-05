package top.cpjinan.akarilevel.booster

import top.cpjinan.akarilevel.cache.MemberCache
import top.cpjinan.akarilevel.cache.MemberCache.memberCache
import top.cpjinan.akarilevel.database.Database
import top.cpjinan.akarilevel.entity.MemberData
import java.util.*

/**
 * AkariLevel
 * top.cpjinan.akarilevel.booster
 *
 * 经验加成器处理器。
 *
 * @author 季楠
 * @since 2025/12/2 23:41
 */
object BoosterHandler {
    /**
     * 获取成员经验加成器列表。
     *
     * @param member 成员。
     * @return 包含请求的所有键值对的 Map。
     */
    fun getMemberBoosters(member: String): Map<UUID, BoosterData> {
        return try {
            val memberData = memberCache[member]
            memberData?.boosters ?: emptyMap()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    /**
     * 设置成员经验加成器。
     *
     * @param member 成员。
     * @param id UUID。
     * @param booster 经验加成器。
     */
    fun setMemberBooster(member: String, id: UUID, booster: BoosterData) {
        val data = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                boosters[id] = booster
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, member, json)
    }

    /**
     * 新增成员经验加成器。
     *
     * @param member 成员。
     * @param id UUID。
     * @param booster 经验加成器。
     */
    fun addMemberBooster(member: String, id: UUID, booster: BoosterData) {
        val data = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                boosters.putIfAbsent(id, booster)
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, member, json)
    }

    /**
     * 移除成员经验加成器。
     *
     * @param member 成员。
     * @param id UUID。
     */
    fun removeMemberBooster(member: String, id: UUID) {
        val data = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                boosters.remove(id)
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, member, json)
    }

    /**
     * 刷新成员经验加成器。
     *
     * @param member 成员。
     */
    fun refreshMemberBoosters(member: String) {
        val oldData = memberCache.getIfPresent(member)
        val newData = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                boosters.entries.removeAll {
                    it.value.start != -1L && it.value.duration != -1L && it.value.start + it.value.duration < System.currentTimeMillis()
                }
            }
        }

        if ((oldData?.boosters?.size ?: -1) != newData!!.boosters.size) {
            val json = MemberCache.gson.toJson(newData)
            Database.instance.set(Database.instance.memberTable, member, json)
        }
    }
}