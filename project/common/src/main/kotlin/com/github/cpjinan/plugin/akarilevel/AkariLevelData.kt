package com.github.cpjinan.plugin.akarilevel

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel
 *
 * @author 季楠
 * @since 2025/1/30 13:27
 */
interface AkariLevelData {
    val map: HashMap<String, Any?>

    fun getKeys(deep: Boolean): Set<String>

    fun contains(path: String): Boolean

    fun get(path: String): Any?

    fun get(path: String, def: Any?): Any?

    fun set(path: String, value: Any?)

    fun getString(path: String): String?

    fun getString(path: String, def: String?): String?

    fun isString(path: String): Boolean

    fun getInt(path: String): Int

    fun getInt(path: String, def: Int): Int

    fun isInt(path: String): Boolean

    fun getBoolean(path: String): Boolean

    fun getBoolean(path: String, def: Boolean): Boolean

    fun isBoolean(path: String): Boolean {
        return get(path) is Double
    }

    fun getDouble(path: String): Double

    fun getDouble(path: String, def: Double): Double

    fun isDouble(path: String): Boolean

    fun getLong(path: String): Long

    fun getLong(path: String, def: Long): Long

    fun isLong(path: String): Boolean

    fun getList(path: String): List<*>?

    fun getList(path: String, def: List<*>?): List<*>?

    fun isList(path: String): Boolean

    fun getStringList(path: String): List<String>

    fun getIntegerList(path: String): List<Int>

    fun getBooleanList(path: String): List<Boolean>

    fun getDoubleList(path: String): List<Double>

    fun getFloatList(path: String): List<Float>

    fun getLongList(path: String): List<Long>

    fun getByteList(path: String): List<Byte>

    fun getCharacterList(path: String): List<Char>

    fun getShortList(path: String): List<Short>

    fun getMapList(path: String): List<Map<*, *>>
}