package com.github.cpjinan.plugin.akarilevel.command

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig
import com.github.cpjinan.plugin.akarilevel.event.PluginReloadEvent
import com.github.cpjinan.plugin.akarilevel.utils.LoggerUtils.debug
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.command
 *
 * @author 季楠
 * @since 2025/7/27 20:01
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
            debug("&8[&3Akari&bLevel&8] &5调试&7#1 &8| &6触发插件重载命令，正在展示处理逻辑。")

            PluginReloadEvent.Pre().call()
            val start = System.currentTimeMillis()
            var time = start

            SettingsConfig.settings.reload()
            debug("&r| &b◈ &r#1 配置文件重载完成，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()

            val languageAPI = AkariLevel.api().getLanguage()
            languageAPI.reload()
            debug("&r| &b◈ &r#1 语言文件重载完成，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()

            val databaseAPI = AkariLevel.api().getDatabase()
            databaseAPI.getDefault().reload()
            debug("&r| &b◈ &r#1 数据库重载完成，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()

            debug("&r| &a◈ &r#1 插件重载完毕，总计用时 ${System.currentTimeMillis() - start}ms。")

            PluginReloadEvent.Post().call()
            sender.sendLang("Plugin-Reloaded")
        }
    }
}