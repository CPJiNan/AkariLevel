package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.api.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.api.event.level.PlayerLevelChangeEvent
import com.github.cpjinan.plugin.akarilevel.api.event.level.PlayerLevelupEvent
import com.github.cpjinan.plugin.akarilevel.api.event.level.PlayerRefreshLevelEvent
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.LevelManager
import com.github.cpjinan.plugin.akarilevel.utils.KetherUtil.evalKether
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.type.BukkitProxyEvent
import taboolib.platform.util.sendLang

object AkariLevelAPI {
    // region function
    fun getPlayerLevel(player: Player): Int = getLevel(player)

    fun getPlayerExp(player: Player): Int = getExp(player)

    fun setPlayerLevel(player: Player, amount: Int, source: String) {
        setLevel(player, amount, source)
        tickLevel(player, source)
    }

    fun addPlayerLevel(player: Player, amount: Int, source: String) {
        setLevel(player, getLevel(player) + amount, source)
        tickLevel(player, source)
    }

    fun removePlayerLevel(player: Player, amount: Int, source: String) {
        setLevel(player, (getLevel(player) - amount).coerceAtLeast(0), source)
        tickLevel(player, source)
    }

    fun setPlayerExp(player: Player, amount: Int, source: String) {
        setExp(player, amount, source)
        tickLevel(player, source)
    }

    fun addPlayerExp(player: Player, amount: Int, source: String) {
        setExp(player, getExp(player) + amount, source)
        tickLevel(player, source)
    }

    fun removePlayerExp(player: Player, amount: Int, source: String) {
        setExp(player, (getExp(player) - amount).coerceAtLeast(0), source)
        tickLevel(player, source)
    }

    fun refreshPlayerLevel(player: Player, source: String) {
        tickLevel(player, source)
    }

    fun playerLevelUP(player: Player, source: String) {
        doLevelUp(player, source)
    }

    // region basic function
    private fun getLevel(player: Player): Int {
        return DatabaseManager.getDatabase().getPlayerByName(player.name).level
    }

    private fun getExp(player: Player): Int {
        return DatabaseManager.getDatabase().getPlayerByName(player.name).exp
    }

    private fun setLevel(player: Player, level: Int, source: String) {
        callEvent(PlayerLevelChangeEvent(player, level, source)) {
            val db = DatabaseManager.getDatabase()
            val data = db.getPlayerByName(player.name)
            data.level = this.level
            db.updatePlayer(player.name, data)
            db.save()
            KetherShell.eval(
                LevelManager.getAction(this.level)!!,
                ScriptOptions.builder().namespace(listOf(AkariLevel.instance.name)).sender(sender = adaptPlayer(player))
                    .build()
            )
        }
    }

    private fun setExp(player: Player, exp: Int, source: String) {
        callEvent(PlayerExpChangeEvent(player, exp, source)) {
            val db = DatabaseManager.getDatabase()
            val data = db.getPlayerByName(player.name)
            data.exp = this.exp
            db.updatePlayer(player.name, data)
            db.save()
        }
    }

    private fun doLevelUp(player: Player, source: String) {
        val curLvl = getLevel(player)
        callEvent(PlayerLevelupEvent(player, source)) {
            if (curLvl < ConfigManager.getMaxLevel()) {
                val curExp = getExp(player)
                val targetLvl = curLvl + 1
                val reqExp = LevelManager.getExp(targetLvl)
                LevelManager.getCondition(targetLvl)?.forEach {
                    if (!it.evalKether(player).toString().toBoolean()) return
                }
                if (curExp >= reqExp) {
                    setExp(player, curExp - reqExp, "PLAYER_LEVELUP")
                    setLevel(player, targetLvl, "PLAYER_LEVELUP")
                    player.sendLang("Levelup-Success")
                    if (source != "PLAYER_REFRESH_LEVEL") {
                        tickLevel(player, "PLAYER_LEVELUP")
                    }
                } else {
                    player.sendLang("Levelup-Fail")
                }
            } else {
                player.sendLang("Max-Level")
            }
        }
    }

    private fun tickLevel(player: Player, source: String) {
        callEvent(PlayerRefreshLevelEvent(player, source)) {
            var isLevelUp: Boolean
            do {
                isLevelUp = false
                val curLvl = getLevel(player)
                val maxLevel = ConfigManager.getMaxLevel()
                if (curLvl < maxLevel) {
                    val curExp = getExp(player)
                    val reqExp = LevelManager.getExp(curLvl + 1)

                    if (curExp >= reqExp && ConfigManager.settings.getBoolean("Level.Auto-Levelup")) {
                        doLevelUp(player, source = "PLAYER_REFRESH_LEVEL")
                        isLevelUp = true
                    }

                    if (ConfigManager.settings.getBoolean("Level.Vanilla-Exp-Bar")) {
                        player.level = curLvl
                        player.exp = (curExp.toFloat() / reqExp.toFloat()).coerceAtMost(1f)
                    }
                } else {
                    if (ConfigManager.settings.getBoolean("Level.Vanilla-Exp-Bar")) {
                        player.level = maxLevel
                        player.exp = 1f
                    }
                    if (ConfigManager.settings.getBoolean("Level.Exp-Limit")) setExp(player, 0, "PLAYER_REFRESH_LEVEL")
                }
            } while (isLevelUp)
        }
    }

    private inline fun <reified T : BukkitProxyEvent> callEvent(event: T, action: T.() -> Unit) {
        event.call()
        if (event.isCancelled) return
        action(event)
    }
}