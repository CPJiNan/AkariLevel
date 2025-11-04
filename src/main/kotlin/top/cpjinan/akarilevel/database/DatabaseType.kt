package top.cpjinan.akarilevel.database

import top.cpjinan.akarilevel.config.DatabaseConfig

/**
 * AkariLevel
 * top.cpjinan.akarilevel.database
 *
 * 数据库类型枚举。
 *
 * @author 季楠
 * @since 2025/8/7 23:13
 */
enum class DatabaseType {
    SQLITE,
    MYSQL;

    companion object {
        @JvmStatic
        val instance by lazy {
            try {
                valueOf(DatabaseConfig.type.uppercase())
            } catch (e: Exception) {
                e.printStackTrace()
                SQLITE
            }
        }
    }
}