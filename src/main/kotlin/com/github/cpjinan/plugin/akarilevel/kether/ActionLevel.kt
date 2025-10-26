package com.github.cpjinan.plugin.akarilevel.kether

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.kether
 *
 * Kether 等级语句。
 *
 * @author 季楠
 * @since 2025/10/26 16:29
 */
object ActionLevel {
    @KetherParser(["get-level"], namespace = "akarilevel")
    fun parserGetLevel() = combinationParser {
        it.group(text(), text()).apply(it) { member, levelGroup ->
            now {
                return@now LevelGroup.getLevelGroups()[levelGroup]?.getMemberLevel(member)
            }
        }
    }

    @KetherParser(["set-level"], namespace = "akarilevel")
    fun parserSetLevel() = combinationParser {
        it.group(text(), text(), long(), text().option().defaultsTo("KETHER_SET_LEVEL"))
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.setMemberLevel(member, amount, source)
                }
            }
    }

    @KetherParser(["add-level"], namespace = "akarilevel")
    fun parserAddLevel() = combinationParser {
        it.group(text(), text(), long(), text().option().defaultsTo("KETHER_ADD_LEVEL"))
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.addMemberLevel(member, amount, source)
                }
            }
    }

    @KetherParser(["remove-level"], namespace = "akarilevel")
    fun parserRemoveLevel() = combinationParser {
        it.group(text(), text(), long(), text().option().defaultsTo("KETHER_REMOVE_LEVEL"))
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.removeMemberLevel(member, amount, source)
                }
            }
    }
}