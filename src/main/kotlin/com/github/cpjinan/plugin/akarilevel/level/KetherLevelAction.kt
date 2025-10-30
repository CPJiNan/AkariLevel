package com.github.cpjinan.plugin.akarilevel.level

import org.bukkit.Bukkit.getOfflinePlayer
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.util.replace
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.compat.replacePlaceholder

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.level
 *
 * Kether 脚本升级动作。
 *
 * @author 季楠
 * @since 2025/10/30 23:17
 */
@Suppress("DEPRECATION")
object KetherLevelAction : ConfigLevelAction {
    override fun run(member: String, levelGroup: String, level: Long, config: ConfigurationSection) {
        val ketherActions = config.getStringList("Kether")
        if (ketherActions.isEmpty()) return
        val offlinePlayer = getOfflinePlayer(member)
        if (!offlinePlayer.isOnline) return
        KetherShell.eval(
            ketherActions
                .replace("{member}" to member, "{levelGroup}" to levelGroup, "{level}" to level)
                .replacePlaceholder(offlinePlayer.player),
            ScriptOptions(sender = adaptPlayer(offlinePlayer.player))
        )
    }
}