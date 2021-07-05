package com.zrp200.rkpd2.actors.mobs;

import com.watabou.utils.Callback;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.MagicMissile;
import com.zrp200.rkpd2.effects.particles.RainbowParticle;
import com.zrp200.rkpd2.levels.traps.SummoningTrap;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.sprites.CharSprite;

import java.util.HashSet;

public class LostSpirit extends AbyssalMob implements Callback {

    {
        HP = HT = 145;
        defenseSkill = 72;
        spriteClass = LostSpiritSprite.class;

        EXP = 40;
        maxLvl = 30;

        flying = true;
        properties.add(Property.BOSS);
        properties.add(Property.DEMONIC);
        properties.add(Property.UNDEAD);
    }

    @Override
    public boolean canAttack(Char enemy) {
        return new Ballistica( pos, enemy.pos, Ballistica.STOP_TARGET).collisionPos == enemy.pos;
    }

    @Override
    public int attackSkill( Char target ) {
        return 70 + abyssLevel()*3;
    }

    @Override
    public void damage(int dmg, Object src) {
        int distance = Dungeon.level.distance(this.pos, Dungeon.hero.pos) - 1;
        float multiplier = Math.min(0.2f, 1 / (1.32f * (float)Math.pow(1.2f, distance)));
        dmg = Math.round(dmg * multiplier);
        super.damage(dmg, src);
    }

    protected boolean doAttack(Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos ) && enemy == Dungeon.hero) {

            if (HP > HT/10) {
                //do nothing
            }
            else {
                CellEmitter.bottom(pos).burst(MagicMissile.WhiteParticle.FACTORY, 20);
                new SummoningTrap().set( pos ).activate();
                pos = Dungeon.level.randomRespawnCell(this);
            }
            spend(attackDelay());
            return true;

        } else {

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }
        }
    }

    private void zap() {
        spend( 1f );

        if (hit( this, enemy, true )) {

            if (enemy.alignment == Alignment.ENEMY){
                ChampionEnemy.rollForChampionInstantly((Mob) enemy);
                enemy.sprite.emitter().burst( RainbowParticle.BURST, 10);
                enemy = null;
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

    {
        immunities.add(Charm.class);
        immunities.add(Terror.class);
        immunities.add(Amok.class);
        immunities.add(Sleep.class);
    }

    public Char chooseEnemy() {

        //find a new enemy if..
        boolean newEnemy = false;
        //we have no enemy, or the current one is dead/missing
        if (enemy != null && enemy.buff(ChampionEnemy.class) != null){
            newEnemy = true;
        }
        else if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
            newEnemy = true;
        }
        else if (enemy == Dungeon.hero && !Dungeon.level.adjacent(pos, enemy.pos)){
            newEnemy = true;
        }

        if ( newEnemy ) {

            HashSet<Char> enemies = new HashSet<>();

            if (Dungeon.level.adjacent(pos, Dungeon.hero.pos)) {
                return Dungeon.hero;
            }
            //look for ally mobs to attack, ignoring the soul flame
            for (Mob mob :  Dungeon.level.mobs.toArray(new Mob[0]))
                if (mob.alignment == Alignment.ENEMY && canSee(mob.pos) && mob != this && mob.buff(ChampionEnemy.class) == null)
                    enemies.add(mob);

            return chooseClosest(enemies);

        } else
            return enemy;
    }

    protected Char chooseClosest(HashSet<Char> enemies){
        Char closest = null;

        for (Char curr : enemies){
            if (closest == null
                    || Dungeon.level.distance(pos, curr.pos) < Dungeon.level.distance(pos, closest.pos)
                    || Dungeon.level.distance(pos, curr.pos) == Dungeon.level.distance(pos, closest.pos)){
                closest = curr;
            }
        }
        return closest;
    }


}
