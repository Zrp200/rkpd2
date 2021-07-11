package com.zrp200.rkpd2.actors.hero.abilities.rat_king;

import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.buffs.Paralysis;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.actors.hero.abilities.mage.ElementalBlast;
import com.zrp200.rkpd2.actors.hero.abilities.rogue.SmokeBomb;
import com.zrp200.rkpd2.actors.hero.abilities.warrior.Shockwave;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.items.armor.ClassArmor;
import com.zrp200.rkpd2.items.armor.HuntressArmor;
import com.zrp200.rkpd2.items.weapon.missiles.Shuriken;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.MissileSprite;
import com.zrp200.rkpd2.utils.GLog;

import java.util.HashMap;
import java.util.Map;

// here we fucking go. this is a legacy ability to correspond with the previous mechanics.

public class Wrath2 extends ArmorAbility {

    {
        baseChargeUse = 35;
    }

    @Override
    public String targetingPrompt() {
        return new SmokeBomb().targetingPrompt();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.AFTERSHOCK,Talent.RAT_BLAST,Talent.SMOKE_AND_MIRRORS,Talent.SEA_OF_BLADES};
    }

    private static final float JUMP_DELAY=1f;

    private ClassArmor armor;
    private Hero hero;
    private int target;
    private boolean[] stages;

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if(target == null) return;
        this.armor = armor;
        this.hero = hero;
        this.target = target;

        stages = new boolean[4]; // jump/molten/blades

        // smoke bomb -> shockwave aoe -> blast -> blades
        stages[0] = doSmokeBomb(); // smoke bomb
        // *technically* it should actually go nowhere, at +4 its range is 1.5 lol. but for the sake of sanity I'm making it 1/2/3/4/5
        doShockwave( ()->{
            doBlast();
            doSpectralBlades();
        });
    }

    private boolean doSmokeBomb() {
        if( !SmokeBomb.isValidTarget(hero, target) ) return false;

        SmokeBomb.blindAdjacentMobs(hero);
        SmokeBomb.throwSmokeBomb(hero, target);
        return true;
    }
    private void doShockwave(Callback next) {
        // TODO find a way to do "half" distance intervals, like rogue searching.
        Shockwave.activate(hero, hero.pos,
                360, 1 + hero.pointsInTalent(Talent.AFTERSHOCK),
                next);
    }
    private void doBlast() {
        ElementalBlast.activate(hero, false);
        hero.sprite.remove(CharSprite.State.INVISIBLE);
        hero.sprite.operate(hero.pos,()->{});
    }
    private void doSpectralBlades() {
        HashMap<Callback, Mob> targets = new HashMap<>();
        for (Mob mob : Dungeon.level.mobs) {
            if (Dungeon.level.distance(hero.pos, mob.pos) <= 12
                    && Dungeon.level.heroFOV[mob.pos]
                    && mob.alignment == Char.Alignment.ENEMY) {
                Callback callback = new Callback() {
                    @Override
                    public void call() {
                        hero.attack( targets.get( this ) );
                        Invisibility.dispel();
                        targets.remove( this );
                        if (targets.isEmpty()) finish();
                    }
                };
                targets.put( callback, mob );
            }
        }
        // this guarantees proper sequence of events for spectral blades
        if ( stages[3] = targets.size() > 0 ) {
            // turn towards the average point of all enemies being shot at.
            Point sum = new Point(); for(Mob mob : targets.values()) sum.offset(Dungeon.level.cellToPoint(mob.pos));
            sum.scale(1f/targets.size());
            // wait for user sprite to finish doing what it's doing, then start shooting.
            hero.sprite.doAfterAnim( () -> hero.sprite.zap(Dungeon.level.pointToCell(sum), ()->{
                Shuriken proto = new Shuriken();
                for(Map.Entry<Callback, Mob> entry : targets.entrySet())
                    ( (MissileSprite) hero.sprite.parent.recycle(MissileSprite.class) )
                            .reset( hero.sprite, entry.getValue().pos, proto, entry.getKey() );
            }));
            hero.busy();
        } else { // still need to finish, but also need to indicate that there was no enemies to shoot at.
            if( stages[1] ) Invisibility.dispel();
            else GLog.w( Messages.get(HuntressArmor.class, "no_enemies") );
            finish();
        }
    }

    private void finish() {
        hero.sprite.doAfterAnim(hero.sprite::idle); // because I overrode the default behavior I need to do this.

        int delay = 0;
        if(stages[1]) delay++;
        if(stages[2]) delay++;
        if(stages[3]) delay += hero.attackDelay();
        if(stages[0]) Actor.addDelayed(new Actor() {
            { actPriority = HERO_PRIO; } // this is basically the hero acting.
            @Override
            protected boolean act() {
                Buff.prolong(hero, Invisibility.class, Invisibility.DURATION/2f);
                remove(this);
                return true;
            }
        },(delay += JUMP_DELAY)-1);

        hero.spendAndNext(delay);
        for(boolean stage : stages) if(stage) { armor.charge -= chargeUse(hero); return; }
    }
}
