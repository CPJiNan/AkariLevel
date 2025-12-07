package top.cpjinan.akarilevel.entity

import java.util.*

/**
 * AkariLevel
 * top.cpjinan.akarilevel.entity
 *
 * 经验加成器数据。
 *
 * @author 季楠
 * @since 2025/12/2 23:30
 */
data class BoosterData(
    var id: UUID,
    var name: String,
    var type: String = "",
    var multiplier: Double = 1.0,
    var start: Long = -1,
    var duration: Long = -1,
    var levelGroup: String = "",
    var source: String = "COMMAND_ADD_EXP"
)