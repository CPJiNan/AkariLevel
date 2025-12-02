package top.cpjinan.akarilevel.booster

import top.cpjinan.akarilevel.cache.MemberCache
import top.cpjinan.akarilevel.cache.MemberCache.memberCache
import top.cpjinan.akarilevel.database.Database
import top.cpjinan.akarilevel.entity.MemberData

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
    fun getMemberBoosters(member: String): Map<Long, BoosterData> {
        return try {
            val memberData = memberCache[member]
            memberData?.boosters ?: emptyMap()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    /**
     * 新增成员经验加成器。
     *
     * @param member 成员。
     * @param id ID。
     * @param booster 经验加成器。
     */
    fun addMemberBooster(member: String, id: Long, booster: BoosterData) {
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
     * @param id ID。
     */
    fun removeMemberBooster(member: String, id: Long) {
        val data = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                boosters.remove(id)
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, member, json)
    }
}