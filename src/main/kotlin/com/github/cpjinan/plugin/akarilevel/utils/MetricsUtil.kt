package com.github.cpjinan.plugin.akarilevel.utils

import org.bukkit.plugin.Plugin
import taboolib.common.platform.Platform
import taboolib.module.metrics.Metrics

/**
 * bStats util
 * @author CPJiNan
 * @since 2024/01/15
 */
object MetricsUtil {
    /**
     * register bStats service
     * collect usage data for Bukkit plugin
     * @param [serviceId] service id
     * @param [plugin] plugin instance
     */
    fun registerBukkitMetrics(serviceId: Int, plugin: Plugin) {
        Metrics(
            serviceId,
            plugin.description.version,
            Platform.BUKKIT
        )
    }
}