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
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Cooldown;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.MagicMissile;
import com.zrp200.rkpd2.effects.particles.ShadowParticle;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.ui.QuickSlotButton;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GuidingLight extends TargetedClericSpell {

	public static final GuidingLight INSTANCE = new GuidingLight();

	@Override
	public int icon() {
		return HeroIcon.GUIDING_LIGHT;
	}

	@Override
	public void tintIcon(HeroIcon icon) {
		// todo make icon
		if (SpellEmpower.isActive()) icon.tint(0, .33f);
	}

	private int targets, deferredCasts;

	@Override
	public void onCast(HolyTome tome, Hero hero) {
		if (SpellEmpower.isActive()) {
			ArrayList<Integer> targets = new ArrayList<>();
			for (Char ch : level.mobs) {
				if (level.heroFOV[ch.pos] && ch.alignment == Char.Alignment.ENEMY) {
					int aim = QuickSlotButton.autoAim(ch, this);
					if (aim == ch.pos) {
						targets.add(aim);
					}
				}
			}
			this.targets = targets.size();
			deferredCasts = 0;
			if (this.targets > 0) {
				hero.sprite.showStatus(CharSprite.POSITIVE, name());
				for (int target : targets) onTargetSelected(tome, hero, target);
				return;
			}
		}
		super.onCast(tome, hero);
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Ballistica aim = new Ballistica(hero.pos, target, targetingFlags());

		if (Actor.findChar( aim.collisionPos ) == hero){
			GLog.i( Messages.get(Wand.class, "self_target") );
			return;
		}

		if (!SpellEmpower.isActive()) {
			if (Actor.findChar(aim.collisionPos) != null) {
				QuickSlotButton.target(Actor.findChar(aim.collisionPos));
			} else {
				QuickSlotButton.target(Actor.findChar(target));
			}
		}


		hero.busy();
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		hero.sprite.zap(target);
		MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.LIGHT_MISSILE, hero.sprite, aim.collisionPos, new Callback() {
			@Override
			public void call() {

				Char ch = Actor.findChar( aim.collisionPos );
				if (ch != null) {
					int damageBoost = SpellEmpower.isActive() && ch.buff(Illuminated.class) != null ? hero.lvl : 0;
					ch.damage(Random.NormalIntRange(2, 6) + damageBoost, GuidingLight.this);
					Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
					ch.sprite.burst(0xFFFFFF44, 3);
					if (damageBoost > 0) {
						ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10);
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
					}
					if (ch.isAlive()){
						Buff.affect(ch, Illuminated.class);
						Buff.affect(ch, WasIlluminatedTracker.class);

						if (SpellEmpower.isActive() && targets == 1 && deferredCasts == 0) {
							targets++;
							onTargetSelected(tome, hero, target);
						}
					}
				} else {
					Dungeon.level.pressCell(aim.collisionPos);
				}

				onSpellCast(tome, hero);

			}
		});
	}

	@Override
	public synchronized void onSpellCast(HolyTome tome, Hero hero) {
		--targets;
		super.onSpellCast(tome, hero);
		if (targets <= 0) {
			deferredCasts = 0;
			hero.spendAndNext(1f);
			if (hero.subClass.is(HeroSubClass.PRIEST) && hero.buff(GuidingLightPriestCooldown.class) == null) {
				Cooldown.affectHero(GuidingLightPriestCooldown.class);
			}
		}
	}

	@Override
	public float chargeUse(Hero hero) {
		if (hero.subClass == HeroSubClass.PRIEST
			&& hero.buff(GuidingLightPriestCooldown.class) == null){
			return 0;
		} else {
			return targets > 0 ? 0 : SpellEmpower.isActive() ? 0.5f * (1 + deferredCasts) : 1;
		}
	}

	public String desc(){
		String desc = Messages.get(this, "desc");
		if (Dungeon.hero.subClass == HeroSubClass.PRIEST){
			desc += "\n\n" + Messages.get(this, "desc_priest");
		}
		if (SpellEmpower.isActive()) {
			desc += "\n\n" + Messages.get("desc_empower");
		}
		return desc + "\n\n" + chargeUseDesc();
	}

	public static class GuidingLightPriestCooldown extends Cooldown {

		@Override
		public int icon() {
			return BuffIndicator.ILLUMINATED;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.brightness(0.5f);
		}

		@Override
		public float duration() {
			return Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.PRIEST ? 50 : 100;
		}
	}

	public static class Illuminated extends Buff {

		{
			type = buffType.NEGATIVE;
		}

		public static void proc(Char target) {
			if (Dungeon.hero.subClass == HeroSubClass.PRIEST && target.buff(Illuminated.class) != null) {
				target.buff(Illuminated.class).detach();
				target.damage(Dungeon.hero.lvl, INSTANCE);
			}
			checkReapply(target);
		}
		public static void checkReapply(Char target) {
			if (Dungeon.hero.hasTalent(Talent.ENDURING_LIGHT) && target.buff(WasIlluminatedTracker.class) != null) {
				if (Random.Int(3) < Dungeon.hero.pointsInTalent(Talent.ENDURING_LIGHT)) {
					Buff.affect(target, Illuminated.class);
				}
			}
		}

		@Override
		public int icon() {
			return BuffIndicator.ILLUMINATED;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
			else target.sprite.remove(CharSprite.State.ILLUMINATED);
		}

		@Override
		public String desc() {
			String desc = super.desc();

			if (Dungeon.hero.subClass == HeroSubClass.PRIEST){
				desc += "\n\n" + Messages.get(this, "desc_priest");
			} else if (Dungeon.hero.heroClass != HeroClass.CLERIC){
				desc += "\n\n" + Messages.get(this, "desc_generic");
			}

			return desc;
		}
	}

	public static class WasIlluminatedTracker extends Buff {}
}
