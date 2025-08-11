package com.github.cpjinan.plugin.akarilevel.database

import taboolib.module.database.Table
import javax.sql.DataSource

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database
 *
 * 数据库接口。
 *
 * @author TabooLib, 季楠
 * @since 2025/8/7 23:08
 */
interface Database {
    companion object {
        @JvmStatic
        val INSTANCE by lazy {
            when (DatabaseType.INSTANCE) {
                DatabaseType.SQLITE -> DatabaseSQLite()
                DatabaseType.MYSQL -> DatabaseMySQL()
            }
        }
    }

    /** 数据库类型 **/
    val type: DatabaseType

    /** 数据源 **/
    val dataSource: DataSource

    /** 成员数据表 **/
    val memberTable: Table<*, *>

    /**
     * 获取此数据库中所有键的集合。
     *
     * @param table 要操作的数据表。
     * @return 包含在此数据库中的键集合。
     */
    fun getKeys(table: Table<*, *>): Set<String>

    /**
     * 获取当前数据库中的所有值。
     *
     * @param table 要操作的数据表。
     * @return 包含当前数据库中所有键值对的 Map。
     */
    fun getValues(table: Table<*, *>): Map<String, String?>

    /**
     * 检查此数据库中是否包含给定路径。
     *
     * 如果请求路径的值不存在但已指定默认值，则此方法将返回 true。
     *
     * @param table 要操作的数据表。
     * @param path 要检查存在性的路径。
     * @return 如果此数据库包含请求的路径（通过默认值或已设置），则返回 true。
     */
    fun contains(table: Table<*, *>, path: String): Boolean

    /**
     * 通过路径获取请求的对象。
     *
     * 如果对象不存在，则返回 null。
     *
     * @param table 要操作的数据表。
     * @param path 要获取的对象的路径。
     * @return 请求的对象。
     */
    fun get(table: Table<*, *>, path: String): String?

    /**
     * 将指定路径设置为给定值。
     *
     * 如果值为 null，则会删除该条目。任何现有条目都将被替换，无论新值是什么。
     *
     * @param table 要操作的数据表。
     * @param path 要设置的对象的路径。
     * @param value 要设置的新值。
     */
    fun set(table: Table<*, *>, path: String, value: String?)
}