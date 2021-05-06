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
    private static final int BASE_PROC = 10*MULTIPLIER;
    // 1/10, 4/31, 5/32, 6/33 (1/5), 7/34 (4/13), 8/35, 9/36 etc.
    public boolean tryProc(Char attacker, int level) {
        return Random.Float() < procChanceMultiplier(attacker)*(MULTIPLIER+level)/(BASE_PROC+level);
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if( tryProc(attacker,weapon.buffedLvl()) ) new Bomb().explode(defender.pos);
        return damage;
    }
}
