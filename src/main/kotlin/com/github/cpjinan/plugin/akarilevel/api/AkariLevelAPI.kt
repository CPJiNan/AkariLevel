package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelChangeEvent
import com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelupEvent
import com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerRefreshLevelEvent
import com.github.cpjinan.plugin.akarilevel.common.script.kether.KetherUtil.evalKether
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.LevelManager
import com.github.cpjinan.plugin.akarilevel.utils.DebugUtil
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
            KetherShell.eval(
                LevelManager.getAction(this.level)!!,
                ScriptOptions.builder().namespace(listOf(AkariLevel.plugin.name)).sender(sender = adaptPlayer(player))
                    .build()
            )
            DebugUtil.printArgs(
                Pair("(BukkitProxyEvent) this", "PlayerLevelChangeEvent"),
                Pair("(String) player.name", player.name),
                Pair("(Int) data.level", data.level),
                Pair("(Int) this.level", this.level),
                Pair("(String) this.source", this.source)
            )
        }
    }

    private fun setExp(player: Player, exp: Int, source: String) {
        callEvent(PlayerExpChangeEvent(player, exp, source)) {
            val db = DatabaseManager.getDatabase()
            val data = db.getPlayerByName(player.name)
            data.exp = this.exp
            db.updatePlayer(player.name, data)
            DebugUtil.printArgs(
                Pair("(BukkitProxyEvent) this", "PlayerExpChangeEvent"),
                Pair("(String) player.name", player.name),
                Pair("(Int) data.exp", data.exp),
                Pair("(Int) this.exp", this.exp),
                Pair("(String) this.source", this.source)
            )
        }
    }

    private fun doLevelUp(player: Player, source: String) {
        val curLvl = getLevel(player)
        callEvent(PlayerLevelupEvent(player, source)) {
            if (curLvl < ConfigManager.getMaxLevel()) {
                val curExp = getExp(player)
                val targetLvl = curLvl + 1
                val reqExp = LevelManager.getExp(targetLvl)
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
                DebugUtil.printArgs(
                    Pair("(BukkitProxyEvent) this", "PlayerLevelupEvent"),
                    Pair("(String) player.name", player.name),
                    Pair("(String) this.source", this.source),
                    Pair("(Boolean) curLvl < ConfigManager.getMaxLevel()", curLvl < ConfigManager.getMaxLevel()),
                    Pair("(Int) curExp", curExp),
                    Pair("(Int) targetLvl", targetLvl),
                    Pair("(Int) reqExp", reqExp)
                )
            } else {
                player.sendLang("Max-Level")
                DebugUtil.printArgs(
                    Pair("(BukkitProxyEvent) this", "PlayerLevelupEvent"),
                    Pair("(String) player.name", player.name),
                    Pair("(String) this.source", this.source),
                    Pair("(Boolean) curLvl < ConfigManager.getMaxLevel()", curLvl < ConfigManager.getMaxLevel())
                )
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
                var matchCondition = true
                LevelManager.getCondition(curLvl + 1)?.forEach {
                    if (!it.evalKether(player).toString().toBoolean()) matchCondition = false
                }
                if (curLvl < maxLevel) {
                    val curExp = getExp(player)
                    val reqExp = LevelManager.getExp(curLvl + 1)
                    if (curExp >= reqExp && matchCondition && ConfigManager.settings.getBoolean("Level.Auto-Levelup")) {
                        doLevelUp(player, source = "PLAYER_REFRESH_LEVEL")
                        isLevelUp = true
                    }
                    if (ConfigManager.settings.getBoolean("Level.Vanilla-Exp-Bar")) {
                        player.level = curLvl
                        if (reqExp != 0) player.exp = (curExp.toFloat() / reqExp.toFloat()).coerceAtMost(1F)
                        else player.exp = 1F
                    }
                    DebugUtil.printArgs(
                        Pair("(BukkitProxyEvent) this", "PlayerRefreshLevelEvent"),
                        Pair("(String) player.name", player.name),
                        Pair("(String) this.source", this.source),
                        Pair("(Int) curLvl", curLvl),
                        Pair("(Int) maxLevel", maxLevel),
                        Pair("(Int) curExp", curExp),
                        Pair("(Int) reqExp", reqExp),
                        Pair(
                            "(Boolean) curExp >= reqExp && ConfigManager.settings.getBoolean(\"Level.Auto-Levelup\")",
                            curExp >= reqExp && ConfigManager.settings.getBoolean("Level.Auto-Levelup")
                        ),
                        Pair(
                            "(Boolean) ConfigManager.settings.getBoolean(\"Level.Vanilla-Exp-Bar\")",
                            ConfigManager.settings.getBoolean("Level.Vanilla-Exp-Bar")
                        ),
                        Pair(
                            "(float) (curExp.toFloat() / reqExp.toFloat()).coerceAtMost(1f)",
                            if (reqExp != 0) (curExp.toFloat() / reqExp.toFloat()).coerceAtMost(1F)
                            else 1F
                        )
                    )
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