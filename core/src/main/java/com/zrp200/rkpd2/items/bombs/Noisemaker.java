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

package com.zrp200.rkpd2.items.bombs;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.Heap;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Noisemaker extends Bomb {
	
	{
		image = ItemSpriteSheet.NOISEMAKER;
	}

	public void setTrigger(int cell){

		Buff.affect(Dungeon.hero, Trigger.class).set(cell);

		CellEmitter.center( cell ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		Sample.INSTANCE.play( Assets.Sounds.ALERT );

		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			mob.beckon( cell );
		}

	}
	
	public static class Trigger extends Buff {

		int cell;
		int floor;
		int left;
		
		public void set(int cell){
			floor = Dungeon.depth;
			this.cell = cell;
			left = 6;
		}
		
		@Override
		public boolean act() {

			if (Dungeon.depth != floor){
				spend(TICK);
				return true;
			}

			Noisemaker bomb = null;
			Heap heap = Dungeon.level.heaps.get(cell);

			if (heap != null){
				for (Item i : heap.items){
					if (i instanceof Noisemaker){
						bomb = (Noisemaker) i;
						break;
					}
				}
			}

			if (bomb == null) {
				detach();

			} else if (Actor.findChar(cell) != null)  {

				heap.items.remove(bomb);
				if (heap.items.isEmpty()) {
					heap.destroy();
				}

				detach();
				bomb.explode(cell);

			} else {
				spend(TICK);

				left--;

				if (left <= 0){
					CellEmitter.center( cell ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
					Sample.INSTANCE.play( Assets.Sounds.ALERT );

					for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
						mob.beckon( cell );
					}
					left = 6;
				}

			}

			return true;
		}

		private static final String CELL = "cell";
		private static final String FLOOR = "floor";
		private static final String LEFT = "left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CELL, cell);
			bundle.put(FLOOR, floor);
			bundle.put(LEFT, left);
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			cell = bundle.getInt(CELL);
			floor = bundle.getInt(FLOOR);
			left = bundle.getInt(LEFT);
		}
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 40);
	}
}
