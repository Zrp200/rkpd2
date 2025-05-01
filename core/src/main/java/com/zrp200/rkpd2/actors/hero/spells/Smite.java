/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

import static com.zrp200.rkpd2.Dungeon.hero;
import static com.zrp200.rkpd2.Dungeon.level;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Combo;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.ui.AttackIndicator;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.ui.RedButton;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.windows.WndTitledMessage;

import java.util.HashMap;

public class Smite extends TargetedClericSpell {

	public static Smite INSTANCE = new Smite();

	@Override
	public int icon() {
		if (Dungeon.hero != null) {
			HolyTome tome = Dungeon.hero.belongings.getItem(HolyTome.class);
			if (tome != null && tome.canCast(Dungeon.hero, new OmniSmite())) {
				return HeroIcon.OMNISMITE;
			}
		}
		return HeroIcon.SMITE;
	}

	@Override
	public int targetingFlags() {
		return Ballistica.STOP_TARGET; //no auto-aim
	}

	@Override
	public String desc() {
		int min = 5 + Dungeon.hero.lvl/2;
		int max = 10 + Dungeon.hero.lvl;
		return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2f;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.subClass == HeroSubClass.PALADIN;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {
        if (tome.canCast(hero, new OmniSmite())) {
            GameScene.show(new WndSmiteSelect(tome, hero));
        }
        else {
            super.onCast(tome, hero);
        }
    }

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero) {
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		//we apply here because of projecting
		SmiteTracker tracker = Buff.affect(hero, SmiteTracker.class);
		int leapPos = Combo.Leap.findLeapPos(hero, enemy, hero.pointsInTalent(Talent.TRIAGE));
		if (leapPos == -1) {
			GLog.w(Messages.get(this, "invalid_enemy"));
			tracker.detach();
			return;
		}
		Combo.Leap.execute(hero, enemy, leapPos, () -> {
			doSmite(tome, hero, enemy, 1);
			tracker.detach();
			hero.spendAndNext(hero.attackDelay());
		});
	}

	public void doSmite(HolyTome tome, Hero hero, Char enemy, float dmgMulti) {
		AttackIndicator.target(enemy);

		float accMult = 1;
		if (!(hero.belongings.attackingWeapon() instanceof Weapon)
				|| ((Weapon) hero.belongings.attackingWeapon()).STRReq() <= hero.STR()) {
			accMult = Char.INFINITE_ACCURACY;
		}
		if (hero.attack(enemy, dmgMulti, 0, accMult)) {
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			enemy.sprite.burst(0xFFFFFFFF, (int)(10 * dmgMulti));
		}

		onSpellCast(tome, hero);
	}

	public static class SmiteTracker extends FlavourBuff {
		public int bonusDmg( Hero attacker, Char defender){
			int min = 5 + attacker.lvl/2;
			int max = 10 + attacker.lvl;
			if (Char.hasProp(defender, Char.Property.UNDEAD) || Char.hasProp(defender, Char.Property.DEMONIC)){
				return max;
			} else {
				return Random.NormalIntRange(min, max);
			}
		}
		public float enchMulti() {
			return 3f;
		}
	};

	public static class OmniSmite extends Smite {

		// this is basically a fancy way of killing anything.
		// todo revisit, it might be nice to have the last hit be a regular smite

		private static int used = 0;

		private static final float MIN_USES = 3;
		public static final float MULTI = 0.75f;

		@Override
		public boolean canCast(Hero hero) {
			return used > 0 ||
					super.canCast(hero) && SpellEmpower.isActive();
		}

		private boolean forceCheckCharges = false;

		@Override
		public String desc() {
			HolyTome tome = hero.belongings.getItem(HolyTome.class);
			return Messages.get(this, "desc",
					(int)Math.max(OmniSmite.MIN_USES, Math.max(tome.getCharges(), SpellEmpower.left())),
					(int)SpellEmpower.left(),
					Math.max(2, tome.getCharges())
			);
		}

		@Override
		public boolean ignoreChargeUse() {
			return !forceCheckCharges && ((used > 0 && used < MIN_USES) || super.ignoreChargeUse());
		}

		@Override
		public float chargeUse(Hero hero) {
			return 1f;
		}

		@Override
		public void onSpellCast(HolyTome tome, Hero hero) {
			if (used >= 2 && SpellEmpower.isActive()) {
				// refunds charge use if it's still attacking with no charges after breaking even with Smite's charge cost.
				forceCheckCharges = true;
				if (!tome.canCast(hero, this)) tome.directCharge(chargeUse(hero));
				forceCheckCharges = false;
			}
			super.onSpellCast(tome, hero);
		}

		@Override
		public void onCast(HolyTome tome, Hero hero) {
			Buff.affect(hero, OmniSmiteTracker.class);
			int range = Math.max(hero.pointsInTalent(Talent.TRIAGE), (int)SpellEmpower.left());
			try {
				HashMap<Char, Integer> validTargets = new HashMap<>();
				int minDist = Integer.MAX_VALUE;
				for (Char m : level.mobs) {
					int distance = level.distance(hero.pos, m.pos);
					if (distance > minDist || m.alignment != Char.Alignment.ENEMY) continue;
					int leapPos = Combo.Leap.findLeapPos(hero, m, range);
					if (leapPos == -1) continue;
					if (distance < minDist) {
						validTargets.clear();
						minDist = distance;
					}
					validTargets.put(m, leapPos);
				}
				Char target = Random.element(validTargets.keySet());
				if (tome.canCast(hero, this) && target != null) {
					Combo.Leap.execute(hero, target, validTargets.get(target), () -> {
						used++;
						doSmite(tome, hero, target, MULTI);
						onCast(tome, hero);
					});
					return;
				}
				if (used > 0) {
					used = 0;
					SpellEmpower.useCharge(SpellEmpower.limit(), SpellEmpower.limit()); // use remaining charge
					hero.spendAndNext(hero.attackDelay());
				} else {
					assert target == null;
					GLog.w(Messages.get(this, "no_targets"));
				}
			} finally {
				Buff.detach(hero, OmniSmiteTracker.class);
			}
		}

		public static class OmniSmiteTracker extends SmiteTracker {
			@Override
			public int bonusDmg(Hero attacker, Char defender) {
				return Random.round(MULTI * super.bonusDmg(attacker, defender));
			}

			@Override
			public float enchMulti() {
				return MULTI * super.enchMulti();
			}
		}
	}

	private class WndSmiteSelect extends WndTitledMessage {
		// fixme should this just be a separate spell?

		RedButton buttonFor(HolyTome tome, Hero hero, Smite smite) {
			return new RedButton("_" + Messages.titleCase(smite.name()) + "_:\n" + smite.desc(), 6) {
				{
					multiline = true;
				}

				@Override
				protected void onClick() {
					hide();
					smite.onCast(tome, hero);
				}
			};
		}

        WndSmiteSelect(HolyTome tome, Hero hero) {
            super(new HeroIcon(Smite.this),
                            "Smite", ""
            );
			RedButton
					smite = buttonFor(tome, hero, Smite.this),
					omniSmite = buttonFor(tome, hero, new OmniSmite());
			omniSmite.setWidth(width);
			omniSmite.setHeight(omniSmite.reqHeight());
			smite.setWidth(width);
			smite.setHeight(smite.reqHeight());
			omniSmite.setY(smite.bottom() + GAP);
            addToBottom(smite, omniSmite);
        }
    }

}
