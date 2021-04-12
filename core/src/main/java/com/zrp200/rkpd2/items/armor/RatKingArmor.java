package com.zrp200.rkpd2.items.armor;

import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Blindness;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Burning;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.buffs.Paralysis;
import com.zrp200.rkpd2.actors.buffs.Roots;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.effects.particles.ElmoParticle;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.scrolls.ScrollOfTeleportation;
import com.zrp200.rkpd2.items.weapon.missiles.Shuriken;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.sprites.MissileSprite;
import com.zrp200.rkpd2.utils.BArray;
import com.zrp200.rkpd2.utils.GLog;

import java.util.HashMap;

public class RatKingArmor extends ClassArmor{
    {
        // TODO add proper asset
        image = ItemSpriteSheet.CROWN;
    }
    // rogue, then mage, then huntress, then warrior.
    private HashMap<Callback, Mob> targets = new HashMap<>();

    @Override
    public void doSpecial() {
        GameScene.selectCell( teleporter );
    }

    protected CellSelector.Listener teleporter = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {
            if(target == null) return;

            boolean[] stages = new boolean[3]; // jump/molten/blades

            stages[0] = target != curUser.pos;
            if(stages[0]) {
                PathFinder.buildDistanceMap(curUser.pos, BArray.not(Dungeon.level.solid, null), 8);

                if (PathFinder.distance[target] == Integer.MAX_VALUE ||
                        !Dungeon.level.heroFOV[target]) {
                    GLog.w(Messages.get(RogueArmor.class, "fov"));
                    return;
                }

                if (Actor.findChar(target) != null) { // use heroic leap mechanics instead.
                    Ballistica route = new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET);
                    //can't occupy the same cell as another char, so move back one until it is valid.
                    int i = 0;
                    while (Actor.findChar(target) != null && target != curUser.pos)
                        target = route.path.get(route.dist - ++i);
                }

                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (Dungeon.level.adjacent(mob.pos, curUser.pos) && mob.alignment != Char.Alignment.ALLY) {
                        Buff.prolong(mob, Blindness.class, Blindness.DURATION / 2f);
                        if (mob.state == mob.HUNTING) mob.state = mob.WANDERING;
                        mob.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 4);
                    }
                }
                Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION / 2f);
                CellEmitter.get(curUser.pos).burst(Speck.factory(Speck.WOOL), 10);
                ScrollOfTeleportation.appear(curUser, target);
                curUser.move(target); // impact from warrior leap.
                Sample.INSTANCE.play(Assets.Sounds.PUFF);
                Dungeon.level.occupyCell(curUser);
                // at least do warrior vfx now.
                Dungeon.observe();
                GameScene.updateFog();
                CellEmitter.center(curUser.pos).burst(Speck.factory(Speck.DUST), 10);
                Camera.main.shake(2, 0.5f);
            }
            // now do mage
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment != Char.Alignment.ALLY) {
                    stages[2] = true;
                    Buff.affect( mob, Burning.class ).reignite( mob );
                    Buff.prolong( mob, Roots.class, Roots.DURATION );
                    mob.damage(Random.NormalIntRange(4, 16 + Dungeon.depth), new Burning());
                }
            }

            if(stages[2]) {
                Dungeon.observe();
                curUser.sprite.operate(curUser.pos);
                //Invisibility.dispel();
                curUser.busy();

                curUser.sprite.emitter().start(ElmoParticle.FACTORY, 0.025f, 20);
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
            }
            // warrior. this is delayed so the burst burning damage doesn't cancel this out instantly.
            if(stages[0]) for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                Char mob = Actor.findChar(curUser.pos + PathFinder.NEIGHBOURS8[i]);
                if (mob != null && mob != curUser && mob.alignment != Char.Alignment.ALLY) {
                    Buff.prolong(mob, Paralysis.class, 5);
                }
            }
            //Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION/2f);
            //curUser.spend( Actor.TICK );
            // huntress
            Item proto = new Shuriken();
            for (Mob mob : Dungeon.level.mobs) {
                if (Dungeon.level.distance(curUser.pos, mob.pos) <= 12
                        && Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment != Char.Alignment.ALLY) {
                    stages[2] = true;
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            curUser.attack( targets.get( this ) );
                            Invisibility.dispel();
                            targets.remove( this );
                            if (targets.isEmpty()) {
                                int delay = 0;
                                if(stages[1]) delay++;
                                if(stages[2]) delay += curUser.attackDelay();
                                if(stages[0]) Actor.addDelayed(new Actor() {
                                        @Override
                                        protected boolean act() {
                                            Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION/2f);
                                            remove(this);
                                            return true;
                                        }
                                    }, ++delay);
                                curUser.spendAndNext(delay+1);
                                if(stages[0] || stages[1] || stages[2]) {
                                    charge -= 35;
                                    updateQuickslot();
                                }
                            }
                        }
                    };
                    ((MissileSprite)curUser.sprite.parent
                            .recycle( MissileSprite.class ))
                            .reset( curUser.sprite, mob.pos, proto, callback );

                    targets.put( callback, mob );
                }
            }
            if (targets.size() > 0) {
                curUser.sprite.zap(curUser.pos);
                curUser.busy();
            }
            // edge case where's there no enemies.. skips all the delay but then again...
            if(!stages[1] && !stages[2]) {
                GLog.w( Messages.get(HuntressArmor.class, "no_enemies") );
                if(stages[0]) {
                    curUser.spendAndNext(1);
                    charge -= 35;
                    updateQuickslot();
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(RogueArmor.class, "prompt");
        }
    };

}
