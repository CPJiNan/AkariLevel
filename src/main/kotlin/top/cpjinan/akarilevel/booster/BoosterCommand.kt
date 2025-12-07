package top.cpjinan.akarilevel.booster

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand
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

    private fun parseCommandArgs(args: String): Map<String, String> {
        return args.split(' ')
            .filter { it.startsWith("-") }
            .associate {
                it.removePrefix("-").removePrefix("-").split('=', limit = 2)
                    .run { this[0] to getOrElse(1) { "" } }
            }
    }
}