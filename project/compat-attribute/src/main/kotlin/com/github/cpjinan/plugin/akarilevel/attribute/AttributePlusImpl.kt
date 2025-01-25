package com.github.cpjinan.plugin.akarilevel.attribute

import com.github.cpjinan.plugin.akarilevel.AkariLevelAttribute
import com.github.cpjinan.plugin.akarilevel.AkariLevelSettings
import org.bukkit.entity.Player
import org.serverct.ersha.api.AttributeAPI
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.platform.BukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.attribute
 *
 * @author 季楠
 * @since 2025/1/24 14:04
 */
class AttributePlusImpl : AkariLevelAttribute {
    override fun getAttribute(player: Player, attributeName: String): Number {
        return AttributeAPI.getAttrData(player).getAttributeValue(attributeName)[0]
    }

    override fun getSourceAttribute(player: Player, attributeName: String): Number {
        return AttributeAPI.getAttrData(player)
            .getSourceAttributeValue(BukkitPlugin.getInstance().name, attributeName)[0]
    }

    override fun setSourceAttribute(player: Player, attributeList: List<String>, amount: Number) {
        AttributeAPI.addSourceAttribute(
            AttributeAPI.getAttrData(player),
            BukkitPlugin.getInstance().name,
            attributeList
        )
    }

    override fun removeSourceAttribute(player: Player) {
        AttributeAPI.takeSourceAttribute(AttributeAPI.getAttrData(player), BukkitPlugin.getInstance().name)
    }

    override fun updateAttribute(player: Player) {
        AttributeAPI.updateAttribute(player)
    }

    companion object {
        @Awake(LifeCycle.CONST)
        fun onConst() {
            if (AkariLevelSettings.attributePlugin.lowercase() == "attributeplus") {
                PlatformFactory.registerAPI<AkariLevelAttribute>(AttributePlusImpl())
            }
        }
    }
}