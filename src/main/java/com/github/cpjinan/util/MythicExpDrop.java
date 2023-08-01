package com.github.cpjinan.util;

import com.github.cpjinan.api.LevelAPI;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IItemDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;

public class MythicExpDrop extends Drop implements IItemDrop {
    public MythicExpDrop(String line, MythicLineConfig config) {
        super(line, config);
    }

    public AbstractItemStack getDrop(DropMetadata dropMetadata) {
        String[] args = this.getLine().split(" ");
        String value = args[1];
        String player = dropMetadata.getTrigger().asPlayer().getName();
        LevelAPI.INSTANCE.addPlayerExp(player, value);
        return null;
    }
}