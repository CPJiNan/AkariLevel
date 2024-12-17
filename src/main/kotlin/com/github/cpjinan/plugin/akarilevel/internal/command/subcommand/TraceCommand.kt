package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupData
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil
import org.bukkit.command.CommandSender
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object TraceCommand {
    val trace = subCommand {
        dynamic("levelGroup") {
            suggest {
                LevelAPI.getLevelGroupNames().toList()
            }
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                if (PlayerAPI.checkPlayerTraceCondition(sender.cast(), context["levelGroup"])) {
                    PlayerAPI.setPlayerTraceLevelGroup(
                        sender.cast(),
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
        }.dynamic("options") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                listOf("--silent")
            }
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                val args = CommandUtil.parseOptions(content.split(" "))
                var silent = false

                for ((k, _) in args) {
                    when (k.lowercase()) {
                        "silent" -> silent = true
                    }
                }

                if (PlayerAPI.checkPlayerTraceCondition(sender.cast(), context["levelGroup"])) {
                    PlayerAPI.setPlayerTraceLevelGroup(
                        sender.cast(),
                        context["levelGroup"]
                    )
                    if (!silent) sender.sendLang(
                        "Trace-Success",
                        context["levelGroup"],
                        getLevelGroupData(context["levelGroup"]).display.colored()
                    )
                } else if (!silent) sender.sendLang(
                    "Trace-Fail",
                    context["levelGroup"],
                    getLevelGroupData(context["levelGroup"]).display.colored()
                )
            }
        }
    }
}