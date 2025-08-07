package com.github.cpjinan.plugin.akarilevel.command

import com.github.cpjinan.plugin.akarilevel.event.PluginReloadEvent
import com.github.cpjinan.plugin.akarilevel.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.manager.LanguageManager
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

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
    permission = "AkariLevel.command.use",
    permissionDefault = PermissionDefault.TRUE
)
object MainCommand {
    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(
        permission = "AkariLevel.command.reload.use",
        permissionDefault = PermissionDefault.OP
    )
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            PluginReloadEvent.Pre().call()

            // 重载配置文件。
            ConfigManager.reload()

            // 重载语言文件。
            LanguageManager.reload()

            PluginReloadEvent.Post().call()
            sender.sendLang("Plugin-Reloaded")
        }
    }
}