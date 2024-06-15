package com.github.cpjinan.plugin.akarilevel.internal.command

import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.DataCommand
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

@CommandHeader(name = "AkariLevel")
object MainCommand {
    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody(permission = "akarilevel.default", hidden = true)
    val help = mainCommand { createHelper() }

    @CommandBody(permission = "akarilevel.admin")
    val data = DataCommand.data

    @CommandBody(permission = "akarilevel.admin")
    val reload = subCommand {
        execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            ConfigManager.settings.reload()
            sender.sendLang("Plugin-Reloaded")
        }
    }

}
