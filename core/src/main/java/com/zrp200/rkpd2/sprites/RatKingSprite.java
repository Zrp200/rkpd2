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

package com.zrp200.rkpd2.sprites;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.abilities.Ratmogrify;
import com.zrp200.rkpd2.utils.Holiday;
import com.watabou.noosa.TextureFilm;

public class RatKingSprite extends MobSprite {

	public boolean festive;
	
	public RatKingSprite() {
		super();

		resetAnims();
	}

	public void resetAnims(){

		int c;
		switch (Holiday.getCurrentHoliday()){
			default:
				c = 0;
				break;
			case APRIL_FOOLS:
				c = 8;
				break;
			case WINTER_HOLIDAYS:
				c = 16;
				break;
		}

		if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify){
			c = 24;
			if (parent != null) aura(0xFFFF00);
		}

		texture( Assets.Sprites.RATKING );

		TextureFilm frames = new TextureFilm( texture, 16, 17 );

		idle = new Animation( 2, true );
		idle.frames( frames, c+0, c+0, c+0, c+1 );

		run = new Animation( 10, true );
		run.frames( frames, c+2, c+3, c+4, c+5, c+6 );

		attack = new Animation( 15, false );
		attack.frames( frames, c+0 );

		die = new Animation( 10, false );
		die.frames( frames, c+0 );

		play( idle );

	}


	@Override
	public void link(Char ch) {
		super.link(ch);
		if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify){
			aura(0xFFFF00);
		}
	}
}
