/**
 * AkariLevel
 *
 * 原版经验变更脚本。
 *
 * @author 季楠
 * @since 2025/8/18 17:28
 */

function onPluginEnable() {
    onPlayerExpChange();
}

function onPlayerExpChange() {
    new Listener(PlayerExpChangeEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var uniqueId = event.player.getUniqueId().toString()
                var amount = event.getAmount();

                // 为所有等级组增加此来源的经验。
                LevelGroup.getLevelGroups().values().forEach(
                    function (levelGroup) {
                        levelGroup.addMemberExp(uniqueId, amount, "VANILLA_EXP_CHANGE");
                    }
                )

                // 如果要取消原版经验变更事件，设置经验变化量为 0。
                event.setAmount(0);
            }
        ).register();
}