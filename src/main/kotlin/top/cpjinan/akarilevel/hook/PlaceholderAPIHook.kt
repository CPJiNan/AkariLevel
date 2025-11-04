package top.cpjinan.akarilevel.hook

import org.bukkit.OfflinePlayer
import taboolib.common.platform.function.console
import taboolib.common5.util.createBar
import taboolib.module.chat.colored
import taboolib.module.lang.asLangText
import taboolib.platform.compat.PlaceholderExpansion
import top.cpjinan.akarilevel.level.LevelGroup

/**
 * AkariLevel
 * top.cpjinan.akarilevel.hook
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
     * 是否递归。
     */
    private val isProcessing = ThreadLocal.withInitial { false }

    /**
     * 离线玩家请求变量事件。
     */
    override fun onPlaceholderRequest(player: OfflinePlayer?, args: String): String {
        val argsList = args.split("_")
        val notAvailable = console().asLangText("PlaceholderNotAvailable")
        if (player == null) return notAvailable
        val playerName = player.name
        val levelGroup = LevelGroup.getLevelGroups()[argsList[0]] ?: return notAvailable
        val currentLevel = levelGroup.getMemberLevel(playerName)
        val lastLevel = currentLevel - 1
        val nextLevel = currentLevel + 1
        val minLevel = levelGroup.getMinLevel()
        val maxLevel = levelGroup.getMaxLevel()
        val currentExp = levelGroup.getMemberExp(playerName)
        val nextLevelExp = if (isProcessing.get()) levelGroup.getLevelExp(currentLevel, nextLevel)
        else levelGroup.getLevelExp(playerName, currentLevel, nextLevel)
        if (!isProcessing.get()) isProcessing.set(true)
        try {
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
                "levelname" -> if (isProcessing.get()) levelGroup.getLevelName(currentLevel).colored()
                else levelGroup.getLevelName(playerName, currentLevel).colored()

                "lastlevelname" -> if (isProcessing.get()) levelGroup.getLevelName(lastLevel).colored()
                else levelGroup.getLevelName(playerName, lastLevel).colored()

                "nextlevelname" -> if (isProcessing.get()) levelGroup.getLevelName(nextLevel).colored()
                else levelGroup.getLevelName(playerName, nextLevel).colored()

                // 升级所需经验。
                "levelexp" -> if (isProcessing.get()) levelGroup.getLevelExp(lastLevel, currentLevel)
                else levelGroup.getLevelExp(playerName, lastLevel, currentLevel)

                "lastlevelexp" -> if (isProcessing.get()) levelGroup.getLevelExp(currentLevel - 2, lastLevel)
                else levelGroup.getLevelExp(playerName, currentLevel - 2, lastLevel)

                "nextlevelexp" -> nextLevelExp

                "levelexpfrom" -> {
                    val oldLevel = argsList[2].toLongOrNull() ?: return notAvailable
                    if (oldLevel > currentLevel) return notAvailable
                    if (isProcessing.get()) levelGroup.getLevelExp(oldLevel, currentLevel)
                    else levelGroup.getLevelExp(playerName, oldLevel, currentLevel)
                }

                "levelexpto" -> {
                    val newLevel = argsList[2].toLongOrNull() ?: return notAvailable
                    if (newLevel < currentLevel) return notAvailable
                    if (isProcessing.get()) levelGroup.getLevelExp(currentLevel, newLevel)
                    else levelGroup.getLevelExp(playerName, currentLevel, newLevel)
                }

                "levelexpfromto" -> {
                    val oldLevel = argsList[2].toLongOrNull() ?: return notAvailable
                    val newLevel = argsList[3].toLongOrNull() ?: return notAvailable
                    if (oldLevel > newLevel) return notAvailable
                    if (isProcessing.get()) levelGroup.getLevelExp(oldLevel, newLevel)
                    else levelGroup.getLevelExp(playerName, oldLevel, newLevel)
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
            if (isProcessing.get()) isProcessing.set(false)
        }
    }
}