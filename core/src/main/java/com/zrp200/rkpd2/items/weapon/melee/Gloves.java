/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class Gloves extends Sai {

	{
		image = ItemSpriteSheet.GLOVES;
		hitSound = Assets.Sounds.HIT;

		tier = 1;
		
		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) +     //6 base, down from 10
				lvl*Math.round(0.5f*(tier+1));  //+1 per level, down from +2
	}

}
