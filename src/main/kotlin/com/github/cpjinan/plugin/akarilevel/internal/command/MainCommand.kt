package com.github.cpjinan.plugin.akarilevel.internal.command

import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.Exp
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.Level
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

@CommandHeader(name = "AkariLevel", aliases = ["playerlevel"])
object MainCommand {
    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody(permission = "akarilevel.admin")
    val level = Level.level

    @CommandBody(permission = "akarilevel.default")
    val levelup = Level.levelup

    @CommandBody(permission = "akarilevel.admin")
    val exp = Exp.exp

    @CommandBody(permission = "akarilevel.default", hidden = true)
    val help = mainCommand { createHelper() }

    @CommandBody(permission = "akarilevel.admin")
    val reload = subCommand {
        execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            ConfigManager.settings.reload()
            ConfigManager.levelConfig.reload()
            sender.sendLang("Plugin-Reloaded")
        }
    }

}
