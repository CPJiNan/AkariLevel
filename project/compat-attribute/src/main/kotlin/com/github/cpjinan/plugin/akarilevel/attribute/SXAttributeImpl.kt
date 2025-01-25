package com.github.cpjinan.plugin.akarilevel.attribute

import com.github.cpjinan.plugin.akarilevel.AkariLevelAttribute
import com.github.cpjinan.plugin.akarilevel.AkariLevelSettings
import github.saukiya.sxattribute.SXAttribute
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.platform.BukkitPlugin

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.attribute
 *
 * @author 季楠
 * @since 2025/1/25 11:19
 */
class SXAttributeImpl : AkariLevelAttribute {
    private val api = SXAttribute.getApi()
    private val pluginClass = BukkitPlugin.getInstance().javaClass

    override fun getAttribute(player: Player, attributeName: String): Number {
        return api.getEntityData(player).getValues(attributeName)[0]
    }

    override fun getSourceAttribute(player: Player, attributeName: String): Number {
        return api.getEntityAPIData(
            pluginClass,
            player.uniqueId
        ).getValues(attributeName)[0]
    }

    override fun setSourceAttribute(player: Player, attributeList: List<String>, amount: Number) {
        api.setEntityAPIData(
            pluginClass,
            player.uniqueId,
            api.loadListData(attributeList)
        )
    }

    override fun removeSourceAttribute(player: Player) {
        api.removePluginAllEntityData(pluginClass)
    }

    override fun updateAttribute(player: Player) {
        api.attributeUpdate(player)
    }

    companion object {
        @Awake(LifeCycle.CONST)
        fun onConst() {
            if (AkariLevelSettings.attributePlugin.lowercase() == "sx-attribute") {
                PlatformFactory.registerAPI<AkariLevelAttribute>(SXAttributeImpl())
            }
        }
    }
}