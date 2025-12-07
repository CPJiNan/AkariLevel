package top.cpjinan.akarilevel.booster

import taboolib.common.platform.event.SubscribeEvent
import top.cpjinan.akarilevel.cache.MemberCache
import top.cpjinan.akarilevel.cache.MemberCache.memberCache
import top.cpjinan.akarilevel.database.Database
import top.cpjinan.akarilevel.entity.MemberData
import top.cpjinan.akarilevel.event.MemberExpChangeEvent
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
     * 新增成员经验加成器。
     *
     * @param member 成员。
     * @param booster 经验加成器。
     */
    fun addMemberBooster(member: String, booster: BoosterData) {
        val data = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                boosters.putIfAbsent(booster.id, booster)
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, member, json)
    }

    /**
     * 移除成员经验加成器。
     *
     * @param member 成员。
     * @param id 经验加成器 UUID。
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

    /**
     * 成员经验加成器是否已启用。
     *
     * @param member 成员。
     * @param id 经验加成器 UUID。
     * @return 如果此成员经验加成器已启用，则返回 true。
     */
    fun isMemberBoosterEnabled(member: String, id: UUID): Boolean {
        return try {
            val memberData = memberCache[member]
            memberData?.boosters[id]?.start != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 启用成员经验加成器。
     *
     * @param member 成员。
     * @param id 经验加成器 UUID。
     */
    fun enableMemberBooster(member: String, id: UUID) {
        val data = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                val booster = boosters[id] ?: return@compute this
                if (booster.start != -1L) return@compute this
                booster.start = System.currentTimeMillis()
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, member, json)
    }

    /**
     * 禁用成员经验加成器。
     *
     * @param member 成员。
     * @param id 经验加成器 UUID。
     */
    fun disableMemberBooster(member: String, id: UUID) {
        val data = memberCache.asMap().compute(member) { _, memberData ->
            (memberData ?: MemberData()).apply {
                val booster = boosters[id] ?: return@compute this
                if (booster.start == -1L) return@compute this
                if (booster.duration != -1L) {
                    booster.duration -= (System.currentTimeMillis() - booster.start).coerceAtLeast(0)
                    if (booster.duration <= 0) boosters.remove(id)
                }
                booster.start = -1
            }
        }

        val json = MemberCache.gson.toJson(data)
        Database.instance.set(Database.instance.memberTable, member, json)
    }

    @SubscribeEvent
    fun onMemberExpChange(event: MemberExpChangeEvent) {
        val member = event.member
        val levelGroup = event.levelGroup
        val expAmount = event.expAmount
        val source = event.source

        if (expAmount <= 0) return
        refreshMemberBoosters(member)

        val boosters = getMemberBoosters(member).filter { it.value.start != -1L }.filter { it.value.duration != -1L }
            .filter { it.value.levelGroup.isEmpty() || it.value.levelGroup == levelGroup }
            .filter { it.value.source.isEmpty() || it.value.source == source }

        val multiplier = boosters.values.groupBy { it.type }.values
            .fold(1.0) { acc, group ->
                acc * group.maxOf { it.multiplier }
            }

        val boosterEvent = BoosterEvent(member, levelGroup, expAmount, source, boosters, multiplier)
        boosterEvent.call()
        if (boosterEvent.isCancelled) return

        event.expAmount = (boosterEvent.expAmount * boosterEvent.multiplier).toLong()
    }
}