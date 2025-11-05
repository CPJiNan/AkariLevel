package top.cpjinan.akarilevel.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import top.cpjinan.akarilevel.level.LevelGroup

/**
 * AkariLevel
 * top.cpjinan.akarilevel.kether
 *
 * Kether 等级组语句。
 *
 * @author 季楠
 * @since 2025/10/26 21:54
 */
object ActionLevelGroup {
    @KetherParser(["add-member"], namespace = "akarilevel", shared = true)
    fun parserAddMember() = combinationParser {
        it.group(text(), text(), text())
            .apply(it) { member, levelGroup, source ->
                now {
                    return@now LevelGroup.getLevelGroups()[levelGroup]?.addMember(member, source)
                }
            }
    }

    @KetherParser(["remove-member"], namespace = "akarilevel", shared = true)
    fun parserRemoveMember() = combinationParser {
        it.group(text(), text(), text())
            .apply(it) { member, levelGroup, source ->
                now {
                    return@now LevelGroup.getLevelGroups()[levelGroup]?.removeMember(member, source)
                }
            }
    }
}