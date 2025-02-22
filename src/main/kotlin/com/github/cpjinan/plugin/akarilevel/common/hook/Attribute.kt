package com.github.cpjinan.plugin.akarilevel.common.hook

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.impl.ExpAddon
import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import com.skillw.attsystem.api.AttrAPI.getAttrData
import github.saukiya.sxattribute.SXAttribute
import org.bukkit.Bukkit
import org.serverct.ersha.AttributePlus
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.compileJS
import kotlin.math.roundToLong

object Attribute {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        if (PluginConfig.isEnabledAttribute() && Bukkit.getServer().pluginManager.isPluginEnabled(PluginConfig.getAttributePlugin()) && event.source in PluginConfig.getAttributeSource()) {
            var exp = event.expAmount
            val attributeValue = when (PluginConfig.getAttributePlugin()) {
                "AttributePlus" -> {
                    when (Bukkit.getServer().pluginManager.getPlugin("AttributePlus")!!.description.version[0]) {
                        '3' -> AttributePlus.attributeManager.getAttributeData(event.player)
                            .getAttributeValue(PluginConfig.getAttributeName())[0]

                        '2' -> org.serverct.ersha.jd.AttributeAPI.getAttrData(event.player)
                            .getAttributeValue(PluginConfig.getAttributeName())

                        else -> throw IllegalArgumentException(
                            "Unsupported AttributePlus version ${
                                Bukkit.getServer().pluginManager.getPlugin(
                                    "AttributePlus"
                                )!!.description.version
                            }."
                        )
                    }
                }

                "SX-Attribute" -> SXAttribute.getApi().getEntityData(event.player)
                    .getValues(PluginConfig.getAttributeName())[0]

                "OriginAttribute" -> OriginAttributeAPI.getAttributeData(event.player)
                    .getData(ExpAddon().index, ExpAddon.DefaultImpl().index)
                    .get(ExpAddon.DefaultImpl().index)

                "AttributeSystem" -> event.player.uniqueId.getAttrData()
                    ?.getAttrValue<Double>(PluginConfig.getAttributeName(), "total") ?: 0

                else -> throw IllegalArgumentException("Unsupported attribute plugin ${PluginConfig.getAttributePlugin()}.")
            }
            exp = PluginConfig.getAttributeFormula()
                .replace("%exp%", exp.toString(), true)
                .replace("%attribute%", attributeValue.toDouble().toString(), true)
                .compileJS()?.eval()?.toString()?.toDouble()?.roundToLong() ?: exp
            event.expAmount = exp
        }
    }
}