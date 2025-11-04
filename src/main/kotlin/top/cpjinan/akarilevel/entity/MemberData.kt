package top.cpjinan.akarilevel.entity

import java.util.concurrent.ConcurrentHashMap

/**
 * AkariLevel
 * top.cpjinan.akarilevel.entity
 *
 * 成员数据。
 *
 * @author 季楠
 * @since 2025/8/7 22:58
 */
data class MemberData(
    val levelGroups: MutableMap<String, MemberLevelData> = ConcurrentHashMap()
)