package com.github.cpjinan.plugin.playerlevel.internal.api

import com.github.cpjinan.plugin.playerlevel.internal.events.exp.SetExpEvent
import com.github.cpjinan.plugin.playerlevel.internal.events.level.LevelUpEvent
import com.github.cpjinan.plugin.playerlevel.internal.events.level.SetLevelEvent
import com.github.cpjinan.plugin.playerlevel.internal.events.level.TickLevelEvent
import com.github.cpjinan.plugin.playerlevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.playerlevel.internal.manager.RegisterManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.util.sendLang

/**
 * 等级应用程序接口
 * @author CPJiNan
 * @date 2024/01/06
 */
object LevelAPI {
    // region basic function
    /**
     * 获取等级
     * @param [player] 玩家
     * @return [Int]
     */
    private fun getLevel(player: Player): Int {
        return RegisterManager.getDatabase().getPlayerByName(player.name).level
    }

    /**
     * 设置等级
     * @param [player] 玩家
     * @param [level] 等级
     * @param [source] 来源
     */
    private fun setLevel(player: Player, level: Int, source: String = "DEFAULT") {
        val setLevelEvent = SetLevelEvent(player, level, source)
        setLevelEvent.call()
        if (setLevelEvent.isCancelled) return
        val level = setLevelEvent.level
        val source = setLevelEvent.source

        val db = RegisterManager.getDatabase()
        val data = db.getPlayerByName(player.name)
        data.level = level
        db.updatePlayer(player.name, data)
        KetherShell.eval(
            ConfigManager.getLevelChangeAction(level),
            ScriptOptions.builder().namespace(emptyList()).sender(sender = adaptPlayer(player)).build()
        )
    }

    /**
     * 获取经验
     * @param [player] 玩家
     * @return [Int]
     */
    private fun getExp(player: Player): Int = RegisterManager.getDatabase().getPlayerByName(player.name).exp

    /**
     * 设置经验
     * @param [player] 玩家
     * @param [exp] 经验
     * @param [source] 来源
     */
    private fun setExp(player: Player, exp: Int, source: String = "DEFAULT") {
        val setExpEvent = SetExpEvent(player, exp, source)
        setExpEvent.call()
        if (setExpEvent.isCancelled) return
        val exp = setExpEvent.exp
        val source = setExpEvent.source

        val db = RegisterManager.getDatabase()
        val data = db.getPlayerByName(player.name)
        data.exp = exp
        db.updatePlayer(player.name, data)
    }

    /**
     * 花费经验值进行升级
     * @param [player] 玩家
     * @param [source] 来源
     */
    private fun doLevelUp(player: Player, source: String = "DEFAULT") {
        val curLvl = getLevel(player)

        val levelUpEvent = LevelUpEvent(
            player, source
        )
        levelUpEvent.call()
        if (levelUpEvent.isCancelled) return
        val source = levelUpEvent.source

        if (curLvl < ConfigManager.getMaxLevel()) {
            val curExp = getExp(player)
            val targetLvl = curLvl + 1
            val reqExp = ConfigManager.getLevelExp(targetLvl)
            if (curExp >= reqExp) {
                setExp(player, curExp - reqExp)
                setLevel(player, targetLvl)
                player.sendLang("level-up-success")

                if (source != "TICK_LEVEL_API") {
                    tickLevel(player)
                }
            } else {
                player.sendLang("level-up-fail")
            }
        } else {
            player.sendLang("max-level")
        }
    }

    /**
     * 刷新等级
     * @param [player] 玩家
     * @param [source] 来源
     */
    private fun tickLevel(player: Player, source: String = "DEFAULT") {
        val tickLevelEvent = TickLevelEvent(
            player, source
        )
        tickLevelEvent.call()
        if (tickLevelEvent.isCancelled) return
        val source = tickLevelEvent.source

        var isLevelUp: Boolean
        do {
            isLevelUp = false

            val curLvl = getLevel(player)
            val maxLevel = ConfigManager.getMaxLevel()
            if (curLvl < maxLevel) {
                val curExp = getExp(player)
                val reqExp = ConfigManager.getLevelExp(curLvl + 1)

                if (curExp >= reqExp && ConfigManager.options.getBoolean("auto-level-up")) {
                    doLevelUp(player, source = "TICK_LEVEL_API")
                    isLevelUp = true
                }

                if (ConfigManager.options.getBoolean("exp-bar")) {
                    player.level = curLvl
                    player.exp = (curExp.toFloat() / reqExp.toFloat()).coerceAtMost(1f)
                }
            } else {
                if (ConfigManager.options.getBoolean("exp-bar")) {
                    player.level = maxLevel
                    player.exp = 1f
                }
                if (ConfigManager.options.getBoolean("exp-limit")) setExp(player, 0)
            }
        } while (isLevelUp)
    }
    // endregion

    // region api
    /**
     * 获取玩家等级
     * @param [player] 玩家
     * @return [Int]
     */
    fun getPlayerLevel(player: Player): Int {
        return getLevel(player)
    }


    /**
     * 设置玩家等级
     * @param [player] 玩家
     * @param [source] 来源
     * @param [amount]
     */
    fun setPlayerLevel(player: Player, amount: Int, source: String = "DEFAULT") {
        setLevel(player, amount, source)
        tickLevel(player, source)
    }

    /**
     * 增加玩家等级
     * @param [player] 玩家
     * @param [source] 来源
     * @param [amount]
     */
    fun addPlayerLevel(player: Player, amount: Int, source: String = "DEFAULT") {
        setLevel(player, getLevel(player) + amount, source)
        tickLevel(player, source)
    }

    /**
     * 移除玩家等级
     * @param [player] 玩家
     * @param [source] 来源
     * @param [amount]
     */
    fun removePlayerLevel(player: Player, amount: Int, source: String = "DEFAULT") {
        setLevel(player, (getLevel(player) - amount).coerceAtLeast(0), source)
        tickLevel(player, source)
    }

    /**
     * 获取玩家经验
     * @param [player] 玩家
     * @return [Int]
     */
    fun getPlayerExp(player: Player): Int {
        return getExp(player)
    }

    /**
     * 设置玩家经验
     * @param [player] 玩家
     * @param [source] 来源
     * @param [amount]
     */
    fun setPlayerExp(player: Player, amount: Int, source: String = "DEFAULT") {
        setExp(player, amount, source)
        tickLevel(player, source)
    }

    /**
     * 增加玩家经验
     * @param [player] 玩家
     * @param [source] 来源
     * @param [amount]
     */
    fun addPlayerExp(player: Player, amount: Int, source: String = "DEFAULT") {
        setExp(player, getExp(player) + amount, source)
        tickLevel(player, source)
    }

    /**
     * 移除玩家经验
     * @param [player] 玩家
     * @param [source] 来源
     * @param [amount]
     */
    fun removePlayerExp(player: Player, amount: Int, source: String = "DEFAULT") {
        setExp(player, (getExp(player) - amount).coerceAtLeast(0), source)
        tickLevel(player, source)
    }

    /**
     * 刷新玩家等级
     * @param [player] 玩家
     * @param [source] 来源
     */
    fun refreshPlayerLevel(player: Player, source: String = "DEFAULT") {
        tickLevel(player, source)
    }

    /**
     * 玩家升级方法
     * @param [player] 玩家
     * @param [source] 来源
     */
    fun playerLevelUP(player: Player, source: String = "DEFAULT") {
        doLevelUp(player, source)
    }
    // endregion
}
