/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.zrp200.rkpd2.items.scrolls.exotic;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.hero.Belongings;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.Enchanting;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.Armor;
import com.zrp200.rkpd2.items.bags.Bag;
import com.zrp200.rkpd2.items.scrolls.InventoryScroll;
import com.zrp200.rkpd2.items.stones.StoneOfEnchantment;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.Icons;
import com.zrp200.rkpd2.ui.Window;
import com.zrp200.rkpd2.utils.GLog;
import com.zrp200.rkpd2.windows.WndBag;
import com.zrp200.rkpd2.windows.WndMessage;
import com.zrp200.rkpd2.windows.WndOptions;
import com.zrp200.rkpd2.windows.WndTitledMessage;
import com.watabou.noosa.audio.Sample;

public class ScrollOfEnchantment extends ExoticScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_ENCHANT;

		unique = true;
	}

	protected static boolean identifiedByUse = false;
	
	@Override
	public void doRead() {
		if (!isKnown()) {
			identify();
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}
		GameScene.selectItem( itemSelector );
	}

	public static boolean enchantable( Item item ){
		return (item instanceof MeleeWeapon || item instanceof SpiritBow || item instanceof Armor);
	}

	private void confirmCancelation() {
		GameScene.show( new WndOptions(new ItemSprite(this),
				Messages.titleCase(name()),
				Messages.get(InventoryScroll.class, "warning"),
				Messages.get(InventoryScroll.class, "yes"),
				Messages.get(InventoryScroll.class, "no") ) {
			@Override
			protected void onSelect( int index ) {
				switch (index) {
					case 0:
						curUser.spendAndNext( TIME_TO_READ );
						identifiedByUse = false;
						break;
					case 1:
						GameScene.selectItem(itemSelector);
						break;
				}
			}
			public void onBackPressed() {}
		} );
	}

	@SuppressWarnings("unchecked")
	public void enchantWeapon(Weapon weapon) {
		final Weapon.Enchantment enchants[] = new Weapon.Enchantment[3];

		Class<? extends Weapon.Enchantment> existing = weapon.enchantment != null ? weapon.enchantment.getClass() : null;
		enchants[0] = Weapon.Enchantment.randomCommon(existing);
		enchants[1] = weapon instanceof SpiritBow
				? SpiritBow.randomUncommonEnchant(existing)
				: Weapon.Enchantment.randomUncommon(existing);
		Class[] toIgnore = {existing, enchants[0].getClass(), enchants[1].getClass()};
		enchants[2] = weapon instanceof SpiritBow
				? SpiritBow.randomEnchantment(toIgnore)
				: Weapon.Enchantment.random(toIgnore);

		GameScene.show(new WndOptions(new ItemSprite(ScrollOfEnchantment.this),
				Messages.titleCase(ScrollOfEnchantment.this.name()),
				Messages.get(ScrollOfEnchantment.class, "weapon") +
						"\n\n" +
						Messages.get(ScrollOfEnchantment.class, "cancel_warn"),
				enchants[0].name(),
				enchants[1].name(),
				enchants[2].name(),
				Messages.get(ScrollOfEnchantment.class, "cancel")){

			@Override
			protected void onSelect(int index) {
				if (index < 3) {
					weapon.enchant(enchants[index]);
					GLog.p(Messages.get(StoneOfEnchantment.class, "weapon"));
					readAnimation();

					Sample.INSTANCE.play( Assets.Sounds.READ );
					Enchanting.show(curUser, weapon);
					Talent.onUpgradeScrollUsed( Dungeon.hero );
				}
			}

					@Override
					protected boolean hasInfo(int index) {
						return index < 3;
					}

					@Override
					protected void onInfo( int index ) {
						GameScene.show(new WndTitledMessage(
								Icons.get(Icons.INFO),
								Messages.titleCase(enchants[index].name()),
								enchants[index].desc()));
					}

			@Override
			public void onBackPressed() {
				//do nothing, reader has to cancel
			}
		});
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(ScrollOfEnchantment.class, "inv_title");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return enchantable(item);
		}

		@Override
		public void onSelect(final Item item) {

			if (item instanceof Weapon){

				enchantWeapon((Weapon)item);

			} else if (item instanceof Armor) {
				
				final Armor.Glyph glyphs[] = new Armor.Glyph[3];
				
				Class<? extends Armor.Glyph> existing = ((Armor) item).glyph != null ? ((Armor) item).glyph.getClass() : null;
				glyphs[0] = Armor.Glyph.randomCommon( existing );
				glyphs[1] = Armor.Glyph.randomUncommon( existing );
				glyphs[2] = Armor.Glyph.random( existing, glyphs[0].getClass(), glyphs[1].getClass());
				
				GameScene.show(new WndOptions( new ItemSprite(ScrollOfEnchantment.this),
						Messages.titleCase(ScrollOfEnchantment.this.name()),
						Messages.get(ScrollOfEnchantment.class, "armor") +
						"\n\n" +
						Messages.get(ScrollOfEnchantment.class, "cancel_warn"),
						glyphs[0].name(),
						glyphs[1].name(),
						glyphs[2].name(),
						Messages.get(ScrollOfEnchantment.class, "cancel")){
					
					@Override
					protected void onSelect(int index) {
						if (index < 3) {
							((Armor) item).inscribe(glyphs[index]);
							GLog.p(Messages.get(StoneOfEnchantment.class, "armor"));
							readAnimation();
							
							Sample.INSTANCE.play( Assets.Sounds.READ );
							Enchanting.show(curUser, item);
							Talent.onUpgradeScrollUsed( Dungeon.hero );
						}
					}

					@Override
					protected boolean hasInfo(int index) {
						return index < 3;
					}

					@Override
					protected void onInfo( int index ) {
						GameScene.show(new WndTitledMessage(
								Icons.get(Icons.INFO),
								Messages.titleCase(glyphs[index].name()),
								glyphs[index].desc()));
					}

					@Override
					public void onBackPressed() {
						//do nothing, reader has to cancel
					}
				});
			} else {
				if (!identifiedByUse){
					curItem.collect();
				} else {
					((ScrollOfEnchantment)curItem).confirmCancelation();
				}
			}
		}
	};
}
