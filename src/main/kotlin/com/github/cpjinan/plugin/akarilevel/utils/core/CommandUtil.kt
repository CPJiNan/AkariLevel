package com.github.cpjinan.plugin.akarilevel.utils.core

object CommandUtil {
    /**
     * 解析命令行参数及其对应值
     * @return 参数及对应值
     */
    @JvmStatic
    fun parseOptions(args: List<String>): HashMap<String, String?> {
        val options = hashMapOf<String, String?>()
        var i = 0
        while (i < args.size) {
            val arg = args[i]
            if (arg.startsWith("--") || arg.startsWith("-")) {
                val (key, value) = if (arg.contains("=")) {
                    val splitArg = arg.split("=", limit = 2)
                    val key = splitArg[0].removePrefix("-").removePrefix("-")
                    val value = splitArg[1]
                    key to value
                } else {
                    val key = arg.removePrefix("-").removePrefix("-")
                    val value = if (i + 1 < args.size && !args[i + 1].startsWith("-")) args[++i] else null
                    key to value
                }
                options[key] = value
            }
            i++
        }
        return options
    }
}