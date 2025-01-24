package com.github.cpjinan.plugin.akarilevel.attribute

import com.github.cpjinan.plugin.akarilevel.AkariLevelAttribute
import com.github.cpjinan.plugin.akarilevel.AkariLevelSettings
import org.bukkit.entity.Player
import org.serverct.ersha.api.AttributeAPI
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.attribute
 *
 * @author 季楠
 * @since 2025/1/24 14:04
 */
class AttributePlusImpl : AkariLevelAttribute {

    override fun getAttributeValue(player: Player, attribute: String): Number {
        return AttributeAPI.getAttrData(player).getAttributeValue(attribute)[0]
    }

    companion object {
        @Awake(LifeCycle.ENABLE)
        fun onEnable() {
            if (AkariLevelSettings.attributePlugin.lowercase() == "attributeplus".uppercase()) {
                PlatformFactory.registerAPI<AkariLevelAttribute>(AttributePlusImpl())
            }
        }
    }
}