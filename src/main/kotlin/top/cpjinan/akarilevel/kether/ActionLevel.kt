package top.cpjinan.akarilevel.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import top.cpjinan.akarilevel.level.LevelGroup

/**
 * AkariLevel
 * top.cpjinan.akarilevel.kether
 *
 * Kether 等级语句。
 *
 * @author 季楠
 * @since 2025/10/26 16:29
 */
object ActionLevel {
    @KetherParser(["get-level"], namespace = "akarilevel", shared = true)
    fun parserGetLevel() = combinationParser {
        it.group(text(), text()).apply(it) { member, levelGroup ->
            now {
                return@now LevelGroup.getLevelGroups()[levelGroup]?.getMemberLevel(member)
            }
        }
    }

    @KetherParser(["set-level"], namespace = "akarilevel", shared = true)
    fun parserSetLevel() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.setMemberLevel(member, amount, source)
                }
            }
    }

    @KetherParser(["add-level"], namespace = "akarilevel", shared = true)
    fun parserAddLevel() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.addMemberLevel(member, amount, source)
                }
            }
    }

    @KetherParser(["remove-level"], namespace = "akarilevel", shared = true)
    fun parserRemoveLevel() = combinationParser {
        it.group(text(), text(), long(), text())
            .apply(it) { member, levelGroup, amount, source ->
                now {
                    LevelGroup.getLevelGroups()[levelGroup]?.removeMemberLevel(member, amount, source)
                }
            }
    }
}