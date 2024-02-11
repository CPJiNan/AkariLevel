package com.github.cpjinan.plugin.akarilevel.internal.hook

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.impl.ExpAddon
import com.github.cpjinan.plugin.akarilevel.api.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import github.saukiya.sxattribute.SXAttribute
import org.bukkit.Bukkit
import org.serverct.ersha.AttributePlus
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.compileJS
import kotlin.math.roundToInt

object Attribute {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        var exp = event.exp
        if (ConfigManager.isEnabledAttribute() && event.source in ConfigManager.getAttributeSource()) {
            var attributeValue: Number = 0
            when (ConfigManager.getAttributePlugin()) {
                "AttributePlus" -> if (Bukkit.getServer().pluginManager.isPluginEnabled("AttributePlus")) attributeValue =
                    AttributePlus.attributeManager.getAttributeData(event.player)
                        .getAttributeValue(ConfigManager.getAttributeName())[0]

                "SX-Attribute" -> if (Bukkit.getServer().pluginManager.isPluginEnabled("SX-Attribute")) attributeValue =
                    SXAttribute.getApi().getEntityData(event.player).getValues(ConfigManager.getAttributeName())[0]

                "OriginAttribute" -> if (Bukkit.getServer().pluginManager.isPluginEnabled("OriginAttribute")) attributeValue =
                    OriginAttributeAPI.getAttributeData(event.player)
                        .getData(ExpAddon().index, ExpAddon.DefaultImpl().index)
                        .get(ExpAddon.DefaultImpl().index)

                else -> throw IllegalArgumentException("Unsupported attribute plugin ${ConfigManager.getAttributePlugin()}.")
            }
            exp = ConfigManager.getAttributeFormula()
                .replace("%exp%", event.exp.toString(), true)
                .replace("%attribute%", attributeValue.toDouble().toString(), true)
                .compileJS()?.eval()?.toString()?.toDouble()?.roundToInt() ?: event.exp
        }
        event.exp = exp
    }
}