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
 * @author 季楠
 * @since 2025/7/27 19:06
 */
object DatabaseConfig {
    @Config("settings.yml")
    lateinit var settings: Configuration
        private set

    val type: String by lazy {
        settings.getString("Database.Type") ?: "SQLITE"
    }

    val table: String by lazy {
        settings.getString("Database.Table") ?: "akarilevel"
    }

    val hostSQL: HostSQL by lazy {
        settings.getHost("Database.MYSQL")
    }

    val file: String by lazy {
        settings.getString("Database.SQLITE.file") ?: "database.db"
    }

    val hostSQLite: HostSQLite by lazy {
        HostSQLite(FileUtils.getFileOrCreate(file))
    }
}