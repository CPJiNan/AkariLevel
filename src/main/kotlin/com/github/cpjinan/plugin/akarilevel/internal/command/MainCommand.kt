package com.github.cpjinan.plugin.akarilevel.internal.command

import com.github.cpjinan.plugin.akarilevel.api.ScriptAPI
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.DataCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.ExpCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.LevelCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.TraceCommand
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import java.io.File

@CommandHeader(name = "AkariLevel")
object MainCommand {
    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("Command-Help")
        }
    }

    @CommandBody(hidden = true)
    val help = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("Command-Help")
        }
    }

    @CommandBody(permission = "akarilevel.admin")
    val level = LevelCommand.level

    @CommandBody(permission = "akarilevel.default")
    val levelup = LevelCommand.levelup

    @CommandBody(permission = "akarilevel.default")
    val trace = TraceCommand.trace

    @CommandBody(permission = "akarilevel.admin")
    val exp = ExpCommand.exp

    @CommandBody(permission = "akarilevel.admin")
    val data = DataCommand.data

    @CommandBody(permission = "akarilevel.admin")
    val reload = subCommand {
        execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            PluginConfig.settings = YamlConfiguration.loadConfiguration(File(FileUtil.dataFolder, "settings.yml"))
            PluginConfig.level = PluginConfig.getLevelGroups()
            ScriptAPI.reloadScript()
            Language.reload()
            sender.sendLang("Plugin-Reloaded")
        }
    }
}