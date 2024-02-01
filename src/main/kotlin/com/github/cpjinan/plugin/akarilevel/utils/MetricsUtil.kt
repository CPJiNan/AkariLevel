package com.github.cpjinan.plugin.akarilevel.utils

import taboolib.common.platform.Platform
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin

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
     */
    fun registerBukkitMetrics(serviceId: Int) {
        Metrics(
            serviceId,
            BukkitPlugin.getInstance().description.version,
            Platform.BUKKIT
        )
    }
}