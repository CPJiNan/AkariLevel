package com.github.cpjinan.plugin.akarilevel.hook  
  
import com.github.cpjinan.plugin.akarilevel.level.LevelGroup  
import org.bukkit.OfflinePlayer  
import taboolib.common.platform.function.console  
import taboolib.common5.util.createBar  
import taboolib.module.chat.colored  
import taboolib.module.lang.asLangText  
import taboolib.platform.compat.PlaceholderExpansion  
  
/**  
 * AkariLevel  
 * com.github.cpjinan.plugin.akarilevel.hook  
 *  
 * PlaceholderAPI 挂钩。  
 *  
 * @author 季楠  
 * @since 2025/8/17 20:07  
 */  
object PlaceholderAPIHook : PlaceholderExpansion {  
    /**  
     * 变量前缀。  
     */  
    override val identifier = "akarilevel"  
      
    /**  
     * 用于检测递归的 ThreadLocal 标志。  
     */  
    private val isCalculating = ThreadLocal.withInitial { false }  
  
    /**  
     * 离线玩家请求变量事件。  
     */  
    override fun onPlaceholderRequest(player: OfflinePlayer?, args: String): String {  
        val argsList = args.split("_")  
        val notAvailable = console().asLangText("PlaceholderNotAvailable")  
        if (player == null) return notAvailable  
          
        val playerName = player.name  
        val levelGroup = LevelGroup.getLevelGroups()[argsList[0]]  
        if (levelGroup == null) return notAvailable  
          
        // 检测是否已在计算中以防止递归
        if (isCalculating.get()) {  
            val currentLevel = levelGroup.getMemberLevel(playerName)  
            return when (argsList[1].lowercase()) {  
                // 等级组名称。  
                "name" -> levelGroup.name  
                "display" -> levelGroup.display.colored()  
                  
                // 等级。  
                "level" -> currentLevel  
                "lastlevel" -> currentLevel - 1  
                "nextlevel" -> currentLevel + 1  
                "minlevel" -> levelGroup.getMinLevel()  
                "maxlevel" -> levelGroup.getMaxLevel()  
                  
                // 经验。  
                "exp" -> levelGroup.getMemberExp(playerName)  
                  
                // 等级名称。  
                "levelname" -> levelGroup.getLevelName(currentLevel).colored()  
                "lastlevelname" -> levelGroup.getLevelName(currentLevel - 1).colored()  
                "nextlevelname" -> levelGroup.getLevelName(currentLevel + 1).colored()  
                  
                // 升级所需经验。  
                "levelexp" -> levelGroup.getLevelExp(currentLevel - 1, currentLevel)  
                "lastlevelexp" -> levelGroup.getLevelExp(currentLevel - 2, currentLevel - 1)  
                "nextlevelexp" -> levelGroup.getLevelExp(currentLevel, currentLevel + 1)  
                  
                "levelexpfrom" -> {  
                    val oldLevel = argsList[2].toLongOrNull() ?: return notAvailable  
                    if (oldLevel > currentLevel) return notAvailable  
                    levelGroup.getLevelExp(oldLevel, currentLevel)  
                }  
                  
                "levelexpto" -> {  
                    val newLevel = argsList[2].toLongOrNull() ?: return notAvailable  
                    if (newLevel < currentLevel) return notAvailable  
                    levelGroup.getLevelExp(currentLevel, newLevel)  
                }  
                  
                "levelexpfromto" -> {  
                    val oldLevel = argsList[2].toLongOrNull() ?: return notAvailable  
                    val newLevel = argsList[3].toLongOrNull() ?: return notAvailable  
                    if (oldLevel > newLevel) return notAvailable  
                    levelGroup.getLevelExp(oldLevel, newLevel)  
                }  
                  
                // 升级进度百分比。  
                "levelprogresspercent" -> {  
                    (currentLevel.toDouble() / levelGroup.getMaxLevel() * 100).coerceIn(0.0, 100.0).toLong()  
                }  
                  
                "expprogresspercent" -> {  
                    val currentExp = levelGroup.getMemberExp(playerName)  
                    val nextLevelExp = levelGroup.getLevelExp(currentLevel, currentLevel + 1)  
                    (currentExp.toDouble() / nextLevelExp * 100).coerceIn(0.0, 100.0).toLong()  
                }  
                  
                // 升级进度条。  
                "levelprogressbar", "expprogressbar" -> ""  
                  
                else -> notAvailable  
            }.toString()  
        }  
          
        // 标记正在计算中  
        isCalculating.set(true)  
        try {  
            val currentLevel = levelGroup.getMemberLevel(playerName)  
            val lastLevel = currentLevel - 1  
            val nextLevel = currentLevel + 1  
            val minLevel = levelGroup.getMinLevel()  
            val maxLevel = levelGroup.getMaxLevel()  
            val currentExp = levelGroup.getMemberExp(playerName)  
            val nextLevelExp = levelGroup.getLevelExp(playerName, currentLevel, nextLevel)  
  
            return when (argsList[1].lowercase()) {  
                // 等级组名称。  
                "name" -> levelGroup.name  
                "display" -> levelGroup.display.colored()  
  
                // 等级。  
                "level" -> currentLevel  
                "lastlevel" -> lastLevel  
                "nextlevel" -> nextLevel  
                "minlevel" -> minLevel  
                "maxlevel" -> maxLevel  
  
                // 经验。  
                "exp" -> currentExp  
  
                // 等级名称。  
                "levelname" -> levelGroup.getLevelName(playerName, currentLevel).colored()  
                "lastlevelname" -> levelGroup.getLevelName(playerName, lastLevel).colored()  
                "nextlevelname" -> levelGroup.getLevelName(playerName, nextLevel).colored()  
  
                // 升级所需经验。  
                "levelexp" -> levelGroup.getLevelExp(playerName, lastLevel, currentLevel)  
                "lastlevelexp" -> levelGroup.getLevelExp(playerName, currentLevel - 2, lastLevel)  
                "nextlevelexp" -> nextLevelExp  
  
                "levelexpfrom" -> {  
                    val oldLevel = argsList[2].toLongOrNull() ?: return notAvailable  
                    if (oldLevel > currentLevel) return notAvailable  
                    levelGroup.getLevelExp(playerName, oldLevel, currentLevel)  
                }  
  
                "levelexpto" -> {  
                    val newLevel = argsList[2].toLongOrNull() ?: return notAvailable  
                    if (newLevel < currentLevel) return notAvailable  
                    levelGroup.getLevelExp(playerName, currentLevel, newLevel)  
                }  
  
                "levelexpfromto" -> {  
                    val oldLevel = argsList[2].toLongOrNull() ?: return notAvailable  
                    val newLevel = argsList[3].toLongOrNull() ?: return notAvailable  
                    if (oldLevel > newLevel) return notAvailable  
                    levelGroup.getLevelExp(playerName, oldLevel, newLevel)  
                }  
  
                // 升级进度百分比。  
                "levelprogresspercent" -> {  
                    (currentLevel.toDouble() / maxLevel * 100).coerceIn(0.0, 100.0).toLong()  
                }  
  
                "expprogresspercent" -> {  
                    (currentExp.toDouble() / nextLevelExp * 100).coerceIn(0.0, 100.0).toLong()  
                }  
  
                // 升级进度条。  
                "levelprogressbar" -> {  
                    createBar(  
                        argsList[2].colored(),  
                        argsList[3].colored(),  
                        argsList[4].toInt(),  
                        (currentLevel.toDouble() / maxLevel).coerceIn(0.0, 1.0)  
                    )  
                }  
  
                "expprogressbar" -> createBar(  
                    argsList[2].colored(),  
                    argsList[3].colored(),  
                    argsList[4].toInt(),  
                    (currentExp.toDouble() / nextLevelExp).coerceIn(0.0, 1.0)  
                )  
  
                else -> notAvailable  
            }.toString()  
        } finally {  
            // 始终清理标志  
            isCalculating.set(false)  
        }  
    }  
}