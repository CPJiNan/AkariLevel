package top.cpjinan.akarilevel.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import taboolib.platform.util.bukkitPlugin
import top.cpjinan.akarilevel.command.subcommand.BoosterCommand
import top.cpjinan.akarilevel.command.subcommand.LevelGroupCommand
import top.cpjinan.akarilevel.command.subcommand.MemberCommand
import top.cpjinan.akarilevel.config.SettingsConfig
import top.cpjinan.akarilevel.event.PluginReloadEvent
import top.cpjinan.akarilevel.level.ConfigLevelGroup
import top.cpjinan.akarilevel.script.ScriptHandler

/**
 * AkariLevel
 * top.cpjinan.akarilevel.command
 *
 * 插件主命令。
 *
 * @author 季楠
 * @since 2025/8/7 22:17
 */
@CommandHeader(
    name = "akarilevel",
    aliases = ["al"],
    permission = "AkariLevel.command.use",
    permissionDefault = PermissionDefault.OP
)
object MainCommand {
    @CommandBody(
        permission = "AkariLevel.command.use",
        permissionDefault = PermissionDefault.OP
    )
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("CommandHelp", bukkitPlugin.description.version)
        }
    }

    @CommandBody(
        hidden = true,
        permission = "AkariLevel.command.help.use",
        permissionDefault = PermissionDefault.OP
    )
    val help = subCommand {
        execute<ProxyCommandSender> { sender, _, content ->
            val version = bukkitPlugin.description.version
            if (content.contains(" ")) sender.sendLang("CommandHelp${content.substringAfter(" ")}", version)
            else sender.sendLang("CommandHelp", version)
        }
    }

    @CommandBody(
        permission = "AkariLevel.command.levelGroup.use",
        permissionDefault = PermissionDefault.OP
    )
    val levelGroup = LevelGroupCommand.levelGroup

    @CommandBody(
        permission = "AkariLevel.command.member.use",
        permissionDefault = PermissionDefault.OP
    )
    val member = MemberCommand.member

    @CommandBody(
        permission = "AkariLevel.command.booster.use",
        permissionDefault = PermissionDefault.OP
    )
    val booster = BoosterCommand.booster

    @CommandBody(
        permission = "AkariLevel.command.reload.use",
        permissionDefault = PermissionDefault.OP
    )
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            PluginReloadEvent.Pre().call()

            // 重载配置文件。
            SettingsConfig.settings.reload()

            // 重载配置等级组。
            ConfigLevelGroup.reloadConfigLevelGroups()

            // 重载语言文件。
            Language.reload()

            // 重载脚本。
            ScriptHandler.reload()

            PluginReloadEvent.Post().call()
            sender.sendLang("PluginReloaded")
        }
    }
}