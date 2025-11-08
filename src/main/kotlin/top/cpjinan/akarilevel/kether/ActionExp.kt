package top.cpjinan.akarilevel.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import top.cpjinan.akarilevel.level.LevelGroup

/**
 * AkariLevel
 * top.cpjinan.akarilevel.kether
 *
 * Kether 经验语句。
 *
 * @author 季楠
 * @since 2025/10/26 16:29
 */
object ActionExp {
    @KetherParser(["get-exp"], namespace = "akarilevel", shared = true)
    fun parserGetExp() = combinationParser {
        it.group(text(), text()).apply(it) { member, levelGroup ->
            now {
                return@now LevelGroup.getLevelGroups()[levelGroup]?.getMemberExp(member)
            }
        }
    }

    @KetherParser(["set-exp"], namespace = "akarilevel", shared = true)
    fun parserSetExp() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.setMemberExp(member, amount, source)
                }
            }
    }

    @KetherParser(["add-exp"], namespace = "akarilevel", shared = true)
    fun parserAddExp() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.addMemberExp(member, amount, source)
                }
            }
    }

    @KetherParser(["remove-exp"], namespace = "akarilevel", shared = true)
    fun parserRemoveExp() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.removeMemberExp(member, amount, source)
                }
            }
    }
}