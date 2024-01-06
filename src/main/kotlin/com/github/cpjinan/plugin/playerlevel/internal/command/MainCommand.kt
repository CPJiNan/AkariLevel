package com.github.cpjinan.plugin.playerlevel.internal.command

import com.github.cpjinan.plugin.playerlevel.internal.command.subcommand.Debug
import com.github.cpjinan.plugin.playerlevel.internal.command.subcommand.Exp
import com.github.cpjinan.plugin.playerlevel.internal.command.subcommand.Level
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand
import taboolib.expansion.createHelper

/**
 * 主命令
 * @author CPJiNan
 * @date 2024/01/06
 */
@CommandHeader(name = "PlayerLevel", aliases = ["plevel", "level", "exp"], permissionDefault = PermissionDefault.TRUE)
object MainCommand {
    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody
    val level = Level.level

    @CommandBody
    val exp = Exp.exp

    @CommandBody
    val debug = Debug.debug

    @CommandBody
    val levelUp = Level.levelUp
}

fun ProxyPlayer.toBukkitPlayer(): Player = Bukkit.getPlayer(this.uniqueId)!!