package com.zrp200.rkpd2.actors.mobs;

import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.effects.Pushing;
import com.zrp200.rkpd2.effects.Splash;
import com.zrp200.rkpd2.items.scrolls.ScrollOfUpgrade;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.SpawnerSprite;

import java.util.ArrayList;

public class AbyssalSpawner extends Mob {

    {
        spriteClass = AbyssalSpawnerSprite.class;

        HP = HT = 320;
        defenseSkill = 0;

        EXP = 50;
        maxLvl = 30;

        state = PASSIVE;

        loot = ScrollOfUpgrade.class;
        lootChance = 1f;

        properties.add(Property.IMMOVABLE);
        properties.add(Property.MINIBOSS);
        properties.add(Property.DEMONIC);
        properties.add(Property.INORGANIC);
        properties.add(Property.UNDEAD);
    }

    public static class AbyssalSpawnerSprite extends SpawnerSprite {
        public AbyssalSpawnerSprite() {
            super();
            hardlight(0x8f8f8f);
        }

        @Override
        public void die() {
            Splash.at( center(), Random.Int(0x000000, 0xFFFFFF), 100 );
            killAndErase();
        }
    }

    @Override
    public void beckon(int cell) {
        //do nothing
    }

    @Override
    public boolean reset() {
        return true;
    }

    private float spawnCooldown = 0;

    public boolean spawnRecorded = false;

    @Override
    protected boolean act() {

        spawnCooldown--;
        if (spawnCooldown <= 0){
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS8) {
                if (Dungeon.level.passable[pos+n] && Actor.findChar( pos+n ) == null) {
                    candidates.add( pos+n );
                }
            }

            if (!candidates.isEmpty()) {
                Mob spawn = Dungeon.level.createMob();

                spawn.pos = Random.element( candidates );
                spawn.state = spawn.HUNTING;

                Dungeon.level.occupyCell(spawn);

                GameScene.add( spawn, 1 );
                if (sprite.visible) {
                    Actor.addDelayed(new Pushing(spawn, pos, spawn.pos), -1);
                }

                spawnCooldown = Math.max(4, 100 - Dungeon.depth);
            }
        }
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        if (dmg >= 40){
            //takes 20/21/22/23/24/25/26/27/28/29/30 dmg
            // at   20/22/25/29/34/40/47/55/64/74/85 incoming dmg
            dmg = 40 + (int)(Math.sqrt(8*(dmg - 40) + 1) - 1)/2;
        }
        spawnCooldown -= dmg;
        super.damage(dmg, src);
    }

    public static final String SPAWN_COOLDOWN = "spawn_cooldown";
    public static final String SPAWN_RECORDED = "spawn_recorded";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SPAWN_COOLDOWN, spawnCooldown);
        bundle.put(SPAWN_RECORDED, spawnRecorded);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        spawnCooldown = bundle.getFloat(SPAWN_COOLDOWN);
        spawnRecorded = bundle.getBoolean(SPAWN_RECORDED);
    }

    {
        immunities.add( Paralysis.class );
        immunities.add( Amok.class );
        immunities.add( Sleep.class );
        immunities.add( Terror.class );
        immunities.add( Vertigo.class );
    }
}
