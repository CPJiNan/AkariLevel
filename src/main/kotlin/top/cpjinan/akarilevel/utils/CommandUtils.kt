package top.cpjinan.akarilevel.utils

/**
 * AkariLevel
 * top.cpjinan.akarilevel.utils
 *
 * 命令工具类。
 *
 * @author 季楠
 * @since 2025/12/14 09:44
 */
object CommandUtils {
    fun parseCommandArgs(args: String): Map<String, String> {
        return args.split(' ')
            .filter { it.startsWith("-") }
            .associate {
                it.removePrefix("-").removePrefix("-").split('=', limit = 2)
                    .run { this[0] to getOrElse(1) { "" } }
            }
    }
}