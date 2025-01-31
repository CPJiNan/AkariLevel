function pluginEnable() {
    playerLevelChangeListener()
}

function playerLevelChangeListener() {
    new Listener(PlayerExpChangeEvent.class)
        .setPriority(EventPriority.NORMAL)
        .setExecutor(function (event) {
            var player = event.getPlayer();
            var levelGroup = event.getLevelGroup();
            var expAmount = event.getExpAmount();
            // var source = event.getSource();

            var playerLevel = PlayerAPI.getPlayerLevel(player, levelGroup);
            var maxLevel = LevelAPI.getLevelGroupData(levelGroup).getMaxLevel();

            if (playerLevel === maxLevel || expAmount <= 0) return;

            player.sendMessage("§7Exp §f+" + expAmount + " §7.");
        })
        .register();
}