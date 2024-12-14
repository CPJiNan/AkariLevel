package com.github.cpjinan.plugin.akarilevel.utils.core

import org.bukkit.plugin.Plugin


object VersionUtil {
    data class Version(
        val major: String,
        val minor: String,
        val patch: String,
        val prerelease: String? = null,
        val buildmetadata: String? = null
    ) : Comparable<Version> {
        override fun toString(): String =
            "$major.$minor.$patch${prerelease?.let { "-$it" } ?: ""}${buildmetadata?.let { "+$it" } ?: ""}"

        override fun compareTo(other: Version): Int =
            compareValuesBy(this, other, Version::major, Version::minor, Version::patch)
                .thenCompare(compareNullableStrings(prerelease, other.prerelease))
                .thenCompare(compareNullableStrings(buildmetadata, other.buildmetadata))

        private fun compareNullableStrings(str1: String?, str2: String?): Int {
            return when {
                str1 == null && str2 != null -> 1
                str1 != null && str2 == null -> -1
                else -> compareValuesBy(str1, str2) { it ?: "" }
            }
        }

        private infix fun Int.thenCompare(other: Int): Int = if (this != 0) this else other
    }

    @JvmStatic
    fun String.toVersion(): Version? =
        Regex("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$")
            .matchEntire(this)
            ?.let {
                Version(
                    major = it.groupValues[1],
                    minor = it.groupValues[2],
                    patch = it.groupValues[3],
                    prerelease = it.groupValues[4].takeIf { it.isNotEmpty() },
                    buildmetadata = it.groupValues[5].takeIf { it.isNotEmpty() }
                )
            }

    @JvmStatic
    fun Plugin.getVersion(): Version? {
        return this.description.version.toVersion()
    }

}