/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.zrp200.rkpd2.items.rings;

import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfAccuracy extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ACCURACY;
	}

	@Override
	protected float multiplier() {
		return 1.3f;
	}

	@Override
	protected RingBuff buff( ) {
		return new Accuracy();
	}
	
	public static float accuracyMultiplier( Char target ){
		return (float)Math.pow(1.3f, getBuffedBonus(target, Accuracy.class));
	}
	
	public class Accuracy extends RingBuff {
	}
}
