/**
 * AkariLevel
 *
 * 追踪等级组脚本。
 *
 * @author 季楠
 * @since 2025/8/19 17:47
 */

function onPluginEnable() {
    onMemberLevelChange();
    onMemberExpChange();
}

// 设置追踪等级组。
var levelGroupName = "";

function onMemberLevelChange() {
    new Listener(MemberLevelChangeEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var member = event.getMember();

                // 刷新原版经验条。
                var offlinePlayer = Bukkit.getOfflinePlayer(member);
                if (offlinePlayer.isOnline() && levelGroupName !== "") {
                    var player = offlinePlayer.getPlayer();
                    var levelGroup = LevelGroup.getLevelGroups()[levelGroupName];
                    var currentLevel = levelGroup.getMemberLevel(member);
                    var currentExp = levelGroup.getMemberExp(member);
                    var nextLevelExp = levelGroup.getLevelExp(member, currentLevel, currentLevel + 1);

                    player.setLevel(Math.min(currentLevel, 2147483647));
                    player.setExp(Math.min(Math.max(currentExp / nextLevelExp, 0), 0.99));
                }
            }
        ).register();
}

function onMemberExpChange() {
    new Listener(MemberExpChangeEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var member = event.getMember();

                // 刷新原版经验条。
                var offlinePlayer = Bukkit.getOfflinePlayer(member);
                if (offlinePlayer.isOnline() && levelGroupName !== "") {
                    var player = offlinePlayer.getPlayer();
                    var levelGroup = LevelGroup.getLevelGroups()[levelGroupName];
                    var currentLevel = levelGroup.getMemberLevel(member);
                    var currentExp = levelGroup.getMemberExp(member);
                    var nextLevelExp = levelGroup.getLevelExp(member, currentLevel, currentLevel + 1);

                    player.setLevel(Math.min(currentLevel, 2147483647));
                    player.setExp(Math.min(Math.max(currentExp / nextLevelExp, 0), 0.99));
                }
            }
        ).register();
}