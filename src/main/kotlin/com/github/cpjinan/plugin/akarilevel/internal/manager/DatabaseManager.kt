package com.github.cpjinan.plugin.akarilevel.internal.manager

import com.github.cpjinan.plugin.akarilevel.internal.database.*

object DatabaseManager {
    private var database: Database? = null

    private fun openDatabase(): Database {
        val dbType = ConfigManager.getMethod() ?: "JSON"
        return when (dbType) {
            "JSON" -> {
                DbJson()
            }

            "CBOR" -> {
                DbCbor()
            }

            "SQL" -> {
                DbSql()
            }

            else -> {
                throw IllegalArgumentException("unknown dbType")
            }
        }
    }

    fun getDatabase(): Database = if (database != null) {
        database!!
    } else {
        database = openDatabase()
        database!!
    }

    fun getRedis(): Database {
        return DbRedis()
    }

    fun getHashMap(): DbHashMap {
        return DbHashMap
    }
}