package top.cpjinan.akarilevel.command.subcommand

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.module.lang.sendLang
import top.cpjinan.akarilevel.level.ConfigLevelGroup
import top.cpjinan.akarilevel.level.LevelGroup

/**
 * AkariLevel
 * top.cpjinan.akarilevel.command.subcommand
 *
 * 等级组子命令。
 *
 * @author 季楠
 * @since 2025/8/14 20:35
 */
object LevelGroupCommand {
    val levelGroup = subCommand {
        // 查看等级组信息命令。
        literal("info").dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]
                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }
                sender.sendLang("LevelGroupInfo", group.name, group.display)
            }
        }

        // 查看等级组列表命令。
        literal("list") {
            execute<ProxyCommandSender> { sender, _, argument ->
                val pageSize = 10
                val levelGroups = LevelGroup.getLevelGroups().values.sortedBy { it.name }
                val totalPages = (levelGroups.size + pageSize - 1) / pageSize
                val currentPage = argument.substringAfter(" ").toIntOrNull()?.coerceIn(1, totalPages) ?: 1
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

        // 取消注册等级组命令。
        literal("unregister").dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]
                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }
                group.unregister()
                sender.sendLang("LevelGroupUnregister", group.name)
            }
        }

        // 重新注册等级组命令。
        literal("reregister").dynamic("levelGroup") {
            suggestUncheck { LevelGroup.getLevelGroups().keys.sortedBy { it } }
            execute<ProxyCommandSender> { sender, context, _ ->
                val group = LevelGroup.getLevelGroups()[context["levelGroup"]]
                if (group == null) {
                    sender.sendLang("LevelGroupNotFound", context["levelGroup"])
                    return@execute
                }
                group.unregister()
                group.register()
                sender.sendLang("LevelGroupReregister", group.name)
            }
        }

        // 重载配置等级组命令。
        literal("reload") {
            execute<ProxyCommandSender> { sender, _, _ ->
                ConfigLevelGroup.reloadConfigLevelGroups()
                sender.sendLang("LevelGroupReload", ConfigLevelGroup.getConfigLevelGroups().size)
            }
        }
    }
}