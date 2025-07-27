package com.github.cpjinan.plugin.akarilevel

import com.github.cpjinan.plugin.akarilevel.database.Database
import com.github.cpjinan.plugin.akarilevel.database.DatabaseMySQL
import com.github.cpjinan.plugin.akarilevel.database.DatabaseSQLite
import com.github.cpjinan.plugin.akarilevel.database.DatabaseType
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/7/27 19:25
 */
object DefaultAkariLevelDatabase : AkariLevelDatabase {
    override fun getDefault(): Database {
        return when (DatabaseType.INSTANCE) {
            DatabaseType.SQLITE -> DatabaseSQLite()
            DatabaseType.MYSQL -> DatabaseMySQL()
        }
    }

    @Awake(LifeCycle.CONST)
    fun onConst() {
        PlatformFactory.registerAPI<AkariLevelDatabase>(DefaultAkariLevelDatabase)
    }
}