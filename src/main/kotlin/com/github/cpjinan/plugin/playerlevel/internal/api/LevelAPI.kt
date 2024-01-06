package com.github.cpjinan.plugin.playerlevel.internal.api

import com.github.cpjinan.plugin.playerlevel.internal.events.SyncLevelUpEvent
import com.github.cpjinan.plugin.playerlevel.internal.events.SyncSetExpEvent
import com.github.cpjinan.plugin.playerlevel.internal.events.SyncSetLevelEvent
import com.github.cpjinan.plugin.playerlevel.internal.events.SyncTickLevelEvent
import com.github.cpjinan.plugin.playerlevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.playerlevel.internal.manager.RegisterManager
import org.bukkit.Bukkit
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
    private fun getLevel(player: Player): Int = RegisterManager.getDatabase().getPlayerByName(player.name).level

    /**
     * 设置等级
     * @param [player] 玩家
     * @param [level] 等级
     */
    private fun setLevel(player: Player, level: Int) {
        val syncSetLevelEvent = SyncSetLevelEvent(player, level)

        Bukkit.getPluginManager().callEvent(syncSetLevelEvent)

        if (syncSetLevelEvent.isCancelled) {
            return
        }

        val level = syncSetLevelEvent.level

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
     */
    private fun setExp(player: Player, exp: Int) {
        val syncSetExpEvent = SyncSetExpEvent(player, exp)

        Bukkit.getPluginManager().callEvent(syncSetExpEvent)

        if (syncSetExpEvent.isCancelled) {
            return
        }

        val exp = syncSetExpEvent.exp

        val db = RegisterManager.getDatabase()
        val data = db.getPlayerByName(player.name)
        data.exp = exp
        db.updatePlayer(player.name, data)
    }

    /**
     * 花费经验值进行升级
     * @param [player] 玩家
     * @param [fromTickLvl]
     */
    private fun doLevelUp(player: Player, fromTickLvl: Boolean = false) {
        val curLvl = getLevel(player)

        val syncLevelUpEvent = SyncLevelUpEvent(
            player, fromTickLvl
        )

        Bukkit.getPluginManager().callEvent(syncLevelUpEvent)

        if (syncLevelUpEvent.isCancelled) {
            return
        }

        val fromTickLvl = syncLevelUpEvent.fromTickLvl

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

    /**
     * 刷新等级
     * @param [player] 玩家
     */
    private fun tickLevel(player: Player) {
        val syncTickLevelEvent = SyncTickLevelEvent(
            player
        )

        Bukkit.getPluginManager().callEvent(syncTickLevelEvent)

        if (syncTickLevelEvent.isCancelled) {
            return
        }

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
     * @param [amount]
     */
    fun setPlayerLevel(player: Player, amount: Int) {
        setLevel(player, amount)
        tickLevel(player)
    }

    /**
     * 增加玩家等级
     * @param [player] 玩家
     * @param [amount]
     */
    fun addPlayerLevel(player: Player, amount: Int) {
        setLevel(player, getLevel(player) + amount)
        tickLevel(player)
    }

    /**
     * 移除玩家等级
     * @param [player] 玩家
     * @param [amount] 量
     */
    fun removePlayerLevel(player: Player, amount: Int) {
        setLevel(player, (getLevel(player) - amount).coerceAtLeast(0))
        tickLevel(player)
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
     * @param [amount]
     */
    fun setPlayerExp(player: Player, amount: Int) {
        setExp(player, amount)
        tickLevel(player)
    }

    /**
     * 增加玩家经验
     * @param [player] 玩家
     * @param [amount]
     */
    fun addPlayerExp(player: Player, amount: Int) {
        setExp(player, getExp(player) + amount)
        tickLevel(player)
    }

    /**
     * 移除玩家经验
     * @param [player] 玩家
     * @param [amount]
     */
    fun removePlayerExp(player: Player, amount: Int) {
        setExp(player, (getExp(player) - amount).coerceAtLeast(0))
        tickLevel(player)
    }

    /**
     * 刷新玩家等级
     * @param [player] 玩家
     */
    fun refreshPlayerLevel(player: Player) {
        tickLevel(player)
    }

    /**
     * 玩家升级方法
     * @param [player] 玩家
     */
    fun playerLevelUP(player: Player) {
        doLevelUp(player)
    }
    // endregion
}
