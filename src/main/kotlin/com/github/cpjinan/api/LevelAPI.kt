package com.github.cpjinan.api

import com.github.cpjinan.manager.ConfigManager
import com.github.cpjinan.manager.RegisterManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.util.sendLang

object LevelAPI {
  // region basic function
  private fun getLevel(player: Player): Int = RegisterManager.getDatabase().getPlayerByName(player.name).level

  private fun setLevel(player: Player, level: Int) {
    val db = RegisterManager.getDatabase()
    val data = db.getPlayerByName(player.name)
    data.level = level
    db.updatePlayer(player.name, data)
    KetherShell.eval(
      ConfigManager.getLevelChangeAction(level),
      ScriptOptions.builder().namespace(emptyList()).sender(sender = adaptPlayer(player)).build()
    )
  }

  private fun getExp(player: Player): Int = RegisterManager.getDatabase().getPlayerByName(player.name).exp

  private fun setExp(player: Player, exp: Int) {
    val db = RegisterManager.getDatabase()
    val data = db.getPlayerByName(player.name)
    data.exp = exp
    db.updatePlayer(player.name, data)
  }

  private fun doLevelUp(player: Player, fromTickLvl: Boolean = false) {
    val curLvl = getLevel(player)

    if (curLvl < ConfigManager.getMaxLevel()) {
      val curExp = getExp(player)
      val targetLvl = curLvl + 1
      val reqExp = ConfigManager.getLevelExp(targetLvl)
      if (curExp >= reqExp) {
        setExp(player, curExp - reqExp)
        setLevel(player, targetLvl)
        player.sendLang("level-up-success")

        if (!fromTickLvl) {
          tickLevel(player)
        }
      } else {
        player.sendLang("level-up-fail")
      }
    } else {
      player.sendLang("max-level")
    }
  }

  private fun tickLevel(player: Player) {
    var isLevelUp: Boolean
    do {
      isLevelUp = false

      val curLvl = getLevel(player)
      val maxLevel = ConfigManager.getMaxLevel()
      if (curLvl < maxLevel) {
        val curExp = getExp(player)
        val reqExp = ConfigManager.getLevelExp(curLvl + 1)

        if (curExp >= reqExp && ConfigManager.options.getBoolean("auto-level-up")) {
          doLevelUp(player, fromTickLvl = true)
          isLevelUp = true
        }

        player.level = curLvl
        player.exp = (curExp.toFloat() / reqExp.toFloat()).coerceAtMost(1f)
      } else {
        player.level = maxLevel
        player.exp = 1f
      }
    } while (isLevelUp)
  }
  // endregion

  // region api


  // 获取等级方法
  fun getPlayerLevel(player: Player): Int {
    return getLevel(player)
  }

  // 设置等级方法
  fun setPlayerLevel(player: Player, amount: Int) {
    setLevel(player, amount)
    tickLevel(player)
  }

  // 增加等级方法
  fun addPlayerLevel(player: Player, amount: Int) {
    setLevel(player, getLevel(player) + amount)
    tickLevel(player)
  }

  // 移除等级方法
  fun removePlayerLevel(player: Player, amount: Int) {
    setLevel(player, (getLevel(player) - amount).coerceAtLeast(0))
    tickLevel(player)
  }

  // 获取等级方法
  fun getPlayerExp(player: Player): Int {
    return getExp(player)
  }

  // 设置经验方法
  fun setPlayerExp(player: Player, amount: Int) {
    setExp(player, amount)
    tickLevel(player)
  }

  // 增加经验方法
  fun addPlayerExp(player: Player, amount: Int) {
    setExp(player, getExp(player) + amount)
    tickLevel(player)
  }

  // 移除经验方法
  fun removePlayerExp(player: Player, amount: Int) {
    setExp(player, (getExp(player) - amount).coerceAtLeast(0))
    tickLevel(player)
  }

  // 刷新玩家等级方法
  fun refreshPlayerLevel(player: Player) {
    tickLevel(player)
  }

  // 玩家升级方法
  fun playerLevelUP(player: Player) {
    doLevelUp(player)
  }
  // endregion
}
