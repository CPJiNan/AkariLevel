function pluginEnable() {
    playerLevelChangeListener()
}

function playerLevelChangeListener() {
    new Listener(PlayerExpChangeEvent.class)
        .setPriority(EventPriority.NORMAL)
        .setExecutor(function (event) {
            const player = event.getPlayer();
            const levelGroup = event.getLevelGroup();
            const expAmount = event.getExpAmount();
            // const source = event.getSource();

            const playerLevel = PlayerAPI.getPlayerLevel(player, levelGroup);
            const maxLevel = LevelAPI.getLevelGroupData(levelGroup).getMaxLevel();

            if (playerLevel === maxLevel || expAmount <= 0) return;

            player.sendMessage("§7Exp §f+" + expAmount + " §7.");
        })
        .register();
}