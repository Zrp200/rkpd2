package com.zrp200.rkpd2.items.armor;

import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Blindness;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.buffs.Paralysis;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.scrolls.ScrollOfTeleportation;
import com.zrp200.rkpd2.items.weapon.missiles.Shuriken;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.sprites.MissileSprite;
import com.zrp200.rkpd2.utils.BArray;
import com.zrp200.rkpd2.utils.GLog;

import java.util.HashMap;
import java.util.Map;

public class RatKingArmor extends ClassArmor{
    {
        // TODO add proper asset
        image = ItemSpriteSheet.CROWN;
    }
    private static final float JUMP_DELAY=2f;

    @Override
    public void doSpecial() {
        GameScene.selectCell( teleporter );
    }

    protected CellSelector.Listener teleporter = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {
            if(target == null) return;

            boolean[] stages = new boolean[3]; // jump/molten/blades

            if( stages[0] = target != curUser.pos ) {
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

                CellEmitter.get(curUser.pos).burst(Speck.factory(Speck.WOOL), 10);
                curUser.sprite.turnTo(curUser.pos, target); // jump from warrior leap
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
            if(stages[1] = MageArmor.doMoltenEarth()) {
                Dungeon.observe();
                curUser.sprite.remove(CharSprite.State.INVISIBLE); // you still benefit from initial invisibiilty, even if you can't see it visually.
                curUser.sprite.operate(curUser.pos,()->{}); // handled.
                MageArmor.playMoltenEarthFX();
            }
            // warrior. this is delayed so the burst burning damage doesn't cancel this out instantly.
            if(stages[0]) for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                Char mob = Actor.findChar(curUser.pos + PathFinder.NEIGHBOURS8[i]);
                if (mob != null && mob != curUser && mob.alignment != Char.Alignment.ALLY) {
                    Buff.prolong(mob, Paralysis.class, 5);
                }
            }
            // huntress
            HashMap<Callback, Mob> targets = new HashMap<>();
            for (Mob mob : Dungeon.level.mobs) {
                if (Dungeon.level.distance(curUser.pos, mob.pos) <= 12
                        && Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment == Char.Alignment.ENEMY) {
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            curUser.attack( targets.get( this ) );
                            Invisibility.dispel();
                            targets.remove( this );
                            if (targets.isEmpty()) finish(stages);
                        }
                    };
                    targets.put( callback, mob );
                }
            }
            // this guarentees proper sequence of events for spectral blades
            if ( stages[2] = targets.size() > 0 ) {
                // turn towards the average point of all enemies being shot at.
                Point sum = new Point(); for(Mob mob : targets.values()) sum.offset(Dungeon.level.cellToPoint(mob.pos));
                sum.scale(1f/targets.size());
                // wait for user sprite to finish doing what it's doing, then start shooting.
                curUser.sprite.doAfterAnim( () -> curUser.sprite.zap(Dungeon.level.pointToCell(sum), ()->{
                    Shuriken proto = new Shuriken();
                    for(Map.Entry<Callback, Mob> entry : targets.entrySet())
                        ( (MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class ) )
                            .reset( curUser.sprite, entry.getValue().pos, proto, entry.getKey() );
                }));
                curUser.busy();
            } else { // still need to finish, but also need to indicate that there was no enemies to shoot at.
                if( stages[1] ) Invisibility.dispel();
                else GLog.w( Messages.get(HuntressArmor.class, "no_enemies") );
                finish(stages);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(RogueArmor.class, "prompt");
        }
    };

    private void finish(boolean[] stages) {
       curUser.sprite.doAfterAnim(curUser.sprite::idle); // because I overrode the default behavior I need to do this.

        int delay = 0;
        if(stages[1]) delay++;
        if(stages[2]) delay += curUser.attackDelay();
        if(stages[0]) Actor.addDelayed(new Actor() {
            { actPriority = HERO_PRIO; } // this is basically the hero acting.
            @Override
            protected boolean act() {
                Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION/2f);
                remove(this);
                return true;
            }
        },(delay += JUMP_DELAY)-1);

        curUser.spendAndNext(delay);
        for(boolean stage : stages) if(stage) { useCharge(); break; }
    }

}
