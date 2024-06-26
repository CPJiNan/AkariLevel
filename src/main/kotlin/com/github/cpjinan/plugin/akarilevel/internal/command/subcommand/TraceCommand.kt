package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupData
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object TraceCommand {
    val trace = subCommand {
        createHelper()
        dynamic("levelGroup") {
            suggest {
                LevelAPI.getLevelGroupNames().toList()
            }
        }.execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if (PlayerAPI.checkPlayerTraceCondition(Bukkit.getPlayer(sender.name)!!, context["levelGroup"])) {
                PlayerAPI.setPlayerTraceLevelGroup(
                    Bukkit.getPlayer(sender.name)!!,
                    context["levelGroup"]
                )
                sender.sendLang(
                    "Trace-Success",
                    context["levelGroup"],
                    getLevelGroupData(context["levelGroup"]).display.colored()
                )
            } else sender.sendLang(
                "Trace-Fail",
                context["levelGroup"],
                getLevelGroupData(context["levelGroup"]).display.colored()
            )
        }
    }
}