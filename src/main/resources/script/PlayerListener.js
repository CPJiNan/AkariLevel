function onPluginEnable() {
    onPlayerJoin()
    onPlayerExpChange()
}

function onPlayerJoin() {
    new Listener(PlayerJoinEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var playerName = event.getPlayer().getName();

                // 设置等级组列表。
                var levelGroups = new java.util.ArrayList();
                // levelGroups.add("Example");

                // 自动加入等级组。
                levelGroups.forEach(
                    function (name) {
                        var group = LevelGroup.getLevelGroups()[name]
                        if (group !== null && !group.hasMember(playerName)) {
                            group.addMember(playerName, "AUTO_JOIN")
                        }
                    }
                );
            }
        ).register();
}

function onPlayerExpChange() {
    new Listener(PlayerExpChangeEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var playerName = event.getPlayer().getName();
                var amount = new java.lang.Long(event.amount);

                // 为所有等级组增加此来源的经验。
                LevelGroup.getLevelGroups().values().forEach(
                    function (levelGroup) {
                        levelGroup.addMemberExp(playerName, amount, "VANILLA_EXP_CHANGE")
                    }
                )

                // 如果要取消原版经验变更事件，设置经验变化量为 0。
                event.amount = 0
            }
        ).register();
}