package top.cpjinan.akarilevel.booster

/**
 * AkariLevel
 * top.cpjinan.akarilevel.booster
 *
 * 经验加成器命令。
 *
 * @author 季楠
 * @since 2025/12/7 14:32
 */
object BoosterCommand {
    fun parseCommandArgs(args: String): Map<String, String> {
        return args.split(' ')
            .filter { it.startsWith("-") }
            .associate {
                it.removePrefix("-").removePrefix("-").split('=', limit = 2)
                    .run { this[0] to getOrElse(1) { "" } }
            }
    }
}