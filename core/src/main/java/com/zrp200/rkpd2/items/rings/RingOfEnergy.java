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

public class RingOfEnergy extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ENERGY;
	}

	public String statsInfo() {
		// manual implementation because custom message...
		int level = level();
		if(!isIdentified()) level(0);
		double[] effect = {1.2,1.15};
		String[] args = new String[2];
		for(int i=0; i < 2; i++)
			args[i] = new DecimalFormat("#.##").format(100*(Math.pow(effect[i],soloBuffedBonus())-1));
		level(level);
		return Messages.get(this, isIdentified()?"stats":"typical_stats", args);
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Energy();
	}
	
	public static float wandChargeMultiplier( Char target ){
		return (float)Math.pow(1.20, getBuffedBonus(target, Energy.class));
	}

	public static float artifactChargeMultiplier( Char target ){
		return (float)Math.pow(1.15, getBuffedBonus(target, Energy.class));
	}
	
	public class Energy extends RingBuff {
	}
}
