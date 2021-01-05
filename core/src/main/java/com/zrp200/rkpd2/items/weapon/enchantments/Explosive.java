package com.zrp200.rkpd2.items.weapon.enchantments;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.items.bombs.Bomb;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.sprites.ItemSprite;

// huntress bow exclusive enchant. I'm sure everyone's going to love this one.
public class Explosive extends Weapon.Enchantment {
    // placeholder color
    private static final ItemSprite.Glowing REDORANGE = new ItemSprite.Glowing( 0xFF5349 );
    @Override
    public ItemSprite.Glowing glowing() {
        return REDORANGE;
    }

    private static final int MULTIPLIER = 3; // upgrades are 1/3 effective, this enchantment scales like crazy even with this.
    private static final int BASE_PROC = 6*MULTIPLIER;
    public static boolean tryProc(int level) {
        return Random.Int(BASE_PROC+level) >= BASE_PROC-MULTIPLIER;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if( tryProc(weapon.buffedLvl()) ) new Bomb().explode(defender.pos);
        return damage;
    }
}
