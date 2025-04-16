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

import static com.zrp200.rkpd2.Dungeon.level;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Bless;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.cleric.PowerOfMany;
import com.zrp200.rkpd2.effects.Flare;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class BlessSpell extends TargetedClericSpell {

	public static final BlessSpell INSTANCE = new BlessSpell();

	@Override
	public int icon() {
		return HeroIcon.BLESS;
	}

	@Override
	public int targetingFlags(){
		return -1; //auto-targeting behaviour is often wrong, so we don't use it
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.BLESS);
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Char ch = Actor.findChar(target);
		if (ch == null || !level.heroFOV[target]){
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

		boolean empowered = SpellEmpower.isActive();
		if (ch == hero || empowered) {
			hero.busy();
			hero.sprite.operate(ch.pos);
			if (ch == hero) {
				if (empowered) {
					GLog.p(Messages.get(SpellEmpower.class, "instant"));
				} else {
					hero.spend( 1f );
				}

				Char ally = PowerOfMany.getPoweredAlly();
				if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
					affectChar(hero, ch, 1);
				}
			} else {
				GLog.p(Messages.get(SpellEmpower.class, "all_allies"));
				int factor = 1;
				for (Char m : level.mobs) {
					if (m.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
						affectChar(hero, m, level.heroFOV[m.pos] ? factor = 2 : 1);
					} else if (m == ch || level.heroFOV[m.pos] && m.alignment == hero.alignment) {
						affectChar(hero, m, 1);
					}
				}
				affectChar(hero, hero, factor);
				hero.spend( 1f );
			}
		} else {
			hero.sprite.zap(ch.pos);
			hero.spendAndNext( 1f );
		}

		onSpellCast(tome, hero);
	}

	private void affectChar(Hero hero, Char ch, int factor){
		new Flare(6, 32).color(0xFFFF00, true).show(ch.sprite, 2f);
		if (ch == hero){
			Buff.prolong(ch, Bless.class, factor*(2f + 4*hero.pointsInTalent(Talent.BLESS)));
		} else {
			Buff.prolong(ch, Bless.class, factor*(5f + 5*hero.pointsInTalent(Talent.BLESS)));
		}
		LayOnHands.affectChar(hero, ch, factor * (5 + 5 * hero.pointsInTalent(Talent.BLESS)), 1);
	}

	public String desc(){
		int talentLvl = Dungeon.hero.pointsInTalent(Talent.BLESS);
		return Messages.get(this, "desc", 2+4*talentLvl, 5+5*talentLvl, 5+5*talentLvl, 5+5*talentLvl) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

}
