/**
 * AkariLevel
 *
 * 队伍经验共享脚本。
 *
 * @author 季楠
 * @since 2025/8/30 22:18
 */

function onPluginEnable() {
    onMemberExpChange();
}

// 导入类。
var DungeonPlus = org.serverct.ersha.dungeon.DungeonPlus;

// 启用队伍经验共享。
var enable = false;
// 经验来源。
var sources = ["MYTHICMOBS_DROP_EXP", "VANILLA_EXP_CHANGE"];
// 队长经验权重。
var leaderWeight = 1
// 成员经验权重。
var memberWeight = 1

function onMemberExpChange() {
    new Listener(MemberExpChangeEvent.class)
        .setExecutor(
            function (event) {
                if (!enable) return;

                // 获取事件参数。
                var member = event.getMember();
                var levelGroupName = event.getLevelGroup();
                var expAmount = event.getExpAmount();
                var source = event.getSource();

                // 队伍经验共享。
                var offlinePlayer = Bukkit.getOfflinePlayer(member);
                if (offlinePlayer.isOnline() && expAmount > 0 && sources.indexOf(source) !== -1) {
                    var player = offlinePlayer.getPlayer();
                    var levelGroup = LevelGroup.getLevelGroups()[levelGroupName];

                    var team = DungeonPlus.getTeamManager().getTeam(player);
                    if (team === null) return;
                    var totalAmount = expAmount * team.getPlayers().length;
                    var totalWeight = leaderWeight + memberWeight * (team.getPlayers().length - 1)
                    var leaderAmount = Math.floor(totalAmount * (leaderWeight / totalWeight))
                    var memberAmount = Math.floor(totalAmount * (memberWeight / totalWeight))

                    team.getPlayers().forEach(
                        function (uuid) {
                            if (uuid === player.getUniqueId()) {
                                if (uuid === team.getLeader()) event.setExpAmount(leaderAmount);
                                else event.setExpAmount(memberAmount);
                            } else {
                                var name = Bukkit.getOfflinePlayer(uuid).getName();
                                if (uuid === team.getLeader()) levelGroup.addMemberExp(name, leaderAmount, "TEAM_SHARE_EXP");
                                else levelGroup.addMemberExp(name, memberAmount, "TEAM_SHARE_EXP");
                            }
                        }
                    )

                }
            }
        ).register();
}