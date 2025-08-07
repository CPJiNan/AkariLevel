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
    /** 数据库类型 **/
    val type: DatabaseType

    /** 数据表 **/
    val table: Table<*, *>

    /** 数据源 **/
    val dataSource: DataSource

    /**
     * 获取此数据库中所有键的集合。
     *
     * @return 包含在此 [Database] 中的键集合。
     */
    fun getKeys(): Set<String>

    /**
     * 获取当前 [Database] 的所有值。
     *
     * @return 包含当前 [Database] 所有键值对的 Map。
     */
    fun getValues(): Map<String, String?>

    /**
     * 将当前 [Database] 转换为 Map。
     *
     * @return 包含当前 [Database] 所有键值对的 Map。
     */
    fun toMap(): Map<String, String?>

    /**
     * 检查此 [Database] 是否包含给定路径。
     *
     * 如果请求路径的值不存在但已指定默认值，则此方法将返回 true。
     *
     * @param path 要检查存在性的路径。
     * @return 如果此数据库包含请求的路径（通过默认值或已设置），则返回 true。
     */
    operator fun contains(path: String): Boolean

    /**
     * 方法 [contains] 的别名。
     */
    fun isSet(path: String): Boolean

    /**
     * 通过路径获取请求的对象。
     *
     * 如果对象不存在，则返回 null。
     *
     * @param path 要获取的对象的路径。
     * @return 请求的对象。
     */
    operator fun get(path: String): String?

    /**
     * 通过路径获取请求的对象。
     *
     * 如果对象不存在但已指定默认值，则将返回默认值。
     * 如果对象不存在且未指定默认值，则返回 null。
     *
     * @param path 要获取的对象的路径。
     * @return 请求的对象。
     */
    operator fun get(path: String, def: String?): String?

    /**
     * 将指定路径设置为给定值。
     *
     * 如果值为 null，则会删除该条目。任何现有条目都将被替换，无论新值是什么。
     *
     * @param path 要设置的对象的路径。
     * @param value 要设置的新值。
     */
    operator fun set(path: String, value: String?)

    /**
     * 保存当前 [Database] 中的所有数据。
     */
    fun save()

    /**
     * 重载当前 [Database] 中的所有数据。
     */
    fun reload()

    /**
     * 清除当前 [Database] 中的所有数据。
     */
    fun clear()
}