package top.cpjinan.akarilevel.booster

import taboolib.common.platform.event.SubscribeEvent
import top.cpjinan.akarilevel.cache.MemberCache
import top.cpjinan.akarilevel.database.Database
import top.cpjinan.akarilevel.entity.MemberData
import top.cpjinan.akarilevel.event.MemberExpChangeEvent

/**
 * AkariLevel
 * top.cpjinan.akarilevel.booster
 *
 * 经验加成器。
 *
 * @author 季楠
 * @since 2025/12/2 23:30
 */
data class Booster(
    var id: String,
    var name: String,
    var type: String = "",
    var multiplier: Double = 1.0,
    var start: Long = -1,
    var duration: Long = -1,
    var levelGroup: String = "",
    var source: String = "COMMAND_ADD_EXP"
) {
    companion object {
        /**
         * 获取成员经验加成器列表。
         *
         * @param member 成员。
         * @return 包含请求的所有键值对的 Map。
         */
        @JvmStatic
        fun getMemberBoosters(member: String): Map<String, Booster> {
            return try {
                val memberData = MemberCache.memberCache[member]
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
        @JvmStatic
        fun addMemberBooster(member: String, booster: Booster) {
            val data = MemberCache.memberCache.asMap().compute(member) { _, memberData ->
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
         * @param id 经验加成器 ID。
         */
        @JvmStatic
        fun removeMemberBooster(member: String, id: String) {
            val data = MemberCache.memberCache.asMap().compute(member) { _, memberData ->
                (memberData ?: MemberData()).apply {
                    boosters.remove(id)
                }
            }

            val json = MemberCache.gson.toJson(data)
            Database.instance.set(Database.instance.memberTable, member, json)
        }

        /**
         * 成员经验加成器是否已启用。
         *
         * @param member 成员。
         * @param id 经验加成器 ID。
         * @return 如果此成员经验加成器已启用，则返回 true。
         */
        @JvmStatic
        fun isMemberBoosterEnabled(member: String, id: String): Boolean {
            return try {
                val memberData = MemberCache.memberCache[member]
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
         * @param id 经验加成器 ID。
         */
        @JvmStatic
        fun enableMemberBooster(member: String, id: String) {
            val data = MemberCache.memberCache.asMap().compute(member) { _, memberData ->
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
         * @param id 经验加成器 ID。
         */
        @JvmStatic
        fun disableMemberBooster(member: String, id: String) {
            val data = MemberCache.memberCache.asMap().compute(member) { _, memberData ->
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

        /**
         * 刷新成员经验加成器。
         *
         * @param member 成员。
         */
        @JvmStatic
        fun refreshMemberBoosters(member: String) {
            val oldData = MemberCache.memberCache.getIfPresent(member)
            val newData = MemberCache.memberCache.asMap().compute(member) { _, memberData ->
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

        @SubscribeEvent
        fun onMemberExpChange(event: MemberExpChangeEvent) {
            val member = event.member
            val levelGroup = event.levelGroup
            val expAmount = event.expAmount
            val source = event.source

            if (expAmount <= 0) return
            refreshMemberBoosters(member)

            val boosters =
                getMemberBoosters(member).filter { it.value.start != -1L }.filter { it.value.duration != -1L }
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
}