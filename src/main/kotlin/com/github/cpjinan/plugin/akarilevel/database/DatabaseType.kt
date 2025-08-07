package com.github.cpjinan.plugin.akarilevel.database

import com.github.cpjinan.plugin.akarilevel.config.DatabaseConfig

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * 数据库类型。
 *
 * @author 季楠
 * @since 2025/8/7 23:13
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