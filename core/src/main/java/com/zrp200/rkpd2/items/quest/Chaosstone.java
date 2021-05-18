package com.zrp200.rkpd2.items.quest;

import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class Chaosstone extends Item {

    {
        image = ItemSpriteSheet.CHAOSSTONE;
        stackable = true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return quantity * 1000;
    }
}
