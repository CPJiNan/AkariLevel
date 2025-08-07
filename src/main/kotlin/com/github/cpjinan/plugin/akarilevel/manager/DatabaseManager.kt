package com.github.cpjinan.plugin.akarilevel.manager

import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.database.DatabaseMySQL
import com.github.cpjinan.plugin.akarilevel.database.DatabaseSQLite
import com.github.cpjinan.plugin.akarilevel.database.DatabaseType

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.manager
 *
 * 数据库管理器。
 *
 * @author 季楠
 * @since 2025/8/7 22:16
 */
object DatabaseManager {
    /**
     * 获取数据库实例。
     *
     * @return [Database] 实例。
     */
    fun getDatabase(): Database {
        return when (DatabaseType.INSTANCE) {
            DatabaseType.SQLITE -> DatabaseSQLite()
            DatabaseType.MYSQL -> DatabaseMySQL()
        }
    }
}