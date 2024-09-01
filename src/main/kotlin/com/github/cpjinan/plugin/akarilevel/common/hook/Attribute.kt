package com.github.cpjinan.plugin.akarilevel.common.hook

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.impl.ExpAddon
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.skillw.attsystem.api.AttrAPI.getAttrData
import github.saukiya.sxattribute.SXAttribute
import org.bukkit.Bukkit
import org.serverct.ersha.AttributePlus
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common5.compileJS
import kotlin.math.roundToLong

object Attribute {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        if (ConfigManager.isEnabledAttribute() && Bukkit.getServer().pluginManager.isPluginEnabled(ConfigManager.getAttributePlugin()) && event.source in ConfigManager.getAttributeSource()) {
            var exp = event.expAmount
            val attributeValue: Number = when (ConfigManager.getAttributePlugin()) {
                "AttributePlus" -> {
                    when(Bukkit.getServer().pluginManager.getPlugin("AttributePlus")!!.description.version[0]){
                        '3' -> AttributePlus.attributeManager.getAttributeData(event.player)
                            .getAttributeValue(ConfigManager.getAttributeName())[0]

                        '2' -> org.serverct.ersha.jd.AttributeAPI.getAttrData(event.player)
                            .getAttributeValue(ConfigManager.getAttributeName())

                        else -> throw IllegalArgumentException("Unsupported AttributePlus version ${Bukkit.getServer().pluginManager.getPlugin("AttributePlus")!!.description.version}.")
                    }
                }

                "SX-Attribute" -> SXAttribute.getApi().getEntityData(event.player)
                    .getValues(ConfigManager.getAttributeName())[0]

                "OriginAttribute" -> OriginAttributeAPI.getAttributeData(event.player)
                    .getData(ExpAddon().index, ExpAddon.DefaultImpl().index)
                    .get(ExpAddon.DefaultImpl().index)

                "AttributeSystem" -> event.player.uniqueId.getAttrData()
                    ?.getAttrValue<Double>(ConfigManager.getAttributeName(), "total") as Number

                else -> throw IllegalArgumentException("Unsupported attribute plugin ${ConfigManager.getAttributePlugin()}.")
            }
            exp = ConfigManager.getAttributeFormula()
                .replace("%exp%", exp.toString(), true)
                .replace("%attribute%", attributeValue.toDouble().toString(), true)
                .compileJS()?.eval()?.toString()?.toDouble()?.roundToLong() ?: exp
            info(exp, attributeValue)
            event.expAmount = exp
        }
    }
}