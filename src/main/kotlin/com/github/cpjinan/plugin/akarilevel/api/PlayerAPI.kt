package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelAction
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelCondition
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelExp
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupData
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupNames
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelChangeEvent
import com.github.cpjinan.plugin.akarilevel.common.script.kether.KetherUtil.evalKether
import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.compileJS
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.type.BukkitProxyEvent
import taboolib.platform.util.sendLang

object PlayerAPI {
    // region function
    fun getPlayerData(player: Player, levelGroup: String): PlayerData = getData(player, levelGroup)

    fun setPlayerData(player: Player, levelGroup: String, playerData: PlayerData) {
        setData(player, levelGroup, playerData)
    }

    fun getPlayerLevel(player: Player, levelGroup: String): Int = getLevel(player, levelGroup)

    fun getPlayerExp(player: Player, levelGroup: String): Int = getExp(player, levelGroup)

    fun setPlayerLevel(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, amount, source)
        runAction(player, levelGroup, amount)
        refreshLevel(player, levelGroup)
    }

    fun addPlayerLevel(player: Player, levelGroup: String, amount: Int, source: String) {
        val targetLevel = getLevel(player, levelGroup) + amount
        setLevel(player, levelGroup, targetLevel, source)
        runAction(player, levelGroup, targetLevel)
        refreshLevel(player, levelGroup)
    }

    fun removePlayerLevel(player: Player, levelGroup: String, amount: Int, source: String) {
        val targetLevel = (getLevel(player, levelGroup) - amount).coerceAtLeast(0)
        setLevel(player, levelGroup, targetLevel, source)
        runAction(player, levelGroup, targetLevel)
        refreshLevel(player, levelGroup)
    }

    fun setPlayerLevelWithoutAction(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, amount, source)
        refreshLevel(player, levelGroup)
    }

    fun addPlayerLevelWithoutAction(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, getLevel(player, levelGroup) + amount, source)
        refreshLevel(player, levelGroup)
    }

    fun removePlayerLevelWithoutAction(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, (getLevel(player, levelGroup) - amount).coerceAtLeast(0), source)
        refreshLevel(player, levelGroup)
    }

    fun setPlayerExp(player: Player, levelGroup: String, amount: Int, source: String) {
        setExp(player, levelGroup, amount, source)
        refreshLevel(player, source)
    }

    fun addPlayerExp(player: Player, levelGroup: String, amount: Int, source: String) {
        val levelGroupData = getLevelGroupData(levelGroup)
        val subscribeSource = levelGroupData.subscribeSource
        var sourceFormula = levelGroupData.sourceFormula

        if (source in subscribeSource) {
            subscribeSource.forEach {
                sourceFormula = sourceFormula.replace("%$it%", if (it == source) amount.toString() else "0")
            }
            sourceFormula = sourceFormula.compileJS()?.eval().toString()
            setExp(player, levelGroup, getExp(player, levelGroup) + sourceFormula.toInt(), source)
            refreshLevel(player, source)
        }
    }

    fun addPlayerExpWithoutFormula(player: Player, levelGroup: String, amount: Int, source: String) {
        setExp(player, levelGroup, getExp(player, levelGroup) + amount, source)
        refreshLevel(player, source)
    }

    fun removePlayerExp(player: Player, levelGroup: String, amount: Int, source: String) {
        setExp(player, levelGroup, (getExp(player, levelGroup) - amount).coerceAtLeast(0), source)
        refreshLevel(player, levelGroup)
    }

    fun refreshPlayerLevel(player: Player) {
        getLevelGroupNames().forEach {
            refreshLevel(player, it)
        }
    }

    fun refreshPlayerLevel(player: Player, levelGroup: String) {
        refreshLevel(player, levelGroup)
    }

    fun checkPlayerLevelupCondition(player: Player, levelGroup: String): Boolean = checkCondition(player, levelGroup)

    fun runLevelAction(player: Player, levelGroup: String, level: Int) {
        runAction(player, levelGroup, level)
    }

    // region basic function
    private fun getLevel(player: Player, levelGroup: String): Int {
        return getData(player, levelGroup).level
    }

    private fun getExp(player: Player, levelGroup: String): Int {
        return getData(player, levelGroup).exp
    }

    private fun setLevel(player: Player, levelGroup: String, level: Int, source: String) {
        val oldLvl = getLevel(player, levelGroup)
        callEvent(PlayerLevelChangeEvent(player, levelGroup, oldLvl, level, source)) {
            val data = getData(this.player, this.levelGroup)
            data.level = this.newLevel
            setData(this.player, this.levelGroup, data)
        }
    }

    private fun setExp(player: Player, levelGroup: String, exp: Int, source: String) {
        val oldExp = getExp(player, levelGroup)
        val expAmount = exp - oldExp
        callEvent(PlayerExpChangeEvent(player, levelGroup, expAmount, source)) {
            val data = getData(this.player, this.levelGroup)
            data.exp = oldExp + this.expAmount
            setData(this.player, this.levelGroup, data)
        }
    }

    private fun checkCondition(player: Player, levelGroup: String): Boolean {
        var matchCondition = true
        val curLvl = getLevel(player, levelGroup)
        val maxLevel = getLevelGroupData(levelGroup).maxLevel
        if (curLvl < maxLevel) {
            getLevelCondition(levelGroup, curLvl + 1).forEach {
                if (!it.evalKether(player).toString().toBoolean()) matchCondition = false
            }
            val curExp = getExp(player, levelGroup)
            val reqExp = getLevelExp(levelGroup, curLvl + 1)
            if (curExp < reqExp) matchCondition = false
        } else matchCondition = false
        return matchCondition
    }

    private fun levelup(player: Player, levelGroup: String) {
        val levelGroupData = getLevelGroupData(levelGroup)
        val curLvl = getLevel(player, levelGroup)
        if (checkCondition(player, levelGroup)) {
            val curExp = getExp(player, levelGroup)
            val targetLvl = curLvl + 1
            val reqExp = getLevelExp(levelGroup, targetLvl)
            setExp(player, levelGroup, curExp - reqExp, "PLAYER_LEVELUP")
            setLevel(player, levelGroup, targetLvl, "PLAYER_LEVELUP")
            player.sendLang("Levelup-Success")
            refreshLevel(player, levelGroup)
        } else {
            if (curLvl >= levelGroupData.maxLevel) player.sendLang("Max-Level")
            else player.sendLang("Levelup-Fail")
        }
    }

    private fun refreshLevel(player: Player, levelGroup: String) {
        var isLevelUp: Boolean
        val levelGroupData = getLevelGroupData(levelGroup)
        do {
            isLevelUp = false
            val curLvl = getLevel(player, levelGroup)
            val maxLevel = levelGroupData.maxLevel
            if (checkCondition(player, levelGroup)) {
                if (levelGroupData.isEnabledAutoLevelup) {
                    levelup(player, levelGroup)
                    isLevelUp = true
                }
            } else if (curLvl >= maxLevel) {
                if (levelGroupData.isEnabledExpLimit) setExp(player, levelGroup, 0, "EXP_LIMIT")
            }
        } while (isLevelUp)
    }

    private fun runAction(player: Player, levelGroup: String, level: Int) {
        KetherShell.eval(
            getLevelAction(levelGroup, level),
            ScriptOptions.builder().namespace(listOf(AkariLevel.plugin.name)).sender(sender = adaptPlayer(player))
                .build()
        )
    }

    private fun getData(player: Player, levelGroup: String): PlayerData = PlayerData(
        Integer.parseInt(DataAPI.getDataValue("Player", player.name, "$levelGroup.Level").takeIf { it.isNotEmpty() }
            ?: "0"),
        Integer.parseInt(DataAPI.getDataValue("Player", player.name, "$levelGroup.Exp").takeIf { it.isNotEmpty() }
            ?: "0")
    )

    private fun setData(player: Player, levelGroup: String, playerData: PlayerData) {
        DataAPI.setDataValue("Player", player.name, "$levelGroup.Level", playerData.level.toString())
        DataAPI.setDataValue("Player", player.name, "$levelGroup.Exp", playerData.exp.toString())
    }

    private inline fun <reified T : BukkitProxyEvent> callEvent(event: T, action: T.() -> Unit) {
        event.call()
        if (event.isCancelled) return
        action(event)
    }
}