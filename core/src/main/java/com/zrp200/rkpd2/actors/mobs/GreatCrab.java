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

package com.zrp200.rkpd2.actors.mobs;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.spells.ClericSpell;
import com.zrp200.rkpd2.actors.mobs.npcs.Ghost;
import com.zrp200.rkpd2.items.food.MysteryMeat;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.GreatCrabSprite;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GreatCrab extends Crab {

	{
		spriteClass = GreatCrabSprite.class;

		HP = HT = 25;
		defenseSkill = 0; //see damage()
		baseSpeed = 1f;

		EXP = 6;

		WANDERING = new Wandering();
		state = WANDERING;

		loot = new MysteryMeat().quantity(2);
		lootChance = 1f;

		properties.add(Property.MINIBOSS);
	}

	private int moving = 0;

	@Override
	protected boolean getCloser( int target ) {
		//this is used so that the crab remains slower, but still detects the player at the expected rate.
		moving++;
		if (moving < 3) {
			return super.getCloser( target );
		} else {
			moving = 0;
			return true;
		}

	}

	@Override
	public void damage( int dmg, Object src ){
		//crab blocks all wand damage from the hero if it sees them.
		//Direct damage is negated, but add-on effects and environmental effects go through as normal.
		if (enemySeen
				&& state != SLEEPING
				&& paralysed == 0
				&& (src instanceof Wand || src instanceof ClericSpell)
				&& enemy == Dungeon.hero
				&& enemy.invisible == 0){
			GLog.n( Messages.get(this, "noticed") );
			sprite.showStatus( CharSprite.NEUTRAL, Messages.get(this, "def_verb") );
			Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
		} else {
			super.damage( dmg, src );
		}
	}

	@Override
	public int defenseSkill( Char enemy ) {
		//crab blocks all melee attacks from its current target
		if (enemySeen
				&& state != SLEEPING
				&& paralysed == 0
				&& enemy == this.enemy
				&& enemy.invisible == 0){
			if (sprite != null && sprite.visible) {
				Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
				GLog.n( Messages.get(this, "noticed") );
			}
			return INFINITE_EVASION;
		}
		return super.defenseSkill( enemy );
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );

		Ghost.Quest.process();
	}

	protected class Wandering extends Mob.Wandering{
		@Override
		protected int randomDestination() {
			//of two potential wander positions, picks the one closest to the hero
			int pos1 = super.randomDestination();
			int pos2 = super.randomDestination();
			PathFinder.buildDistanceMap(Dungeon.hero.pos, Dungeon.level.passable);
			if (PathFinder.distance[pos2] < PathFinder.distance[pos1]){
				return pos2;
			} else {
				return pos1;
			}
		}
	}
}
