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

import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Callback;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.cleric.PowerOfMany;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.ui.QuickSlotButton;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class ShieldOfLight extends TargetedClericSpell {

	public static ShieldOfLight INSTANCE = new ShieldOfLight();

	@Override
	public int icon() {
		return HeroIcon.SHIELD_OF_LIGHT;
	}

	@Override
	public int targetingFlags() {
		return Ballistica.STOP_TARGET;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && (SpellEmpower.isActive() || hero.hasTalent(Talent.SHIELD_OF_LIGHT));
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {

		if (target == null){
			return;
		}

		Char ch = Actor.findChar(target);
		if (ch == null || ch.alignment == Char.Alignment.ALLY || !Dungeon.level.heroFOV[target]){
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		QuickSlotButton.target(ch);

		Sample.INSTANCE.play(Assets.Sounds.READ);
		hero.sprite.operate(hero.pos);

		affectChar(hero, ch);

		hero.busy();
		hero.sprite.operate(hero.pos);

		Char ally = PowerOfMany.getPoweredAlly();
		if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){
			affectChar(ally, ch);
		}

		onSpellCast(tome, hero);

	}

	public void affectChar(Char ch, Char target) {
		if (SpellEmpower.isActive()) {
			Buff.prolong(ch, DivineShield.class, DivineShield.duration() - 1).object = target.id();
		} else {
			//1 turn less as the casting is instant
			Buff.prolong(ch, ShieldOfLightTracker.class, 3f).object = target.id();
		}
		ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.15f, 6);
	}

	@Override
	public String desc() {
		String desc;
		if (SpellEmpower.isActive()) {
			desc = Messages.get(this, "desc_empower", DivineShield.parries());
		} else {
			int min = 1 + Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT);
			int max = 2*min;
			desc = Messages.get(this, "desc", min, max);
		}
 		return desc + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public static class ShieldOfLightTracker extends FlavourBuff {

		public int object = 0;

		private static final float DURATION = 4;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.LIGHT_SHIELD;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		private static final String OBJECT  = "object";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( OBJECT, object );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			object = bundle.getInt( OBJECT );
		}

	}

	// divine shield gives 1/2/3 parries over 3/6/9 turns
	// if you don't use them, you lose them, though.
	public static class DivineShield extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.LIGHT_SHIELD;
		}

		private static final float TURNS_PER_PARRY = 3f;

		public static int parries() {
			return 1 + Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT);
		}

		public static float duration() {
			// 1/2/3 parries
			return TURNS_PER_PARRY * parries();
		}

		private int object;

		@Override
		public String desc() {
			return Messages.get(this, "desc", left(), visualcooldown() % TURNS_PER_PARRY);
		}

		@Override
		public float iconFadePercent() {
			return 1 - Math.min(visualcooldown() / duration(), 1);
		}

		public int left() {
			return (int)Math.ceil(visualcooldown() / TURNS_PER_PARRY);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(left());
		}

		public synchronized void use(Char ch, Callback callback) {
			if (active == this) active = null;
			spend(-TURNS_PER_PARRY);
			if (callback != null) {
				if (!ch.sprite.visible) callback.call();
				else {
					add(new Actor() {
						{
							actPriority = VFX_PRIO;
						}
						@Override
						protected boolean act() {
							remove(this);
							return false;
						}

						@Override
						protected void onRemove() {
							ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.15f, 6);
							Sample.INSTANCE.play(Assets.Sounds.READ);
							ch.sprite.parent.add(new Delayer(0.45f) {
								@Override
								protected void onComplete() {
									callback.call();
									next();
								}
							});
						}
					});
				}
			}

			if (left() <= 0) detach();
		}

		public static final String OBJECT = "object";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}

		public static DivineShield find(Char ch, Char target) {
			for (DivineShield buff : ch.buffs(DivineShield.class)) {
				if (buff.object == target.id()) return buff;
			}
			if (ch == Dungeon.hero || ch.alignment != Dungeon.hero.alignment) return null;
			return find(Dungeon.hero, target);
		}

		private static ShieldOfLight.DivineShield active = null;

		// defenseProc doesn't take a target :/

		public static synchronized boolean tryActivate(Char ch, Char target) {
			active = find(ch, target);
			if (active == null) return false;
			add(() -> active = null);
			return true;
		}

		public static boolean tryUse(Char ch) {
			return tryUse(ch, null);
		}

		public static synchronized boolean tryUse(Char ch, Callback reflect) {
			if (active != null) {
				active.use(ch, reflect);
				return true;
			}
			return false;
		}

		public static boolean tryUse(Char ch, Char enemy, Callback callback) {
			return ch != enemy && tryActivate(ch, enemy) && tryUse(ch, callback);
		}
	}

}
