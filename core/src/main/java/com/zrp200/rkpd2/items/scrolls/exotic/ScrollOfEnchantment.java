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

package com.zrp200.rkpd2.items.scrolls.exotic;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.Enchanting;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.Armor;
import com.zrp200.rkpd2.items.scrolls.Scroll;
import com.zrp200.rkpd2.items.stones.StoneOfEnchantment;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.enchantments.Blocking;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.items.weapon.enchantments.Grim;
import com.zrp200.rkpd2.items.weapon.enchantments.Lucky;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.utils.GLog;
import com.zrp200.rkpd2.windows.WndBag;
import com.zrp200.rkpd2.windows.WndOptions;

public class ScrollOfEnchantment extends ExoticScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_ENCHANT;

		unique = true;
	}
	
	@Override
	public void doRead() {
		identify();
		
		GameScene.selectItem( itemSelector, WndBag.Mode.ENCHANTABLE, Messages.get(this, "inv_title"));
	}

	private static Class<? extends Weapon.Enchantment>[] generateIgnoreList(Weapon weapon, Class<?extends Weapon.Enchantment>... existing) {
		if(!(weapon instanceof SpiritBow)) return existing;
		Class<?extends Weapon.Enchantment>[] toIgnore = new Class[existing.length+SpiritBow.REMOVED_ENCHANTS.length];
		int i=0;
		for(Class enchantClass : existing) toIgnore[i++] = enchantClass;
		// 33% for it to be ignored and thus making it impossible to be replaced.
		for(Class enchantClass : SpiritBow.REMOVED_ENCHANTS) if(Random.Int(2) == 0) toIgnore[i++] = enchantClass;
		return toIgnore;
	}

	public static void enchantWeapon(Weapon weapon) {
		final Weapon.Enchantment enchants[] = new Weapon.Enchantment[3];

		Class<? extends Weapon.Enchantment> existing = weapon.enchantment != null ? weapon.enchantment.getClass() : null;
		enchants[0] = Weapon.Enchantment.randomCommon(generateIgnoreList(weapon,existing));
		enchants[1] = Weapon.Enchantment.randomUncommon(generateIgnoreList(weapon,existing));
		enchants[2] = Weapon.Enchantment.random( generateIgnoreList( weapon, existing, enchants[0].getClass(), enchants[1].getClass() ) );
		if(weapon instanceof SpiritBow) {
			// essentially a find and replace with priorities.
			// this means that they're all technically obtainable (aside from blocking)
			// overall effect here is that blocking and lucky are removed from the pool.
			if(enchants[1] instanceof Blocking || enchants[1] instanceof Lucky) enchants[1] = new Explosive();
			// if they show up in the third slot they are replaced with grim
			if(enchants[2] instanceof Blocking || enchants[2] instanceof Lucky) enchants[2] = Random.Int(3) == 0 || enchants[1] instanceof Explosive ? new Grim() : new Explosive();
		}

		GameScene.show(new WndOptions(Messages.titleCase(new ScrollOfEnchantment().name()),
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
					((Scroll)curItem).readAnimation();

					Sample.INSTANCE.play( Assets.Sounds.READ );
					Enchanting.show(curUser, weapon);
					Talent.onUpgradeScrollUsed( Dungeon.hero );
				}
			}

			@Override
			public void onBackPressed() {
				//do nothing, reader has to cancel
			}
		});
	}
	
	protected WndBag.Listener itemSelector = new WndBag.Listener() {
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
				
				GameScene.show(new WndOptions(Messages.titleCase(ScrollOfEnchantment.this.name()),
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
							((ScrollOfEnchantment)curItem).readAnimation();
							
							Sample.INSTANCE.play( Assets.Sounds.READ );
							Enchanting.show(curUser, item);
							Talent.onUpgradeScrollUsed( Dungeon.hero );
						}
					}
					
					@Override
					public void onBackPressed() {
						//do nothing, reader has to cancel
					}
				});
			} else {
				//TODO if this can ever be found un-IDed, need logic for that
				curItem.collect();
			}
		}
	};
}
