package com.github.cpjinan.plugin.akarilevel.kether

import com.github.cpjinan.plugin.akarilevel.level.LevelGroup
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.kether
 *
 * Kether 经验语句。
 *
 * @author 季楠
 * @since 2025/10/26 16:29
 */
object ActionExp {
    @KetherParser(["get-exp"], namespace = "akarilevel")
    fun parserGetExp() = combinationParser {
        it.group(text(), text()).apply(it) { member, levelGroup ->
            now {
                return@now LevelGroup.getLevelGroups()[levelGroup]?.getMemberExp(member)
            }
        }
    }

    @KetherParser(["set-exp"], namespace = "akarilevel")
    fun parserSetExp() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.setMemberExp(member, amount, source)
                }
            }
    }

    @KetherParser(["add-exp"], namespace = "akarilevel")
    fun parserAddExp() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.addMemberLevel(member, amount, source)
                }
            }
    }

    @KetherParser(["remove-exp"], namespace = "akarilevel")
    fun parserRemoveExp() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.removeMemberLevel(member, amount, source)
                }
            }
    }
}