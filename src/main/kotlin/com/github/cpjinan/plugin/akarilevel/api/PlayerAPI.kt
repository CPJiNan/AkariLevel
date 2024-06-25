package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelAction
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelCondition
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelExp
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupData
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI.getLevelGroupNames
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.common.event.level.PlayerLevelChangeEvent
import com.github.cpjinan.plugin.akarilevel.common.script.kether.KetherUtil.evalKether
import com.github.cpjinan.plugin.akarilevel.internal.database.type.PlayerData
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent
import taboolib.platform.util.sendLang

/**
 * 玩家相关 API
 * @author CPJiNan
 * @since 2024/06/23
 */
object PlayerAPI {
    /**
     * 获取指定玩家某等级组下的数据
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @return 玩家数据
     */
    fun getPlayerData(player: Player, levelGroup: String): PlayerData = getData(player, levelGroup)

    /**
     * 设置指定玩家某等级组下的数据
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param playerData 玩家数据
     */
    fun setPlayerData(player: Player, levelGroup: String, playerData: PlayerData) {
        setData(player, levelGroup, playerData)
    }

    /**
     * 获取指定玩家某等级组下的等级
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @return 等级数值
     */
    fun getPlayerLevel(player: Player, levelGroup: String): Int = getLevel(player, levelGroup)

    /**
     * 获取指定玩家某等级组下的经验
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @return 经验数值
     */
    fun getPlayerExp(player: Player, levelGroup: String): Int = getExp(player, levelGroup)

    /**
     * 设置指定玩家某等级组下的等级并触发该等级升级执行动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 等级数值
     * @param source PlayerLevelChangeEvent 事件来源
     */
    fun setPlayerLevel(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, amount, source)
        runAction(player, levelGroup, amount)
        refreshLevel(player, levelGroup)
    }

    /**
     * 增加指定玩家某等级组下的等级并触发该等级升级执行动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 等级数值
     * @param source PlayerLevelChangeEvent 事件来源
     */
    fun addPlayerLevel(player: Player, levelGroup: String, amount: Int, source: String) {
        val targetLevel = getLevel(player, levelGroup) + amount
        setLevel(player, levelGroup, targetLevel, source)
        runAction(player, levelGroup, targetLevel)
        refreshLevel(player, levelGroup)
    }

    /**
     * 移除指定玩家某等级组下的等级并触发该等级升级执行动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 等级数值
     * @param source PlayerLevelChangeEvent 事件来源
     */
    fun removePlayerLevel(player: Player, levelGroup: String, amount: Int, source: String) {
        val targetLevel = (getLevel(player, levelGroup) - amount).coerceAtLeast(0)
        setLevel(player, levelGroup, targetLevel, source)
        runAction(player, levelGroup, targetLevel)
        refreshLevel(player, levelGroup)
    }

    /**
     * 设置指定玩家某等级组下的等级而不触发该等级升级执行动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 等级数值
     * @param source PlayerLevelChangeEvent 事件来源
     */
    fun setPlayerLevelWithoutAction(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, amount, source)
        refreshLevel(player, levelGroup)
    }

    /**
     * 增加指定玩家某等级组下的等级而不触发该等级升级执行动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 等级数值
     * @param source PlayerLevelChangeEvent 事件来源
     */
    fun addPlayerLevelWithoutAction(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, getLevel(player, levelGroup) + amount, source)
        refreshLevel(player, levelGroup)
    }

    /**
     * 移除指定玩家某等级组下的等级的而不触发该等级升级执行动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 等级数值
     * @param source PlayerLevelChangeEvent 事件来源
     */
    fun removePlayerLevelWithoutAction(player: Player, levelGroup: String, amount: Int, source: String) {
        setLevel(player, levelGroup, (getLevel(player, levelGroup) - amount).coerceAtLeast(0), source)
        refreshLevel(player, levelGroup)
    }

    /**
     * 设置指定玩家某等级组下的经验
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 经验数值
     * @param source PlayerExpChangeEvent 事件来源
     */
    fun setPlayerExp(player: Player, levelGroup: String, amount: Int, source: String) {
        setExp(player, levelGroup, amount, source)
        refreshLevel(player, levelGroup)
    }

    /**
     * 增加指定玩家所有等级组下的经验 (带有等级组是否订阅事件来源检查)
     * @param player 玩家
     * @param amount 经验数值
     * @param source PlayerExpChangeEvent 事件来源
     */
    fun addPlayerExp(player: Player, amount: Int, source: String) {
        LevelAPI.getLevelGroupNames().forEach {
            if (source in getLevelGroupData(it).subscribeSource) {
                setExp(player, it, getExp(player, it) + amount, source)
                refreshLevel(player, it)
            }
        }
    }

    /**
     * 增加指定玩家某等级组下的经验 (带有等级组是否订阅事件来源检查)
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 经验数值
     * @param source PlayerExpChangeEvent 事件来源
     */
    fun addPlayerExp(player: Player, levelGroup: String, amount: Int, source: String) {
        if (source in getLevelGroupData(levelGroup).subscribeSource) {
            setExp(player, levelGroup, getExp(player, levelGroup) + amount, source)
            refreshLevel(player, levelGroup)
        }
    }

    /**
     * 增加指定玩家某等级组下的经验 (跳过等级组是否订阅事件来源检查)
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 经验数值
     * @param source PlayerExpChangeEvent 事件来源
     */
    fun addPlayerExpForce(player: Player, levelGroup: String, amount: Int, source: String) {
        setExp(player, levelGroup, getExp(player, levelGroup) + amount, source)
        refreshLevel(player, levelGroup)
    }

    /**
     * 移除指定玩家某等级组下的经验
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param amount 经验数值
     * @param source PlayerExpChangeEvent 事件来源
     */
    fun removePlayerExp(player: Player, levelGroup: String, amount: Int, source: String) {
        setExp(player, levelGroup, (getExp(player, levelGroup) - amount).coerceAtLeast(0), source)
        refreshLevel(player, levelGroup)
    }

    /**
     * 刷新玩家所有等级组下的等级
     * @param player 玩家
     */
    fun refreshPlayerLevel(player: Player) {
        getLevelGroupNames().forEach {
            refreshLevel(player, it)
        }
    }

    /**
     * 刷新玩家指定等级组下的等级
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     */
    fun refreshPlayerLevel(player: Player, levelGroup: String) {
        refreshLevel(player, levelGroup)
    }

    /**
     * 检查玩家所有等级组是否满足升级条件并尝试升级
     * @param player 玩家
     */
    fun levelupPlayer(player: Player) {
        getLevelGroupNames().forEach {
            levelup(player, it)
        }
    }

    /**
     * 检查玩家指定等级组是否满足升级条件并尝试升级
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     */
    fun levelupPlayer(player: Player, levelGroup: String) {
        levelup(player, levelGroup)
    }

    /**
     * 获取玩家是否满足指定等级组升级条件
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @return 玩家是否满足指定等级组升级条件
     */
    fun checkPlayerLevelupCondition(player: Player, levelGroup: String): Boolean = checkCondition(player, levelGroup)

    /**
     * 为玩家执行指定等级组下的某等级升级动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     * @param level 等级数值
     */
    fun runPlayerLevelAction(player: Player, levelGroup: String, level: Int) {
        runAction(player, levelGroup, level)
    }

    /**
     * 获取玩家正在追踪的等级组
     * @param player 玩家
     * @return 等级组编辑名
     */
    fun getPlayerTraceLevelGroup(player: Player): String = getTraceLvlGroup(player)

    /**
     * 设置玩家正在追踪的等级组
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     */
    fun setPlayerTraceLevelGroup(player: Player, levelGroup: String) {
        setTraceLvlGroup(player, levelGroup)
    }

    /**
     * 检查玩家是否满足指定等级组追踪条件
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     */
    fun checkPlayerTraceCondition(player: Player, levelGroup: String): Boolean = checkTraceCondition(player, levelGroup)

    /**
     * 为玩家执行指定等级组下的追踪动作
     * @param player 玩家
     * @param levelGroup 等级组编辑名
     */
    fun runPlayerTraceAction(player: Player, levelGroup: String) {
        runTraceAction(player, levelGroup)
    }

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

    private fun runAction(player: Player, levelGroup: String, level: Int) {
        getLevelAction(levelGroup, level).evalKether(player)
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
            runAction(player, levelGroup, targetLvl)
            refreshLevel(player, levelGroup)
        } else {
            if (curLvl >= levelGroupData.maxLevel) player.sendLang("Max-Level", levelGroup)
            else player.sendLang("Levelup-Fail", levelGroup)
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
        setTraceLvlGroup(player, getTraceLvlGroup(player))
    }

    private fun getTraceLvlGroup(player: Player): String {
        return DataAPI.getDataValue("Player", player.getDataID(), "Trace").takeIf { it.isNotEmpty() }
            ?: ConfigManager.getDefaultTrace()
    }

    private fun setTraceLvlGroup(player: Player, levelGroup: String) {
        if (levelGroup.isEmpty()) return
        val levelGroupData = getLevelGroupData(levelGroup)
        if (levelGroupData.isEnabledTrace) {
            var matchCondition = true
            levelGroupData.traceCondition.forEach {
                if (!it.evalKether(player).toString().toBoolean()) matchCondition = false
            }
            if (matchCondition) {
                val curLvl = getLevel(player, levelGroup)
                val maxLevel = getLevelGroupData(levelGroup).maxLevel
                if (curLvl < maxLevel) {
                    val curExp = getExp(player, levelGroup)
                    val reqExp = getLevelExp(levelGroup, curLvl + 1)
                    player.level = curLvl
                    if (reqExp != 0) player.exp = (curExp.toFloat() / reqExp.toFloat()).coerceAtMost(1F)
                    else player.exp = 1F
                } else {
                    player.level = maxLevel
                    player.exp = 1F
                }
                DataAPI.setDataValue("Player", player.getDataID(), "Trace", levelGroup)
                levelGroupData.traceAction.evalKether(player)
            } else return
        }
    }

    private fun checkTraceCondition(player: Player, levelGroup: String): Boolean {
        if (levelGroup.isEmpty()) return false
        val levelGroupData = getLevelGroupData(levelGroup)
        if (levelGroupData.isEnabledTrace) {
            var matchCondition = true
            levelGroupData.traceCondition.forEach {
                if (!it.evalKether(player).toString().toBoolean()) matchCondition = false
            }
            return matchCondition
        } else return false
    }

    private fun runTraceAction(player: Player, levelGroup: String) {
        getLevelGroupData(levelGroup).traceAction.evalKether(player)
    }

    private fun getData(player: Player, levelGroup: String): PlayerData = PlayerData(
        Integer.parseInt(
            DataAPI.getDataValue("Player", player.getDataID(), "$levelGroup.Level").takeIf { it.isNotEmpty() }
                ?: "0"),
        Integer.parseInt(
            DataAPI.getDataValue("Player", player.getDataID(), "$levelGroup.Exp").takeIf { it.isNotEmpty() }
                ?: "0")
    )

    private fun setData(player: Player, levelGroup: String, playerData: PlayerData) {
        DataAPI.setDataValue("Player", player.getDataID(), "$levelGroup.Level", playerData.level.toString())
        DataAPI.setDataValue("Player", player.getDataID(), "$levelGroup.Exp", playerData.exp.toString())
    }

    private inline fun <reified T : BukkitProxyEvent> callEvent(event: T, action: T.() -> Unit) {
        event.call()
        if (event.isCancelled) return
        action(event)
    }

    private fun Player.getDataID(): String {
        return this.uniqueId.toString().takeIf { ConfigManager.isEnabledUUID() } ?: this.name
    }
}