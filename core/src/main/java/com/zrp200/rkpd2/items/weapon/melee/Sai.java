/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.zrp200.rkpd2.items.weapon.melee;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

import java.util.HashSet;

public class Sai extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SAI;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 0.5f; //2x speed
	}

	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //10 base, down from 20
				lvl*Math.round(0.5f*(tier+1));  //+2 per level, down from +4
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	protected DuelistAbility duelistAbility() {
		return new ComboStrike();
	}

	protected class ComboStrike extends MeleeAbility {

		// 1 - 0.4
		// 3 - 0.35
		// 5 - 0.3
		public final float boostPerHit = 0.4f - (tier-1)*.025f;

		private int recentHits;
		@Override
		protected void beforeAbilityUsed(Hero hero) {
			HashSet<ComboStrikeTracker> buffs = hero.buffs(ComboStrikeTracker.class);
			recentHits = buffs.size();
			for (Buff b : buffs){
				b.detach();
			}
		}

		@Override
		public float dmgMulti(Char enemy) {
			return boostPerHit*recentHits;
		}

		@Override
		protected void playSFX() {
			if (recentHits >= 2) super.playSFX();
		}
	}

	public static class ComboStrikeTracker extends FlavourBuff{

		public static float DURATION = 5f;

	}

}
