package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.common.PluginDatabase.getDatabase

/**
 * 插件数据相关 API
 * @author CPJiNan
 * @since 2024/06/23
 */
object DataAPI {
    /**
     * 获取指定表中某索引下某键的值
     * @param table 表名
     * @param index 索引名
     * @param key 键名
     * @return 数据值
     */
    fun getDataValue(table: String, index: String, key: String): String = getValue(table, index, key)

    /**
     * 设置指定表中某索引下某键的值
     * @param table 表名
     * @param index 索引名
     * @param key 键名
     * @param value 数据值
     */
    fun setDataValue(table: String, index: String, key: String, value: String) {
        setValue(table, index, key, value)
    }

    /**
     * 保存数据 (使用 setDataValue 修改数据后会自动保存)
     */
    fun saveData() {
        save()
    }

    private fun getValue(table: String, index: String, key: String): String = getDatabase().getValue(table, index, key)

    private fun setValue(table: String, index: String, key: String, value: String) {
        getDatabase().setValue(table, index, key, value)
        save()
    }

    private fun save() {
        getDatabase().save()
    }
}