package com.github.cpjinan.plugin.akarilevel.config

import com.github.cpjinan.plugin.akarilevel.utils.FileUtils
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.database.HostSQL
import taboolib.module.database.HostSQLite
import taboolib.module.database.getHost

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.config
 *
 * 数据库配置。
 *
 * @author 季楠
 * @since 2025/8/7 22:20
 */
object DatabaseConfig {
    @Config("settings.yml")
    lateinit var config: Configuration
        private set

    val type: String by lazy {
        config.getString("Database.Type") ?: "SQLITE"
    }

    val table: String by lazy {
        config.getString("Database.Table") ?: "akarilevel"
    }

    val hostSQL: HostSQL by lazy {
        config.getHost("Database.MYSQL")
    }

    val file: String by lazy {
        config.getString("Database.SQLITE.file") ?: "sqlite.db"
    }

    val hostSQLite: HostSQLite by lazy {
        HostSQLite(FileUtils.getFileOrCreate(file))
    }
}