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

package com.zrp200.rkpd2.actors.mobs.npcs;

import com.watabou.noosa.Game;
import com.watabou.utils.Callback;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.GamesInProgress;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.items.Amulet;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.scenes.RankingsScene;
import com.zrp200.rkpd2.sprites.RatKingSprite;
import com.zrp200.rkpd2.windows.WndQuest;

public class RatKing extends NPC {

	{
		spriteClass = RatKingSprite.class;
		
		state = SLEEPING;
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public float speed() {
		return 2f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	@Override
	public boolean reset() {
		return true;
	}

	//***This functionality is for when rat king may be summoned by a distortion trap

	@Override
	protected void onAdd() {
		super.onAdd();
		if (Dungeon.depth != 5){
			yell(Messages.get(this, "confused"));
		}
	}

	@Override
	protected boolean act() {
		if (Dungeon.depth < 5){
			if (pos == Dungeon.level.exit){
				destroy();
				sprite.killAndErase();
			} else {
				target = Dungeon.level.exit;
			}
		} else if (Dungeon.depth > 5){
			if (pos == Dungeon.level.entrance){
				destroy();
				sprite.killAndErase();
			} else {
				target = Dungeon.level.entrance;
			}
		}
		return super.act();
	}

	//***

	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return super.interact(c);
		}

		if (Dungeon.hero.belongings.getItem(Amulet.class) != null) {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(RatKing.this, Messages.get(RatKing.this,"amulet")){
						@Override
						public void hide() {
							Dungeon.win(RatKing.class);
							Dungeon.deleteGame( GamesInProgress.curSlot, true );
							Game.switchScene(RankingsScene.class);
							super.hide();
						}
					});
				}
			});
			return true;
		}

		if (state == SLEEPING) {
			notice();
			yell( Messages.get(this, "not_sleeping") );
			state = WANDERING;
		} else {
			yell( Messages.get(this, "what_is_it") );
		}
		return true;
	}
	
	@Override
	public String description() {
		return ((RatKingSprite)sprite).festive ?
				Messages.get(this, "desc_festive")
				: super.description();
	}
}
