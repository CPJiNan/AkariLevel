package com.github.cpjinan.plugin.akarilevel.config

import com.github.cpjinan.plugin.akarilevel.config.SettingsConfig.settings
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.HostSQL
import taboolib.module.database.HostSQLite
import taboolib.module.database.getHost
import java.io.File

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
    val type: String by lazy {
        settings.getString("Database.Type") ?: "SQLITE"
    }

    val table: String by lazy {
        settings.getString("Database.Table") ?: "AkariLevel"
    }

    val hostSQL: HostSQL by lazy {
        settings.getHost("Database.MYSQL")
    }

    val file: String by lazy {
        settings.getString("Database.SQLITE.file") ?: "sqlite.db"
    }

    val hostSQLite: HostSQLite by lazy {
        HostSQLite(
            File(getDataFolder(), file).apply {
                parentFile?.mkdirs()
                createNewFile()
            }
        )
    }
}