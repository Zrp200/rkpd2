package com.zrp200.rkpd2.items.wands;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.blobs.Fire;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Burning;
import com.zrp200.rkpd2.effects.MagicMissile;
import com.zrp200.rkpd2.effects.particles.FlameParticle;
import com.zrp200.rkpd2.items.Heap;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.enchantments.Blazing;
import com.zrp200.rkpd2.items.weapon.melee.MagesStaff;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class WandOfFirebolt extends DamageWand {
    {
        image = ItemSpriteSheet.WAND_FIREBOLT;
    }

    @Override
    public int min(int lvl) {
        return 1+lvl;
    }

    @Override
    public int max(int lvl) {
        return 5+8*lvl;
    }

    @Override
    protected void onZap(Ballistica attack) {
        for(int cell : attack.subPath(1,attack.dist-1)) {
            if(Dungeon.level.flamable[cell]) GameScene.add(Fire.seed(cell,1,Fire.class));
            Heap heap = Dungeon.level.heaps.get(cell);
            if(heap != null) heap.burn();
        }
        Char ch = Actor.findChar(attack.collisionPos);
        if(ch != null) {
            int dmg = damageRoll();
            processSoulMark(ch,1,damageRoll());
            ch.damage(dmg, new WandOfFireblast()); // should share resists with wand of fireblast.
            if(ch.isAlive()) Buff.affect(ch, Burning.class).reignite(ch);
            ch.sprite.emitter().burst(FlameParticle.FACTORY, 5);
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.FIRE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    @Override
    public void onHit(Weapon staff, Char attacker, Char defender, int damage) {
        // pre-rework blazing ;)
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        int level = Math.max(0,staff.buffedLvl());
        if (Random.Float() < (level+1f)/(level+3f)* Weapon.Enchantment.procChanceMultiplier(attacker)) {

            if (Random.Int( 2 ) == 0) {
                Buff.affect( defender, Burning.class ).reignite( defender );
            }
            defender.damage( Random.Int( 1, level+2 ), new Blazing() );

            defender.sprite.emitter().burst( FlameParticle.FACTORY, level + 1 );

        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color( 0xEE7722 );
        particle.am = 0.5f;
        particle.setLifespan(0.6f);
        particle.acc.set(0, -40);
        particle.setSize( 0f, 3f);
        particle.shuffleXY( 1.5f );
    }
}
