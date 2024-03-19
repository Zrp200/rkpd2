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

package com.zrp200.rkpd2.items.weapon.melee;

import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class AssassinsBlade extends Dirk {

	{
		image = ItemSpriteSheet.ASSASSINS_BLADE;
		hitSoundPitch = 0.9f;

		tier = 4;

		//20 base, down from 25
		//scaling unchanged

		//deals 50% toward max to max on surprise, instead of min to max.
		surpriseTowardMax = 0.50f;
	}@Override protected int maxDist() { return 4; }
}