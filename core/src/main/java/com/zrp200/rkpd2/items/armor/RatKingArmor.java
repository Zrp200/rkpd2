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

            PathFinder.buildDistanceMap(curUser.pos, BArray.not(Dungeon.level.solid, null), 8);

            if (PathFinder.distance[target] == Integer.MAX_VALUE ||
                    !Dungeon.level.heroFOV[target]) {
                GLog.w(Messages.get(RogueArmor.class, "fov"));
                return;
            }
            if(Actor.findChar(target) != null) { // use heroic leap mechanics instead.
                Ballistica route = new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET);
                //can't occupy the same cell as another char, so move back one until it is valid.
                int i = 0;
                while (Actor.findChar( target ) != null && target != curUser.pos)
                    target = route.path.get(route.dist-++i);
            }

            charge -= 35;
            updateQuickslot();
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (Dungeon.level.adjacent(mob.pos, curUser.pos) && mob.alignment != Char.Alignment.ALLY) {
                    Buff.prolong(mob, Blindness.class, Blindness.DURATION / 2f);
                    if (mob.state == mob.HUNTING) mob.state = mob.WANDERING;
                    mob.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 4);
                }
            }
            Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION/2f);
            CellEmitter.get(curUser.pos).burst(Speck.factory(Speck.WOOL), 10);
            ScrollOfTeleportation.appear(curUser, target);
            Sample.INSTANCE.play(Assets.Sounds.PUFF);
            Dungeon.level.occupyCell(curUser);
            // at least do warrior vfx now.
            CellEmitter.center(curUser.pos).burst(Speck.factory(Speck.DUST), 10);
            Camera.main.shake(2, 0.5f);
            Dungeon.observe();
            GameScene.updateFog();
            // now do mage
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment != Char.Alignment.ALLY) {
                    Buff.affect( mob, Burning.class ).reignite( mob );
                    Buff.prolong( mob, Roots.class, Roots.DURATION );
                    mob.damage(Random.NormalIntRange(4, 16 + Dungeon.depth), new Burning());
                }
            }

            curUser.sprite.operate( curUser.pos );
            //Invisibility.dispel();
            curUser.busy();

            curUser.sprite.emitter().start( ElmoParticle.FACTORY, 0.025f, 20 );
            Sample.INSTANCE.play( Assets.Sounds.BURNING );
            Sample.INSTANCE.play( Assets.Sounds.BURNING );
            Sample.INSTANCE.play( Assets.Sounds.BURNING );
            // warrior
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                Char mob = Actor.findChar(curUser.pos + PathFinder.NEIGHBOURS8[i]);
                if (mob != null && mob != curUser && mob.alignment != Char.Alignment.ALLY) {
                    Buff.prolong(mob, Paralysis.class, 5);
                }
            }
            Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION/2f);
            curUser.spend( Actor.TICK );
            // huntress
            Item proto = new Shuriken();
            for (Mob mob : Dungeon.level.mobs) {
                if (Dungeon.level.distance(curUser.pos, mob.pos) <= 12
                        && Dungeon.level.heroFOV[mob.pos]
                        && mob.alignment != Char.Alignment.ALLY) {
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            Invisibility.dispel();
                            curUser.attack( targets.get( this ) );
                            targets.remove( this );
                            if (targets.isEmpty()) {
                                //Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION/2f);
                                curUser.spendAndNext( curUser.attackDelay() );
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
            curUser.spendAndNext(Actor.TICK*3 ); // punishment
        }
        @Override
        public String prompt() {
            return Messages.get(RogueArmor.class, "prompt");
        }
    };

}
