package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.internal.manager.DatabaseManager.getDatabase

object DataAPI {
    // region function
    fun getDataValue(table: String, index: String, key: String): String = getValue(table, index, key)

    fun setDataValue(table: String, index: String, key: String, value: String) {
        setValue(table, index, key, value)
    }

    fun saveData() {
        save()
    }

    // region basic function
    private fun getValue(table: String, index: String, key: String): String = getDatabase().getValue(table, index, key)

    private fun setValue(table: String, index: String, key: String, value: String) {
        getDatabase().setValue(table, index, key, value)
        save()
    }

    private fun save() {
        getDatabase().save()
    }
}