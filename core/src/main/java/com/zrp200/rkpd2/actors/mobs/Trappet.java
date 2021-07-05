package com.zrp200.rkpd2.actors.mobs;

import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Light;
import com.zrp200.rkpd2.items.Gold;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.levels.traps.*;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.utils.GLog;

import java.util.ArrayList;

public class Trappet extends AbyssalMob implements Callback {

    private static final float TIME_TO_ZAP	= 1f;

    {
        spriteClass = TrappetSprite.class;

        HP = HT = 125;
        defenseSkill = 36;
        viewDistance = Light.DISTANCE;

        EXP = 20;
        maxLvl = 30;

        loot = new Gold();
        lootChance = 0.45f;

        properties.add(Property.DEMONIC);
        properties.add(Property.UNDEAD);
    }

    @Override
    public int attackSkill( Char target ) {
        return 34 + abyssLevel();
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0 + abyssLevel()*3, 7 + abyssLevel()*11);
    }

    @Override
    public boolean canAttack(Char enemy) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    protected boolean doAttack( Char enemy ) {
        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap( enemy.pos );
            return false;
        } else {
            zap();
            return true;
        }
    }

    protected Class[] traps = new Class[]{
            FrostTrap.class, StormTrap.class, CorrosionTrap.class,  DisintegrationTrap.class,
            RockfallTrap.class, FlashingTrap.class, GuardianTrap.class,
            DisarmingTrap.class,
            WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class };

    private void zap() {
        spend( TIME_TO_ZAP );

        if (hit( this, enemy, true )) {
            ArrayList<Integer> points = Level.getSpawningPoints(enemy.pos);
            if (!points.isEmpty()){
                Trap t = ((Trap) Reflection.newInstance(Random.element(traps)));
                Dungeon.level.setTrap(t, Random.element(points));
                Dungeon.level.map[t.pos] = t.visible ? Terrain.TRAP : Terrain.SECRET_TRAP;
                t.reveal();
            } else {
                Trap t = ((Trap)Reflection.newInstance(Random.element(traps)));
                Dungeon.level.setTrap(t, enemy.pos);
                t.activate();
            }

            if (enemy == Dungeon.hero && !enemy.isAlive()) {
                Dungeon.fail( getClass() );
                GLog.n( Messages.get(this, "bolt_kill") );
            }
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public void call() {
        next();
    }
}
