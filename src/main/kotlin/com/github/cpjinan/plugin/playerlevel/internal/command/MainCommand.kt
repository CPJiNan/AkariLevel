package com.github.cpjinan.plugin.playerlevel.internal.command

import com.github.cpjinan.plugin.playerlevel.internal.command.subcommand.Debug
import com.github.cpjinan.plugin.playerlevel.internal.command.subcommand.Exp
import com.github.cpjinan.plugin.playerlevel.internal.command.subcommand.Level
import com.github.cpjinan.plugin.playerlevel.internal.command.subcommand.LevelUp
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

@CommandHeader(name = "PlayerLevel", aliases = ["plevel", "level", "exp"], permissionDefault = PermissionDefault.TRUE)
object MainCommand {
  @CommandBody
  val main = mainCommand { createHelper() }

  @CommandBody(permission = "playerlevel.admin", permissionDefault = PermissionDefault.OP)
  val level = Level.level
  @CommandBody(permission = "playerlevel.admin", permissionDefault = PermissionDefault.OP)
  val exp = Exp.exp
  @CommandBody(permission = "playerlevel.admin", permissionDefault = PermissionDefault.OP)
  val debug = Debug.debug

  @CommandBody(permission = "playerlevel.default", permissionDefault = PermissionDefault.TRUE)
  val levelUp = LevelUp.levelUp
}

fun ProxyPlayer.toBukkitPlayer(): Player = Bukkit.getPlayer(this.uniqueId)!!