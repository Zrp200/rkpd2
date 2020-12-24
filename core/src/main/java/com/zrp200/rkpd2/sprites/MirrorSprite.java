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

package com.zrp200.rkpd2.sprites;

import com.watabou.utils.RectF;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.npcs.AbstractMirrorImage;
import com.zrp200.rkpd2.actors.mobs.npcs.MirrorImage;
import com.watabou.noosa.TextureFilm;

public class MirrorSprite extends MobSprite {
	
	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;
	
	public MirrorSprite() {
		super();
		
		texture( Dungeon.hero.heroClass.spritesheet() );
		updateArmor( 0 );
		idle();
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		updateArmor( ((AbstractMirrorImage)ch).armTier );
	}
	
	public void updateArmor( int tier ) {
		CharSprite ref = Dungeon.hero.sprite;

		idle = ref.idle.clone();

		run = ref.run.clone();

		attack = ref.attack.clone();

		// a hack to get the first frame, which should be the first idle frame.
		die = ref.idle.clone();
		die.frames(ref.idle.frames[0]);
		die.delay = ref.die.delay;
		die.looped = ref.die.looped;
		
		idle();
	}
}
