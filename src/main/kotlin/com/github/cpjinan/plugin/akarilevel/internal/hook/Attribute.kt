package com.github.cpjinan.plugin.akarilevel.internal.hook

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.impl.ExpAddon
import com.github.cpjinan.plugin.akarilevel.api.AkariLevelAPI
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
        if (ConfigManager.isEnabledAttribute() && Bukkit.getServer().pluginManager.isPluginEnabled(ConfigManager.getAttributePlugin()) && event.source in ConfigManager.getAttributeSource()) {
            val curExp = AkariLevelAPI.getPlayerExp(event.player)
            var exp = event.exp - curExp
            val attributeValue: Number = when (ConfigManager.getAttributePlugin()) {
                "AttributePlus" -> AttributePlus.attributeManager.getAttributeData(event.player)
                    .getAttributeValue(ConfigManager.getAttributeName())[0]

                "SX-Attribute" -> SXAttribute.getApi().getEntityData(event.player)
                    .getValues(ConfigManager.getAttributeName())[0]

                "OriginAttribute" -> OriginAttributeAPI.getAttributeData(event.player)
                    .getData(ExpAddon().index, ExpAddon.DefaultImpl().index)
                    .get(ExpAddon.DefaultImpl().index)

                else -> throw IllegalArgumentException("Unsupported attribute plugin ${ConfigManager.getAttributePlugin()}.")
            }
            exp = ConfigManager.getAttributeFormula()
                .replace("%exp%", exp.toString(), true)
                .replace("%attribute%", attributeValue.toDouble().toString(), true)
                .compileJS()?.eval()?.toString()?.toDouble()?.roundToInt() ?: exp
            event.exp = curExp + exp
        }
    }
}