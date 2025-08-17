package com.github.cpjinan.plugin.akarilevel.config

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.config
 *
 * 玩家配置。
 *
 * @author 季楠
 * @since 2025/8/7 22:20
 */
@ConfigNode(bind = "player.yml")
object PlayerConfig {
    @Config("player.yml")
    lateinit var player: Configuration
        private set

    /**
     * 自动加入等级组。
     */
    @ConfigNode("Player.Auto-Join")
    var autoJoin = listOf<String>()

    /**
     * 取消原版经验变更事件。
     */
    @ConfigNode("Player.Cancel-Vanilla")
    var cancelVanilla = true
}