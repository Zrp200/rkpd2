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

package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.Thief;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.food.FrozenCarpaccio;
import com.zrp200.rkpd2.items.food.MysteryMeat;
import com.zrp200.rkpd2.items.potions.Potion;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Frost extends FlavourBuff {

	public static final float DURATION	= 10f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public boolean attachTo( Char target ) {
		Buff.detach( target, Burning.class );

		if (super.attachTo( target )) {
			
			target.paralysed++;
			Buff.detach( target, Chill.class );

			if (target instanceof Hero) {

				Hero hero = (Hero)target;
				ArrayList<Item> freezable = new ArrayList<>();
				//does not reach inside of containers
				if (!hero.belongings.lostInventory()) {
					for (Item i : hero.belongings.backpack.items) {
						if (!i.unique && (i instanceof Potion || i instanceof MysteryMeat)) {
							freezable.add(i);
						}
					}
				}
				
				if (!freezable.isEmpty()){
					Item toFreeze = Random.element(freezable).detach( hero.belongings.backpack );
					GLog.w( Messages.capitalize(Messages.get(this, "freezes", toFreeze.title())) );
					if (toFreeze instanceof Potion){
						((Potion) toFreeze).shatter(hero.pos);
					} else if (toFreeze instanceof MysteryMeat){
						FrozenCarpaccio carpaccio = new FrozenCarpaccio();
						if (!carpaccio.collect( hero.belongings.backpack )) {
							Dungeon.level.drop( carpaccio, target.pos ).sprite.drop();
						}
					}
				}
				
			} else if (target instanceof Thief) {

				Item item = ((Thief) target).item;

				if (item instanceof Potion && !item.unique) {
					((Potion) ((Thief) target).item).shatter(target.pos);
					((Thief) target).item = null;
				} else if (item instanceof MysteryMeat){
					((Thief) target).item = new FrozenCarpaccio();
				}

			}

			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		super.detach();
		if (target.paralysed > 0)
			target.paralysed--;
		if (Dungeon.level.water[target.pos])
			Buff.prolong(target, Chill.class, Chill.DURATION/2f);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FROST;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0f, 0.75f, 1f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void fx(boolean on) {
		if (on) {
			target.sprite.add(CharSprite.State.FROZEN);
			target.sprite.add(CharSprite.State.PARALYSED);
		} else {
			target.sprite.remove(CharSprite.State.FROZEN);
			if (target.paralysed <= 1) target.sprite.remove(CharSprite.State.PARALYSED);
		}
	}

	{
		//can't chill what's frozen!
		immunities.add( Chill.class );
	}

}
