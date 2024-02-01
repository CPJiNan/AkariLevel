package com.github.cpjinan.plugin.akarilevel.internal.hook

import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import github.saukiya.sxattribute.SXAttribute
import org.bukkit.entity.Player
import org.serverct.ersha.AttributePlus
import taboolib.common5.compileJS

class Attribute {
    fun getAddition(player: Player, exp: Int, source: String): Int {
        if (ConfigManager.isEnabledAttribute() && source in ConfigManager.getAttributeSource()) {
            var attributeValue: Number = 0
            when (ConfigManager.getAttributeName()) {
                "AttributePlus" -> attributeValue =
                    AttributePlus.attributeManager.getAttributeData(player.uniqueId, player)
                        .getAttributeValue(ConfigManager.getAttributeName())[0]

                "SX-Attribute" -> attributeValue =
                    SXAttribute.getApi().getEntityData(player).getValues(ConfigManager.getAttributeName())[0]
            }
            return ConfigManager.getAttributeFormula()
                .replace("%exp%", exp.toString(), true)
                .replace("%attribute%", attributeValue.toDouble().toString(), true)
                .compileJS()?.eval()?.toString()?.toIntOrNull() ?: return exp
        }
        return exp
    }
}