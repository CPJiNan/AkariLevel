package com.github.cpjinan.plugin.akarilevel.internal.command.subcommand

import com.github.cpjinan.plugin.akarilevel.api.DataAPI
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

object DataCommand {
    val data = subCommand {
        createHelper()
        literal("get") {
            dynamic("table").dynamic("index").dynamic("key") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    sender.sendLang(
                        "Data-Get-Value",
                        context["table"],
                        context["index"],
                        context["key"],
                        DataAPI.getDataValue(context["table"], context["index"], context["key"])
                    )
                }
            }
        }
        literal("set") {
            dynamic("table").dynamic("index").dynamic("key").dynamic("value") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    DataAPI.setDataValue(context["table"], context["index"], context["key"], context["value"])
                    sender.sendLang(
                        "Data-Set-Value",
                        context["table"],
                        context["index"],
                        context["key"],
                        context["value"]
                    )
                }
            }
        }
        literal("reset") {
            dynamic("table").dynamic("index").dynamic("key") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    DataAPI.setDataValue(context["table"], context["index"], context["key"], "")
                    sender.sendLang(
                        "Data-Set-Value",
                        context["table"],
                        context["index"],
                        context["key"],
                        ""
                    )
                }
            }
        }
        literal("save") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                DataAPI.saveData()
                sender.sendLang("Data-Save")
            }
        }
    }
}