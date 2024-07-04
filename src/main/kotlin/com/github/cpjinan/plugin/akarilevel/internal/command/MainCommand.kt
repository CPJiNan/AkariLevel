package com.github.cpjinan.plugin.akarilevel.internal.command

import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.DataCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.ExpCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.LevelCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.TraceCommand
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.utils.FileUtil
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import java.io.File

@CommandHeader(name = "AkariLevel")
object MainCommand {
    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody(permission = "akarilevel.default", hidden = true)
    val help = mainCommand { createHelper() }

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
            ConfigManager.settings = YamlConfiguration.loadConfiguration(File(FileUtil.dataFolder, "settings.yml"))
            ConfigManager.level = ConfigManager.getLevelGroups()
            sender.sendLang("Plugin-Reloaded")
        }
    }

}