package com.github.cpjinan.plugin.akarilevel.utils

import org.bukkit.plugin.Plugin


object VersionUtil {
    data class SemanticVersion(
        val major: String,
        val minor: String,
        val patch: String,
        val prerelease: String? = null,
        val buildmetadata: String? = null
    ) : Comparable<SemanticVersion> {
        override fun toString(): String =
            "$major.$minor.$patch${prerelease?.let { "-$it" } ?: ""}${buildmetadata?.let { "+$it" } ?: ""}"

        override fun compareTo(other: SemanticVersion): Int =
            compareValuesBy(this, other, SemanticVersion::major, SemanticVersion::minor, SemanticVersion::patch)
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

    fun String.toSemanticVersion(): SemanticVersion? =
        Regex("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$")
            .matchEntire(this)
            ?.let {
                SemanticVersion(
                    major = it.groupValues[1],
                    minor = it.groupValues[2],
                    patch = it.groupValues[3],
                    prerelease = it.groupValues[4].takeIf { it.isNotEmpty() },
                    buildmetadata = it.groupValues[5].takeIf { it.isNotEmpty() }
                )
            }

    fun Plugin.getSemanticVersion(): SemanticVersion? {
        return this.description.version.toSemanticVersion()
    }

}