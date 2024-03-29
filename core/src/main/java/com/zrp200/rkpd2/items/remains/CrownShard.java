package com.zrp200.rkpd2.items.remains;

import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class CrownShard extends RemainsItem {

    {
        image = ItemSpriteSheet.BROKEN_CROWN;
    }

    @Override
    protected void doEffect(Hero hero) {
        // super innovative
        for (RemainsItem item : new RemainsItem[]{new SealShard(), new BrokenStaff(), new BowFragment(), new BrokenHilt()}) {
            item.doEffect(hero);
        }
    }
}