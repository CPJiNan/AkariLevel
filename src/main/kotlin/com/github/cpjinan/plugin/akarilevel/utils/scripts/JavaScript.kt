package com.github.cpjinan.plugin.akarilevel.utils.scripts

import taboolib.common5.compileJS
import taboolib.common5.util.replace

object JavaScript {
    fun String.evalJS(
        args: Map<String, Any>? = null
    ): Any? {
        return eval(this, args)
    }

    private fun eval(
        script: String,
        args: Map<String, Any>? = null,
    ): Any? {
        var scriptList = script
        args?.forEach { (k, v) -> scriptList = scriptList.replace(Pair(k, v)) }
        return scriptList.compileJS()?.eval()
    }
}