package com.github.cpjinan.plugin.akarilevel.command

import com.github.cpjinan.plugin.akarilevel.command.subcommand.LevelGroupCommand
import com.github.cpjinan.plugin.akarilevel.command.subcommand.MemberCommand
import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import com.github.cpjinan.plugin.akarilevel.event.PluginReloadEvent
import com.github.cpjinan.plugin.akarilevel.level.ConfigLevelGroup
import com.github.cpjinan.plugin.akarilevel.script.ScriptManager
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import taboolib.platform.util.bukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.command
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
            ScriptManager.reload()

            PluginReloadEvent.Post().call()
            sender.sendLang("PluginReloaded")
        }
    }
}