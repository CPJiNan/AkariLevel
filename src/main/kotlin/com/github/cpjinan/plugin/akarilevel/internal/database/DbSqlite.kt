package com.github.cpjinan.plugin.akarilevel.internal.database

import com.github.cpjinan.plugin.akarilevel.common.PluginConfig
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.HostSQLite
import taboolib.module.database.Table
import java.io.File

class DbSqlite : Database {

    private val host =
        HostSQLite(File(getDataFolder(), PluginConfig.getSqliteSection().getString("file")!!))
    private val dataSource by lazy { host.createDataSource() }
    private val sqlTable = Table(PluginConfig.getSqlTable(), host) {
        add("table_name") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("index_name") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("key") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("value") {
            type(ColumnTypeSQLite.TEXT)
        }
    }

    init {
        sqlTable.createTable(dataSource, checkExists = true)
        sqlTable.createIndex(dataSource, "idx_table", listOf("table_name"), checkExists = true)
        sqlTable.createIndex(dataSource, "idx_index", listOf("index_name"), checkExists = true)
        sqlTable.createIndex(dataSource, "idx_key", listOf("key"), checkExists = true)
    }

    override fun getValue(table: String, index: String, key: String): String {
        return get(table, index, key)
    }

    override fun setValue(table: String, index: String, key: String, value: String) {
        set(table, index, key, value)
    }

    override fun save() {}

    private fun add(table: String, index: String, key: String, value: String) {
        sqlTable.insert(dataSource, "table_name", "index_name", "key", "value") {
            value(table, index, key, value)
        }
    }

    private fun delete(table: String, index: String, key: String) {
        sqlTable.delete(dataSource) {
            where { "table_name" eq table and ("index_name" eq index) and ("key" eq key) }
        }
    }

    fun set(table: String, index: String, key: String, value: String) {
        if (have(table, index, key)) sqlTable.update(dataSource) {
            set("value", value)
            where("table_name" eq table and ("index_name" eq index) and ("key" eq key))
        } else add(table, index, key, value)
    }

    fun get(table: String, index: String, key: String): String {
        return sqlTable.select(dataSource) {
            where("table_name" eq table and ("index_name" eq index) and ("key" eq key))
            limit(1)
        }.firstOrNull {
            this.getString("value")
        }.orEmpty()
    }

    fun have(table: String, index: String, key: String): Boolean {
        return sqlTable.select(dataSource) {
            where("table_name" eq table and ("index_name" eq index) and ("key" eq key))
            limit(1)
        }.firstOrNull {
            true
        } ?: false
    }
}