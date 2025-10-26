package com.github.cpjinan.plugin.akarilevel.kether

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.kether
 *
 * Kether 等级组语句。
 *
 * @author 季楠
 * @since 2025/10/26 21:54
 */
object ActionLevelGroup {
    @KetherParser(["add-member"], namespace = "akarilevel")
    fun parserAddMember() = combinationParser {
        it.group(text(), text(), text())
            .apply(it) { member, levelGroup, source ->
                now {
                    return@now LevelGroup.getLevelGroups()[levelGroup]?.addMember(member, source)
                }
            }
    }

    @KetherParser(["remove-member"], namespace = "akarilevel")
    fun parserRemoveMember() = combinationParser {
        it.group(text(), text(), text())
            .apply(it) { member, levelGroup, source ->
                now {
                    return@now LevelGroup.getLevelGroups()[levelGroup]?.removeMember(member, source)
                }
            }
    }
}