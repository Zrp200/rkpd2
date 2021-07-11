package com.zrp200.rkpd2.actors.hero.abilities.rat_king;

import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.actors.hero.abilities.huntress.SpectralBlades;
import com.zrp200.rkpd2.actors.hero.abilities.mage.ElementalBlast;
import com.zrp200.rkpd2.actors.hero.abilities.rogue.SmokeBomb;
import com.zrp200.rkpd2.actors.hero.abilities.warrior.Shockwave;
import com.zrp200.rkpd2.items.armor.ClassArmor;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.mechanics.ConeAOE;
import com.zrp200.rkpd2.sprites.CharSprite;

import java.util.HashSet;

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
        if(target == hero.pos) stages[0] = false;
        else if(!doSmokeBomb()) return;
        else stages[0] = true;
        // smoke bomb should happen 'instantly'

        // *technically* it should actually go nowhere, at +4 its range is 1.5 lol. but for the sake of sanity I'm making it 1/2/3/4/5
        hero.sprite.doAfterAnim( () ->
                doShockwave( () ->
                        doBlast(this::doSpectralBlades)
                )
        );
    }

    private boolean doSmokeBomb() {
        if( !SmokeBomb.isValidTarget(hero, target) ) return false;

        SmokeBomb.blindAdjacentMobs(hero);
        SmokeBomb.throwSmokeBomb(hero, target);
        return true;
    }
    private void doShockwave(Callback next) {
        // TODO find a way to do "half" distance intervals, like rogue searching.
        stages[2] = true; // todo do I want to tie this to the jump?
        Shockwave.activate(hero, hero.pos,
                360, 1 + hero.pointsInTalent(Talent.AFTERSHOCK),
                next);
    }
    private void doBlast(Callback next) {
        stages[1] = ElementalBlast.activate(hero, next);
        hero.sprite.remove(CharSprite.State.INVISIBLE);
        hero.sprite.operate(hero.pos,()->{});
    }
    private void doSpectralBlades() { // fixme this code....aaaaaaaaaaaaaaaaaaaaaaa
        Ballistica b = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
        final HashSet<Char> targets = new HashSet<>();

        ConeAOE cone = new ConeAOE(b, 360);
        for (Ballistica ray : cone.rays) {
            Char toAdd = SpectralBlades.findChar(ray, hero,
                    2 * hero.pointsInTalent(Talent.SEA_OF_BLADES), targets);
            if (toAdd != null && toAdd.isAlive() && hero.fieldOfView[toAdd.pos]) {
                targets.add(toAdd);
            }
        }
        // 1x/1.5x/2x/2.5x/3x total effectiveness
        final float mult = Math.min(1,
                (1 + .5f * hero.pointsInTalent(Talent.SEA_OF_BLADES)) / targets.size());

        final HashSet<Callback> callbacks = new HashSet<>();

        stages[3] = !targets.isEmpty();
        if(!stages[3]) {
            finish();
            return;
        }
        // turn towards the average point of all enemies being shot at.
        Point sum = new Point();
        for (Char ch : targets) sum.offset(Dungeon.level.cellToPoint(ch.pos));
        sum.scale(1f / targets.size());
        hero.sprite.zap(Dungeon.level.pointToCell(sum), () -> {
            Callback onComplete = this::finish;
            for (Char ch : targets) SpectralBlades.shoot(
                    hero, ch,
                    mult, 1 + 1/4f * hero.pointsInTalent(Talent.SEA_OF_BLADES),
                    Talent.SEA_OF_BLADES, SpectralBladesTracker.class,
                    callbacks, onComplete);
            hero.busy();
        });
    }
    public static class SpectralBladesTracker extends Talent.SpiritBladesTracker {
        public int effectiveness;
    }

    private void finish() {
        // because I overrode the default behavior I need to do this.
        hero.sprite.doAfterAnim(() -> {
            Invisibility.dispel();
            hero.sprite.idle();
        });

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
