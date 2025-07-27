package com.github.cpjinan.plugin.akarilevel.data

import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * @author 季楠
 * @since 2025/7/27 18:13
 */
enum class DatabaseType {
    SQLITE,
    MYSQL;

    companion object {
        val INSTANCE: DatabaseType by lazy {
            try {
                valueOf(DatabaseConfig.type.uppercase())
            } catch (_: Exception) {
                SQLITE
            }
        }
    }
}