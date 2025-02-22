package com.github.cpjinan.plugin.akarilevel.internal.command

import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.github.cpjinan.plugin.akarilevel.common.PluginScript
import com.github.cpjinan.plugin.akarilevel.common.event.plugin.PluginReloadEvent
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.DataCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.ExpCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.LevelCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.TraceCommand
import com.github.cpjinan.plugin.akarilevel.utils.script.Kether.evalKether
import com.github.cpjinan.plugin.akarilevel.utils.script.nashorn.getScriptEngine
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import java.io.File

@CommandHeader(name = "AkariLevel", aliases = ["al"])
object MainCommand {
    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("Command-Help", pluginVersion)
        }
    }

    @CommandBody(hidden = true)
    val help = subCommand {
        dynamic("page", optional = true) {
            execute<ProxyCommandSender> { sender, context, _ ->
                sender.sendLang("Command-Help-${context["page"]}", pluginVersion)
            }
        }
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("Command-Help", pluginVersion)
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
    val script = subCommand {
        literal("eval") {
            literal(aliases = arrayOf("javascript", "js")).dynamic {
                execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val engine = getScriptEngine()
                        engine.put("sender", sender)
                        sender.sendMessage("§8§l‹ ›§r §7Result: §f${engine.eval(content) ?: ""}")
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
            literal(aliases = arrayOf("kether", "ke")).dynamic {
                execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, content: String ->
                    sender.castSafely<Player>()
                        ?.let { sender.sendMessage("§8§l‹ ›§r §7Result: §f${content.evalKether(it)}") }
                }
            }
        }
    }

    @CommandBody(permission = "akarilevel.admin")
    val reload = subCommand {
        execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            PluginReloadEvent.Pre().call()
            PluginConfig.settings = YamlConfiguration.loadConfiguration(File(getDataFolder(), "settings.yml"))
            PluginConfig.level = PluginConfig.getLevelGroups()
            PluginScript.reload()
            Language.reload()
            PluginReloadEvent.Post().call()
            sender.sendLang("Plugin-Reloaded")
        }
    }
}