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
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.mobs.Eye;
import com.zrp200.rkpd2.effects.Beam;
import com.zrp200.rkpd2.effects.MagicMissile;
import com.zrp200.rkpd2.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class EyeSprite extends MobSprite {

	private int zapPos;

	private Animation charging;
	private Emitter chargeParticles;
	
	public EyeSprite() {
		super();
		
		texture( Assets.Sprites.EYE );
		
		TextureFilm frames = new TextureFilm( texture, 16, 18 );
		
		idle = new Animation( 8, true );
		idle.frames( frames, 0, 1, 2 );

		charging = new Animation( 12, true);
		charging.frames( frames, 3, 4 );
		
		run = new Animation( 12, true );
		run.frames( frames, 5, 6 );
		
		attack = new Animation( 8, false );
		attack.frames( frames, 4, 3 );
		zap = attack.clone();
		
		die = new Animation( 8, false );
		die.frames( frames, 7, 8, 9 );
		
		play( idle );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		
		chargeParticles = centerEmitter();
		chargeParticles.autoKill = false;
		chargeParticles.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
		chargeParticles.on = false;
		
		if (((Eye)ch).beamCharged) play(charging);
	}

	@Override
	public void update() {
		super.update();
		if (chargeParticles != null){
			chargeParticles.pos( center() );
			chargeParticles.visible = visible;
		}
	}

	@Override
	public void die() {
		super.die();
		if (chargeParticles != null){
			chargeParticles.on = false;
		}
	}

	@Override
	public void kill() {
		super.kill();
		if (chargeParticles != null){
			chargeParticles.killAndErase();
		}
	}

	public void charge( int pos ){
		turnTo(ch.pos, pos);
		play(charging);
		if (visible) Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
	}

	@Override
	public void play(Animation anim) {
		if (chargeParticles != null) chargeParticles.on = anim == charging;
		super.play(anim);
	}

	@Override
	public void zap( int pos ) {
		zapPos = pos;
		super.zap( pos );
	}
	
	@Override
	public void onComplete( Animation anim ) {
		super.onComplete( anim );
		
		if (anim == zap) {
			idle();
			parent.add(deathGaze(this, zapPos));
			((Eye)ch).deathGaze();
			ch.next();
		} else if (anim == die){
			chargeParticles.killAndErase();
		}
	}

	public static Beam.DeathRay deathGaze(CharSprite src, int zapPos) {
		Char ch = Actor.findChar(zapPos);
		if (ch != null){
			return new Beam.DeathRay(src.center(), ch.sprite.center());
		} else {
			return new Beam.DeathRay(src.center(), DungeonTilemap.raisedTileCenterToWorld(zapPos));
		}
	}
}
