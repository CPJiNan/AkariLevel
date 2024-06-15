package com.github.cpjinan.plugin.akarilevel.internal.database

interface Database {
    fun getValue(table: String, index: String, key: String): String
    fun setValue(table: String, index: String, key: String, value: String)
    fun save()
}