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

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Blindness;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.actors.buffs.Paralysis;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.Beam;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.tiles.DungeonTilemap;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.ui.QuickSlotButton;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.List;

public class Sunray extends TargetedClericSpell {

	public static final Sunray INSTANCE = new Sunray();

	@Override
	public int icon() {
		return HeroIcon.SUNRAY;
	}

	@Override
	protected List<Object> getDescArgs() {
		int points = Dungeon.hero.pointsInTalent(Talent.SUNRAY);
		int min = 2 * (1 + points);
		int max = 4 * (1 + points);
		int dur = 2 * (1 + points);
		return Arrays.asList(min, max, dur);
	}

	private int targets = 0;

	@Override
	public float chargeUse(Hero hero) {
		return SpellEmpower.isActive() ? 0.5f * Math.max(1, targets) : super.chargeUse(hero);
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && (hero.hasTalent(Talent.SUNRAY) || SpellEmpower.isActive());
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {
		if (SpellEmpower.isActive()) {
			for (Char m : Dungeon.level.mobs) {
				if (m.alignment == Char.Alignment.ENEMY && Dungeon.level.heroFOV[m.pos]) {
					int pos = QuickSlotButton.autoAim(m, this);
					if (pos != -1) {
						onTargetSelected(tome, hero, m.pos);
					}
				}
			}
			if (targets == 0) {
				GLog.w("No enemies in sight!");
			} else {
				onSpellCast(tome, hero);
			}
		}
		else super.onCast(tome, hero);
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Ballistica aim = new Ballistica(hero.pos, target,  targetingFlags());

		if (Actor.findChar( aim.collisionPos ) == hero){
			GLog.i( Messages.get(Wand.class, "self_target") );
			return;
		}

		if (Actor.findChar(aim.collisionPos) != null) {
			QuickSlotButton.target(Actor.findChar(aim.collisionPos));
		} else {
			QuickSlotButton.target(Actor.findChar(target));
		}

		targets++;
		hero.busy();
		if (targets == 1) {
			Sample.INSTANCE.play(Assets.Sounds.RAY);
		}
		hero.sprite.zap(target);

		hero.sprite.parent.add(
				new Beam.SunRay(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(aim.collisionPos)));

		Char ch = Actor.findChar( aim.collisionPos );
		if (ch != null) {
			ch.sprite.burst(0xFFFFFF44, 5);

			if (Char.hasProp(ch, Char.Property.UNDEAD) || Char.hasProp(ch, Char.Property.DEMONIC)){
				if (hero.pointsInTalent(Talent.SUNRAY) == 2) {
					ch.damage(12, Sunray.this);
				} else {
					ch.damage(8, Sunray.this);
				}
			} else {
				if (hero.pointsInTalent(Talent.SUNRAY) == 2) {
					ch.damage(Random.NormalIntRange(6, 12), Sunray.this);
				} else {
					ch.damage(Random.NormalIntRange(4, 8), Sunray.this);
				}
			}

			if (ch.isAlive()) for (int i = 0; i < (SpellEmpower.isActive() ? 2 : 1); i++){
				if (ch.buff(Blindness.class) != null && ch.buff(SunRayRecentlyBlindedTracker.class) != null) {
					Buff.prolong(ch, Paralysis.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
					ch.buff(SunRayRecentlyBlindedTracker.class).detach();
				} else if (ch.buff(SunRayUsedTracker.class) == null) {
					Buff.prolong(ch, Blindness.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
					Buff.prolong(ch, SunRayRecentlyBlindedTracker.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
					Buff.affect(ch, SunRayUsedTracker.class);
				}
			}
		}
		if (!SpellEmpower.isActive()) {
			onSpellCast(tome, hero);
		}
	}

	@Override
	public void onSpellCast(HolyTome tome, Hero hero) {
		hero.spendAndNext( 1f );
		super.onSpellCast(tome, hero);
		targets = 0;
	}

	public static class SunRayUsedTracker extends Buff {}
	public static class SunRayRecentlyBlindedTracker extends FlavourBuff {}

}
