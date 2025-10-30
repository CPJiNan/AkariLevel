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
 * Kether 脚本升级条件。
 *
 * @author 季楠
 * @since 2025/10/30 23:17
 */
@Suppress("DEPRECATION")
object KetherLevelCondition : ConfigLevelCondition {
    override fun check(member: String, levelGroup: String, level: Long, config: ConfigurationSection): Boolean {
        val ketherConditions = config.getStringList("Kether")
        if (ketherConditions.isEmpty()) return true
        val offlinePlayer = getOfflinePlayer(member)
        if (!offlinePlayer.isOnline) return false
        return ketherConditions.all {
            KetherShell.eval(
                it
                    .replace("{member}" to member, "{levelGroup}" to levelGroup, "{level}" to level)
                    .replacePlaceholder(offlinePlayer.player),
                ScriptOptions(sender = adaptPlayer(offlinePlayer.player))
            ).join().toString().toBoolean()
        }
    }
}