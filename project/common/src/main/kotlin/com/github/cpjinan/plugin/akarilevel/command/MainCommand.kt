package com.github.cpjinan.plugin.akarilevel.command

import com.github.cpjinan.plugin.akarilevel.AkariLevelSettings
import com.github.cpjinan.plugin.akarilevel.event.AkariLevelReloadEvent
import com.github.cpjinan.plugin.akarilevel.util.DebugUtils
import com.github.cpjinan.plugin.akarilevel.util.DebugUtils.debug
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.command
 *
 * @author 季楠
 * @since 2025/1/22 23:19
 */
@CommandHeader(name = "AkariLevel", aliases = ["al"])
object MainCommand {
    @CommandBody(permission = "akarilevel.admin")
    val reload = subCommand {
        execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            DebugUtils.debug(
                "&8[&3Akari&bLevel&8] &5调试&7#3 &8| &6触发插件重载命令，正在展示处理逻辑。",
                "&r============================="
            )

            AkariLevelReloadEvent().call()
            val start = System.currentTimeMillis()
            var time = System.currentTimeMillis()

            AkariLevelSettings.config.reload()
            debug("&r| &b◈ &r配置文件重载完成，用时 ${System.currentTimeMillis() - time}ms。")
            time = System.currentTimeMillis()

            Language.reload()
            debug("&r| &b◈ &r语言文件重载完成，用时 ${System.currentTimeMillis() - time}ms。")

            debug(
                "&r| &a◈ &r插件重载完毕，总计用时 ${System.currentTimeMillis() - start}ms。",
                "&r============================="
            )

            sender.sendLang("Plugin-Reloaded")
        }
    }
}