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

package com.zrp200.rkpd2.actors.hero.spells;

import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.items.bags.Bag;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.windows.WndBag;

public abstract class InventoryClericSpell extends ClericSpell {

	@Override
	public void onCast(HolyTome tome, Hero hero) {
		GameScene.selectItem(new WndBag.ItemSelector() {

			@Override
			public String textPrompt() {
				return inventoryPrompt();
			}

			@Override
			public Class<? extends Bag> preferredBag() {
				return InventoryClericSpell.this.preferredBag();
			}

			@Override
			public boolean itemSelectable(Item item) {
				return usableOnItem(item);
			}

			@Override
			public void onSelect(Item item) {
				onItemSelected(tome, hero, item);
			}
		});
	}

	protected String inventoryPrompt(){
		return Messages.get(this, "prompt");
	}

	protected Class<? extends Bag> preferredBag() {
		return null; //defaults to no preference
	}

	protected boolean usableOnItem( Item item ){
		return true;
	}

	protected abstract void onItemSelected( HolyTome tome, Hero hero, Item item );

}
