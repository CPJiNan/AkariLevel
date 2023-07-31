package com.github.cpjinan.manager

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.KetherShell.eval
import taboolib.module.kether.ScriptOptions

object LevelManager {

    // 刷新玩家等级方法
    fun refreshPlayerLevel(player: Player){
        val level: Int = ConfigManager.player.getInt("${player.name}.level")
        if( level < (ConfigManager.level.getString("max-level")!!.toInt()) ){
            val exp: Int = ConfigManager.player.getInt("${player.name}.exp")
            val expToLevel: Int = ConfigManager.level.getString("${level + 1}.exp")?.toInt() ?: 0

            player.level = level

            if (exp >= expToLevel) player.exp = 1.toFloat()
            else player.exp = (exp.toFloat() / expToLevel.toFloat())
        } else {
            player.level = ConfigManager.level.getString("max-level")!!.toInt()
            player.exp = 1.toFloat()
        }
    }

    // 执行等级动作方法
    fun runLevelAction(player: Player){
        eval(ConfigManager.level.getStringList("${ConfigManager.player.getInt("${player.name}.level")}.action"), ScriptOptions.builder().namespace(emptyList()).sender(sender = adaptPlayer(player)).build())
    }

}