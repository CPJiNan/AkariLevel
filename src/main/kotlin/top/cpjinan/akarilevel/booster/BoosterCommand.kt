package top.cpjinan.akarilevel.booster

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang
import taboolib.platform.util.bukkitPlugin

/**
 * AkariLevel
 * top.cpjinan.akarilevel.booster
 *
 * 经验加成器命令。
 *
 * @author 季楠
 * @since 2025/12/7 14:32
 */
@CommandHeader(
    name = "akarilevelbooster",
    aliases = ["alb"],
    permission = "AkariLevel.command.booster.use",
    permissionDefault = PermissionDefault.OP
)
object BoosterCommand {
    @CommandBody(
        permission = "AkariLevel.command.booster.use",
        permissionDefault = PermissionDefault.OP
    )
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("CommandHelpBooster", bukkitPlugin.description.version)
        }
    }

    @CommandBody(
        permission = "AkariLevel.command.booster.use",
        permissionDefault = PermissionDefault.OP
    )
    val list = subCommand {
        dynamic("member") {
            execute<ProxyCommandSender> { sender, context, content ->
                val pageSize = 10
                val boosters = BoosterHandler.getMemberBoosters(context["member"]).values.sortedBy { it.name }
                val totalPages = (boosters.size + pageSize - 1) / pageSize
                val currentPage = content.substringAfter(" ").toIntOrNull()?.coerceIn(1, totalPages) ?: 1
                with(sender) {
                    sendLang("BoosterListHeader", boosters.size)
                    boosters
                        .subList((currentPage - 1) * pageSize, (currentPage * pageSize).coerceAtMost(boosters.size))
                        .forEach { sendLang("BoosterListFormat", it.id, it.name) }
                    sendLang(
                        "BoosterListFooter",
                        currentPage,
                        totalPages,
                        (currentPage - 1).coerceAtLeast(1),
                        (currentPage + 1).coerceAtMost(totalPages)
                    )
                }
            }
        }
    }

    private fun parseCommandArgs(args: String): Map<String, String> {
        return args.split(' ')
            .filter { it.startsWith("-") }
            .associate {
                it.removePrefix("-").removePrefix("-").split('=', limit = 2)
                    .run { this[0] to getOrElse(1) { "" } }
            }
    }
}