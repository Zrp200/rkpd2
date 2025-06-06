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

package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.ui.BuffIndicator;

public class LostInventory extends Buff {

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			if (target instanceof Hero && ((Hero) target).belongings != null){
				((Hero) target).belongings.lostInventory(true);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void detach() {
		super.detach();
		if (target instanceof Hero && ((Hero) target).belongings != null){
			((Hero) target).belongings.lostInventory(false);
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.NOINV;
	}

}
