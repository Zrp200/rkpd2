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

package com.zrp200.rkpd2.levels.traps;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.Heap;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.scrolls.ScrollOfTeleportation;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.utils.BArray;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WarpingTrap extends Trap {

	{
		color = TEAL;
		shape = STARS;
	}

	@Override
	public void activate() {
		CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		
		Char ch = Actor.findChar(pos);
		if (ch != null && !ch.flying) {
			if (ch instanceof Hero) {
				ScrollOfTeleportation.teleportHero((Hero) ch);
				BArray.setFalse(Dungeon.level.visited);
				BArray.setFalse(Dungeon.level.mapped);
				GameScene.updateFog();
				Dungeon.observe();
				
			} else {
				int count = 10;
				int pos;
				do {
					pos = Dungeon.level.randomRespawnCell( ch );
					if (count-- <= 0) {
						break;
					}
				} while (pos == -1);
				
				if (pos == -1 || Dungeon.bossLevel()) {
					
					GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
					
				} else {
					
					ch.pos = pos;
					if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).HUNTING) {
						((Mob) ch).state = ((Mob) ch).WANDERING;
					}
					ch.sprite.place(ch.pos);
					ch.sprite.visible = Dungeon.level.heroFOV[pos];
					
				}
			}
		}
		
		Heap heap = Dungeon.level.heaps.get(pos);
		
		if (heap != null){
			int cell = Dungeon.level.randomRespawnCell( null );
			
			Item item = heap.pickUp();
			
			if (cell != -1) {
				Dungeon.level.drop( item, cell );
			}
		}

	}
}
