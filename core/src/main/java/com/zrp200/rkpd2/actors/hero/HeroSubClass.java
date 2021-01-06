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

package com.zrp200.rkpd2.actors.hero;

import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.missiles.MissileWeapon;
import com.zrp200.rkpd2.messages.Messages;
import com.watabou.utils.Bundle;

public enum HeroSubClass {

	NONE( null ),
	
	GLADIATOR( "gladiator" ),
	BERSERKER( "berserker" ),
	
	WARLOCK( "warlock" ),
	BATTLEMAGE( "battlemage" ),
	
	ASSASSIN( "assassin" ) {
		@Override public int getBonus(Item item) {
			return item instanceof Weapon ? 2 : 0; // for a sum of 6 total boosts in play.
		}
	},
	FREERUNNER( "freerunner" ) {
		@Override
		public int getBonus(Item item) {
			if(item instanceof Wand) return 2;
			if(item instanceof MissileWeapon) return 2;
			return 0;
		}
	},
	
	SNIPER( "sniper" ),
	WARDEN( "warden" ),

	KING ("king");
	
	private String title;

	// this corresponds to the one in HeroClass
	public int getBonus(Item item) { return 0; }
	
	HeroSubClass( String title ) {
		this.title = title;
	}
	
	public String title() {
		return Messages.get(this, title);
	}
	
	public String desc() {
		return Messages.get(this, title+"_desc");
	}
	
	private static final String SUBCLASS	= "subClass";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( SUBCLASS, toString() );
	}
	
	public static HeroSubClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( SUBCLASS );
		return valueOf( value );
	}
	
}
