package com.github.cpjinan.plugin.akarilevel.internal.command

import com.github.cpjinan.plugin.akarilevel.api.ScriptAPI
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig.commands
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.DataCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.ExpCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.LevelCommand
import com.github.cpjinan.plugin.akarilevel.internal.command.subcommand.TraceCommand
import com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil.Command
import com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil.CommandParameter
import com.github.cpjinan.plugin.akarilevel.utils.core.CommandUtil.createHelper
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common.platform.function.pluginVersion
import taboolib.common.util.replaceWithOrder
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import java.io.File

@CommandHeader(name = "AkariLevel")
object MainCommand {
    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendHelper()
        }
    }

    @CommandBody(hidden = true)
    val help = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendHelper()
        }
    }

    @CommandBody
    val level = LevelCommand.level

    @CommandBody
    val levelup = LevelCommand.levelup

    @CommandBody
    val trace = TraceCommand.trace

    @CommandBody
    val exp = ExpCommand.exp

    @CommandBody
    val data = DataCommand.data

    @CommandBody
    val reload = subCommand {
        execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            PluginConfig.settings = YamlConfiguration.loadConfiguration(File(FileUtil.dataFolder, "settings.yml"))
            PluginConfig.level = PluginConfig.getLevelGroups()
            PluginConfig.commands = YamlConfiguration.loadConfiguration(File(FileUtil.dataFolder, "commands.yml"))
            ScriptAPI.reloadScript()
            Language.reload()
            sender.sendLang("Plugin-Reloaded")
        }
    }

    fun ProxyCommandSender.sendHelper() {
        this.createHelper(
            plugin = commands.getString("plugin")!!,
            version = commands.getString("version")!!.replaceWithOrder(pluginVersion),
            mainCommand = Command(
                name = commands.getString("mainCommand.name")!!,
                parameters = listOf(
                    CommandParameter(
                        commands.getString("mainCommand.parameters.1.name")!!,
                        optional = true
                    )
                )
            ),
            subCommands = arrayOf(
                Command(
                    name = commands.getString("subCommands.1.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.1.parameters.1.name")!!,
                            description = commands.getString("subCommands.1.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.1.parameters.2.name")!!,
                            description = commands.getString("subCommands.1.parameters.2.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.1.parameters.3.name")!!,
                            description = commands.getString("subCommands.1.parameters.3.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.1.parameters.4.name")!!,
                            description = commands.getString("subCommands.1.parameters.4.description")!!,
                            optional = true
                        )
                    ),
                    info = commands.getString("subCommands.1.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.2.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.2.parameters.1.name")!!,
                            description = commands.getString("subCommands.2.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.2.parameters.2.name")!!,
                            description = commands.getString("subCommands.2.parameters.2.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.2.parameters.3.name")!!,
                            description = commands.getString("subCommands.2.parameters.3.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.2.parameters.4.name")!!,
                            description = commands.getString("subCommands.2.parameters.4.description")!!,
                            optional = true
                        )
                    ),
                    info = commands.getString("subCommands.2.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.3.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.3.parameters.1.name")!!,
                            description = commands.getString("subCommands.3.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.3.parameters.2.name")!!,
                            description = commands.getString("subCommands.3.parameters.2.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.3.parameters.3.name")!!,
                            description = commands.getString("subCommands.3.parameters.3.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.3.parameters.4.name")!!,
                            description = commands.getString("subCommands.3.parameters.4.description")!!,
                            optional = true
                        )
                    ),
                    info = commands.getString("subCommands.3.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.4.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.4.parameters.1.name")!!,
                            description = commands.getString("subCommands.4.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.4.parameters.2.name")!!,
                            description = commands.getString("subCommands.4.parameters.2.description")!!
                        )
                    ),
                    info = commands.getString("subCommands.4.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.5.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.5.parameters.1.name")!!,
                            description = commands.getString("subCommands.5.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.5.parameters.2.name")!!,
                            description = commands.getString("subCommands.5.parameters.2.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.5.parameters.3.name")!!,
                            description = commands.getString("subCommands.5.parameters.3.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.5.parameters.4.name")!!,
                            description = commands.getString("subCommands.5.parameters.4.description")!!,
                            optional = true
                        )
                    ),
                    info = commands.getString("subCommands.5.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.6.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.6.parameters.1.name")!!,
                            description = commands.getString("subCommands.6.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.6.parameters.2.name")!!,
                            description = commands.getString("subCommands.6.parameters.2.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.6.parameters.3.name")!!,
                            description = commands.getString("subCommands.6.parameters.3.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.6.parameters.4.name")!!,
                            description = commands.getString("subCommands.6.parameters.4.description")!!,
                            optional = true
                        )
                    ),
                    info = commands.getString("subCommands.6.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.7.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.7.parameters.1.name")!!,
                            description = commands.getString("subCommands.7.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.7.parameters.2.name")!!,
                            description = commands.getString("subCommands.7.parameters.2.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.7.parameters.3.name")!!,
                            description = commands.getString("subCommands.7.parameters.3.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.7.parameters.4.name")!!,
                            description = commands.getString("subCommands.7.parameters.4.description")!!,
                            optional = true
                        )
                    ),
                    info = commands.getString("subCommands.7.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.8.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.8.parameters.1.name")!!,
                            description = commands.getString("subCommands.8.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.8.parameters.2.name")!!,
                            description = commands.getString("subCommands.8.parameters.2.description")!!
                        )
                    ),
                    info = commands.getString("subCommands.8.info")!!,
                    needOP = true
                ),
                Command(
                    name = commands.getString("subCommands.9.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.9.parameters.1.name")!!,
                            description = commands.getString("subCommands.9.parameters.1.description")!!
                        )
                    ),
                    info = commands.getString("subCommands.9.info")!!
                ),
                Command(
                    name = commands.getString("subCommands.10.name")!!,
                    parameters = listOf(
                        CommandParameter(
                            name = commands.getString("subCommands.10.parameters.1.name")!!,
                            description = commands.getString("subCommands.10.parameters.1.description")!!
                        ),
                        CommandParameter(
                            name = commands.getString("subCommands.10.parameters.2.name")!!,
                            description = commands.getString("subCommands.10.parameters.2.description")!!
                        )
                    ),
                    info = commands.getString("subCommands.10.info")!!
                ),
                Command(
                    name = commands.getString("subCommands.11.name")!!,
                    info = commands.getString("subCommands.11.info")!!
                )
            )
        )
    }

}