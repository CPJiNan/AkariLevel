package com.github.cpjinan.plugin.playerlevel.internal.manager

import taboolib.common.platform.function.info

object DebugManager {

    /**
     * 输出调试信息方法
     */
    fun debugPrint(message:String?) {
        if (ConfigManager.options.getBoolean("debug")) info(message)
    }

    /**
     * 输出字符Logo方法
     */
    fun logoPrint() {
        info(" ______ _                       _                    _  ")
        info(" | ___ \' |                     | |                  | | ")
        info(" | |_/ / | __ _ _   _  ___ _ __| |     _____   _____| | ")
        info(" |  __/| |/ _` | | | |/ _ \' '__| |    / _ \' \' / / _ \' | ")
        info(" | |   | | (_| | |_| |  __/ |  | |___|  __/\' V /  __/ | ")
        info(" \'_|   |_|\'__,_|\'__, |\'___|_|  \'_____/\'___| \'_/ \'___|_| ")
        info("                 __/ |                                  ")
        info("                |___/                                   ")
    }

}