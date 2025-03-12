package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.internal.database.Database
import com.github.cpjinan.plugin.akarilevel.internal.database.DbJson
import com.github.cpjinan.plugin.akarilevel.internal.database.DbSql
import com.github.cpjinan.plugin.akarilevel.internal.database.DbSqlite

object PluginDatabase {
    private var database: Database? = null

    private fun openDatabase(): Database {
        val dbType = PluginConfig.getMethod() ?: "JSON"
        return when (dbType.uppercase()) {
            "JSON" -> {
                DbJson()
            }

            "SQL" -> {
                DbSql()
            }

            "SQLITE" -> {
                DbSqlite()
            }

            else -> {
                throw IllegalArgumentException("Unknown dbType.")
            }
        }
    }

    fun getDatabase(): Database = if (database != null) {
        database!!
    } else {
        database = openDatabase()
        database!!
    }
}