package com.github.cpjinan.plugin.akarilevel.command

import com.github.cpjinan.plugin.akarilevel.cache.forcePersistAllPlayers
import com.github.cpjinan.plugin.akarilevel.cache.forcePersistPlayer
import com.github.cpjinan.plugin.akarilevel.cache.getPersistenceStats
import com.github.cpjinan.plugin.akarilevel.manager.SmartPersistenceManager
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.chat.colored

/**
 * @author QwQ-dev
 * @since 2025/8/12 17:00
 */
@CommandHeader("alpersistence", aliases = ["alp"], permission = "akarilevel.admin")
object PersistenceCommand {
    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val stats = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendMessage(getPersistenceStats().colored())
        }
    }

    @CommandBody
    val save = subCommand {
        execute<CommandSender> { sender, _, _ ->
            val savedCount = forcePersistAllPlayers()
            sender.sendMessage("&a已强制保存 $savedCount 个玩家的数据".colored())
        }
    }

    @CommandBody
    val savePlayer = subCommand {
        dynamic("player") {
            execute<CommandSender> { sender, _, argument ->
                val player = argument
                val success = forcePersistPlayer(player)
                if (success) {
                    sender.sendMessage("&a已保存玩家 $player 的数据".colored())
                } else {
                    sender.sendMessage("&c保存玩家 $player 的数据失败".colored())
                }
            }
        }
    }

    @CommandBody
    val checkpoint = subCommand {
        execute<CommandSender> { sender, _, _ ->
            SmartPersistenceManager.createCheckpoint()
            sender.sendMessage("&a已执行检查点操作".colored())
        }
    }

    @CommandBody
    val reset = subCommand {
        execute<CommandSender> { sender, _, _ ->
            SmartPersistenceManager.resetStats()
            sender.sendMessage("&a已重置持久化统计信息".colored())
        }
    }
}