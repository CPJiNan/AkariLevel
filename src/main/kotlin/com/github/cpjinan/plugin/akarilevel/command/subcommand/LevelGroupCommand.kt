package com.github.cpjinan.plugin.akarilevel.command.subcommand

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.sendLang

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.command.subcommand
 *
 * 等级组子命令。
 *
 * @author 季楠
 * @since 2025/8/14 20:35
 */
object LevelGroupCommand {
    val levelGroup = subCommand {
        // 查看等级组列表命令。
        literal("list") {
            execute<ProxyCommandSender> { sender, _, content ->
                val pageSize = 10
                val levelGroups = LevelGroup.getLevelGroups().values.sortedBy { it.name }
                val totalPages = (levelGroups.size + pageSize - 1) / pageSize
                val currentPage = content.substringAfter(" ").toIntOrNull()?.coerceIn(1, totalPages) ?: 1

                with(sender) {
                    sendLang("LevelGroupListHeader", levelGroups.size)

                    levelGroups
                        .subList((currentPage - 1) * pageSize, (currentPage * pageSize).coerceAtMost(levelGroups.size))
                        .forEach { sendLang("LevelGroupListFormat", it.name, it.display) }

                    sendLang(
                        "LevelGroupListFooter",
                        currentPage,
                        totalPages,
                        (currentPage - 1).coerceAtLeast(1),
                        (currentPage + 1).coerceAtMost(totalPages)
                    )
                }
            }
        }
    }
}