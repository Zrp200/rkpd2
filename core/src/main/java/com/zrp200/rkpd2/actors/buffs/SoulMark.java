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

package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.actors.hero.Talent;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.items.wands.WandOfWarding;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.ui.BuffIndicator;

public class SoulMark extends FlavourBuff {

	public static final float DURATION	= 10f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public static void process(Char defender, int level, int chargesUsed, boolean delay) {
		//standard 1 - 0.92^x chance, plus 7%. Starts at 15%
		if (defender != Dungeon.hero
				&& (Dungeon.hero.subClass == HeroSubClass.WARLOCK || Dungeon.hero.subClass == HeroSubClass.KING)
				&& Random.Float() > (Math.pow(0.92f, (level * chargesUsed) + 1) - 0.07f)) {
			DelayedMark mark = affect(defender,DelayedMark.class);
			mark.duration = DURATION+level;
			if(!delay) mark.activate();
			// see Char#damage
		}
	}
	// basically lets me hold onto it for a hot second. detaching adds the mark, which means I can delay activation to when I want it to.
	public static class DelayedMark extends Buff {
		public float duration;

		public void activate() {
			Buff.prolong(target,SoulMark.class,duration);
			detach();
		}
		// is this needed? idk
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put("duration",duration);
		}
		public void restoreFromBundle(Bundle bundle) {
			duration = bundle.getFloat("duration");
		}
	}

	public void proc(Object src, Char defender, int damage) {
		if(!(src instanceof Char) && !Dungeon.hero.hasTalent(Talent.SOUL_SIPHON)) return; // this shouldn't come up, but in case it does...
		int restoration = Math.min(damage, defender.HP);
		//physical damage that doesn't come from the hero is less effective
		if (defender != Dungeon.hero){
			// wand damage is handled prior this method's calling.
			restoration = Math.round(restoration * Dungeon.hero.pointsInTalent(Talent.SOUL_SIPHON,Talent.RK_WARLOCK) * (Dungeon.hero.hasTalent(Talent.SOUL_SIPHON) ? 1/6f : .15f));
		}
		if(restoration > 0)
		{
			Buff.affect(Dungeon.hero, Hunger.class).affectHunger(restoration*Math.max(
					Dungeon.hero.pointsInTalent(Talent.SOUL_EATER)/2f,
					Dungeon.hero.pointsInTalent(Talent.RK_WARLOCK)/3f));
			Dungeon.hero.HP = (int) Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP + (restoration * 0.4f)));
			Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.CORRUPT;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.5f, 0.5f, 0.5f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.MARKED);
		else target.sprite.remove(CharSprite.State.MARKED);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Dungeon.hero.subClass.title(), dispTurns());
	}
}
