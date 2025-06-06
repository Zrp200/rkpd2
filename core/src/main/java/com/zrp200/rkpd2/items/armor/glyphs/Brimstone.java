/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.rkpd2.items.armor.glyphs;

import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.ShieldBuff;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.items.armor.Armor;
import com.zrp200.rkpd2.sprites.ItemSprite;

public class Brimstone extends Armor.Glyph {

	private static ItemSprite.Glowing ORANGE = new ItemSprite.Glowing( 0xFF4400 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, triggers in Char.isImmune
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return ORANGE;
	}

	//pre-0.7.4 saves
	public static class BrimstoneShield extends ShieldBuff {

		{
			type = buffType.POSITIVE;
		}

		@Override
		public boolean act() {
			Hero hero = (Hero)target;

			if (hero.belongings.armor == null || !hero.belongings.armor.hasGlyph(Brimstone.class, hero)) {
				detach();
				return true;
			}

			if (shielding() > 0){
				decShield();

				//shield decays at a rate of 1 per turn.
				spend(TICK);
			} else {
				detach();
			}

			return true;
		}

	}

}
