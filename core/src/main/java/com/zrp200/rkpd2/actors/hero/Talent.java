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

package com.zrp200.rkpd2.actors.hero;

import static com.zrp200.rkpd2.Dungeon.hero;

import static java.lang.Math.max;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.GamesInProgress;
import com.zrp200.rkpd2.ShatteredPixelDungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Adrenaline;
import com.zrp200.rkpd2.actors.buffs.AllyBuff;
import com.zrp200.rkpd2.actors.buffs.ArtifactRecharge;
import com.zrp200.rkpd2.actors.buffs.Barrier;
import com.zrp200.rkpd2.actors.buffs.Berserk;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.CounterBuff;
import com.zrp200.rkpd2.actors.buffs.EnhancedRings;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.actors.buffs.Haste;
import com.zrp200.rkpd2.actors.buffs.Hunger;
import com.zrp200.rkpd2.actors.buffs.LostInventory;
import com.zrp200.rkpd2.actors.buffs.PhysicalEmpower;
import com.zrp200.rkpd2.actors.buffs.Preparation;
import com.zrp200.rkpd2.actors.buffs.Recharging;
import com.zrp200.rkpd2.actors.buffs.RevealedArea;
import com.zrp200.rkpd2.actors.buffs.Roots;
import com.zrp200.rkpd2.actors.buffs.WandEmpower;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.actors.hero.abilities.Ratmogrify;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.effects.SpellSprite;
import com.zrp200.rkpd2.items.BrokenSeal;
import com.zrp200.rkpd2.items.EquipableItem;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.Armor;
import com.zrp200.rkpd2.items.artifacts.Artifact;
import com.zrp200.rkpd2.items.armor.ClothArmor;
import com.zrp200.rkpd2.items.artifacts.CloakOfShadows;
import com.zrp200.rkpd2.items.artifacts.HornOfPlenty;
import com.zrp200.rkpd2.items.potions.Potion;
import com.zrp200.rkpd2.items.rings.Ring;
import com.zrp200.rkpd2.items.scrolls.Scroll;
import com.zrp200.rkpd2.items.scrolls.ScrollOfRecharging;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.melee.Gloves;
import com.zrp200.rkpd2.items.weapon.melee.MagesStaff;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.items.weapon.missiles.MissileWeapon;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.levels.features.HighGrass;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), VETERANS_INTUITION(1), TEST_SUBJECT(2), IRON_WILL(3),
	//Warrior T2
	IRON_STOMACH(4), RESTORED_WILLPOWER(5), RUNIC_TRANSFERENCE(6), LETHAL_MOMENTUM(7), IMPROVISED_PROJECTILES(8),
	//Warrior T3
	HOLD_FAST(9, 3), STRONGMAN(10, 3),
	//Berserker T3
	ENDLESS_RAGE(11, 3), DEATHLESS_FURY(12, 3), ENRAGED_CATALYST(13, 3), ONE_MAN_ARMY(30,3),
	//Gladiator T3
	CLEAVE(14, 3), LETHAL_DEFENSE(15, 3), ENHANCED_COMBO(16, 3), SKILL(31,3),
	//Heroic Leap T4
	BODY_SLAM(17, 4), IMPACT_WAVE(18, 4), DOUBLE_JUMP(19, 4),
	//Shockwave T4
	EXPANDING_WAVE(20, 4), STRIKING_WAVE(21, 4), SHOCK_FORCE(22, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION(23, 4), SHRUG_IT_OFF(24, 4), EVEN_THE_ODDS(25, 4),

	//Mage T1
	ENERGIZING_MEAL_I(36), SCHOLARS_INTUITION(33), TESTED_HYPOTHESIS(34), BACKUP_BARRIER(35),
	//Mage T2
	ENERGIZING_MEAL_II(36), ENERGIZING_UPGRADE(37), WAND_PRESERVATION(38), ARCANE_VISION(39), SHIELD_BATTERY(40),
	//Mage T3
	EMPOWERING_SCROLLS(41, 3), ALLY_WARP(42, 3),
	//Battlemage T3
	EMPOWERED_STRIKE(43, 3), MYSTICAL_CHARGE(44, 3), EXCESS_CHARGE(45, 3), SORCERY(62,3),
	//Warlock T3
	SOUL_EATER(46, 3), SOUL_SIPHON(47, 3), NECROMANCERS_MINIONS(48, 3), WARLOCKS_TOUCH (63, 3),
	//Elemental Blast T4
	BLAST_RADIUS(49, 4), ELEMENTAL_POWER(50, 4), REACTIVE_BARRIER(51, 4),
	//Wild Magic T4
	WILD_POWER(52, 4), FIRE_EVERYTHING(53, 4), CONSERVED_MAGIC(54, 4),
	//Warp Beacon T4
	TELEFRAG(55, 4), REMOTE_BEACON(56, 4), LONGRANGE_WARP(57, 4),

	//Rogue T1
	CACHED_RATIONS(64), THIEFS_INTUITION(65), SUCKER_PUNCH(66), MENDING_SHADOWS(67),
	//Rogue T2
	MYSTICAL_MEAL(68), MYSTICAL_UPGRADE(69), WIDE_SEARCH(70), SILENT_STEPS(71), ROGUES_FORESIGHT(72),
	//Rogue T3
	ENHANCED_RINGS(73, 3), LIGHT_CLOAK(74, 3),
	//Assassin T3
	ENHANCED_LETHALITY(75, 3), ASSASSINS_REACH(76, 3), BOUNTY_HUNTER(77, 3), LETHAL_MOMENTUM_2(94,3),
	//Freerunner T3
	EVASIVE_ARMOR(78, 3), PROJECTILE_MOMENTUM(79, 3), SPEEDY_STEALTH(80, 3), FAST_RECOVERY(95,3), // TODO implement icon for fast recovery
	//Smoke Bomb T4
	HASTY_RETREAT(81, 4), BODY_REPLACEMENT(82, 4), SHADOW_STEP(83, 4),
	//Death Mark T4
	FEAR_THE_REAPER(84, 4), DEATHLY_DURABILITY(85, 4), DOUBLE_MARK(86, 4),
	//Shadow Clone T4
	SHADOW_BLADE(87, 4), CLONED_ARMOR(88, 4), PERFECT_COPY(89, 4),

	//Huntress T1
	NATURES_BOUNTY(96), SURVIVALISTS_INTUITION(97), FOLLOWUP_STRIKE(98), NATURES_AID(99),
	//Huntress T2
	INVIGORATING_MEAL(100), RESTORED_NATURE(101), REJUVENATING_STEPS(102), HEIGHTENED_SENSES(103), DURABLE_PROJECTILES(104),
	//Huntress T3
	POINT_BLANK(105, 3), SEER_SHOT(106, 3),
	//Sniper T3
	FARSIGHT(107, 3), SHARED_ENCHANTMENT(108, 3), SHARED_UPGRADES(109, 3), MULTISHOT(126,3) {{aliases = new String[]{"RANGER"};}},
	//Warden T3
	DURABLE_TIPS(110, 3), BARKSKIN(111, 3), SHIELDING_DEW(112, 3), NATURES_BETTER_AID(127,3),
	//Spectral Blades T4
	FAN_OF_BLADES(113, 4), PROJECTING_BLADES(114, 4), SPIRIT_BLADES(115, 4),
	//Natures Power T4
	GROWING_POWER(116, 4), NATURES_WRATH(117, 4), WILD_MOMENTUM(118, 4),
	//Spirit Hawk T4
	EAGLE_EYE(119, 4), GO_FOR_THE_EYES(120, 4), SWIFT_SPIRIT(121, 4),

	//Duelist T1
	STRENGTHENING_MEAL(128), ADVENTURERS_INTUITION(129), PATIENT_STRIKE(130), AGGRESSIVE_BARRIER(131),
	//Duelist T2
	FOCUSED_MEAL(132), RESTORED_AGILITY(133), WEAPON_RECHARGING(134), LETHAL_HASTE(135), SWIFT_EQUIP(136),
	//Duelist T3
	LIGHTWEIGHT_CHARGE(137, 3), DEADLY_FOLLOWUP(138, 3),
	//Champion T3
	SECONDARY_CHARGE(139, 3), TWIN_UPGRADES(140, 3), COMBINED_LETHALITY(141, 3), ELITE_DEXTERITY(158, 3),
	//Monk T3
	UNENCUMBERED_SPIRIT(142, 3), MONASTIC_VIGOR(143, 3), COMBINED_ENERGY(144, 3), PLACEHOLDER(159),
	//Challenge T4
	CLOSE_THE_GAP(145, 4), INVIGORATING_VICTORY(146, 4), ELIMINATION_MATCH(147, 4),
	//Elemental Strike T4
	ELEMENTAL_REACH(148, 4), STRIKING_FORCE(149, 4), DIRECTED_POWER(150, 4),
	//Duelist A3 T4
	FEIGNED_RETREAT(151, 4), EXPOSE_WEAKNESS(152, 4), COUNTER_ABILITY(153, 4),

	//universal T4
	HEROIC_ENERGY(26, 4) {
		// this is why wrath doesn't have any talents...
		private boolean ratmogrify() {
			// FIXME this is really brittle, will be an issue if/when I add OmniAbility
			return Ratmogrify.useRatroicEnergy
					|| GamesInProgress.selectedClass == HeroClass.RAT_KING
					|| hero != null
						&& (hero.heroClass == HeroClass.RAT_KING
							|| hero.armorAbility instanceof Ratmogrify);
		}
		@Override public int icon() {
			if ( ratmogrify() ) return 218;
			switch (hero != null ? hero.heroClass : GamesInProgress.selectedClass){
				case WARRIOR: default: return 26;
				case MAGE: return 58;
				case ROGUE: return 90;
				case HUNTRESS: return 122;
				case DUELIST: return 154;
				// Rat King handled on line 164
			}
		}

		@Override public String title() {
			//TODO translate this
			if (ratmogrify()) {
				return Messages.get(this, name() + ".rat_title");
			}
			return super.title();
		}
	},

	//Ratmogrify T4
	RATSISTANCE(215, 4), RATLOMACY(216, 4), RATFORCEMENTS(217, 4),
	// TODO add unique icons, really bad now.
	ROYAL_PRIVILEGE(224), // food related talents, uses empowering icon
	ROYAL_INTUITION(225), // intuition-related talents, uses survivalist's icon
	KINGS_WISDOM(226), // on-id + combat talents, uses tested hypothesis
	NOBLE_CAUSE(227), // other ones. uses iron will
	ROYAL_MEAL(228), //// all on-eat talents for tier 2. uses arcane meal
	RESTORATION(229), // all upgrade/potion of healing talents, uses restored willpower icon
	POWER_WITHIN(230), // runic (3), wand preservation (3), rogue's foresight (5), rejuvenating steps (3), uses foresight.
	KINGS_VISION(231), // improvised projectiles (4), arcane vision(4), wide search(3), heightened senses(4)
	PURSUIT(232), // durable projectiles (5),silent steps(4),lethal momentum (3),shield battery(5)
	// Rat King T3
	RK_BERSERKER(233,3), RK_GLADIATOR(234,3), RK_BATTLEMAGE(235,3),
	RK_WARLOCK(236,3), RK_ASSASSIN(237,3), RK_FREERUNNER(238,3), RK_SNIPER(239,3), RK_WARDEN(240,3),
	// placeholders for if/when duelist is implemented into rk
	RK_CHAMPION(241,3), RK_MONK(242,3),
	// Wrath2
	AFTERSHOCK(220,4), RAT_BLAST(221,4), SMOKE_AND_MIRRORS(222,4), SEA_OF_BLADES(223,4);

	protected String[] aliases = new String[0];

	public static abstract class Cooldown extends FlavourBuff {
		public static <T extends Cooldown> void affectHero(Class<T> cls) {
			if(cls == Cooldown.class) return;
			T buff = Buff.affect(hero, cls);
			buff.spend( buff.duration() );
		}
		public abstract float duration();
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / duration(),1); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	}

	// TODO is splitting up t2s arbitrarily really a good idea?
	public static class ImprovisedProjectileCooldown extends Cooldown {
		public float duration() { return hero.hasTalent(IMPROVISED_PROJECTILES) ? 15 : 50; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
	};
	public static class LethalMomentumTracker extends FlavourBuff{
		public static void process() { hero.byTalent(process, PURSUIT); }
		private static final TalentCallback process = (talent, points) -> {
			if( Random.Float() < ( (talent == LETHAL_MOMENTUM ? 2 : 1) + points )
					/ (talent == PURSUIT ? 3f : 4f) ) {
				Buff.prolong(hero, LethalMomentumTracker.class, 1f);
			}
		};
		// check if it applies
		public static boolean apply(Char ch) {
			if(ch.buff(LethalMomentumTracker.class) != null) {
				detach(ch, LethalMomentumTracker.class);
				return true;
			}
			return false;
		}
	};

	// this is my idea of buffing lethal momentum: remove all possible inconsistencies with it.
	public static abstract class RKPD2LethalMomentumTracker extends FlavourBuff {
		{ actPriority = VFX_PRIO; }
		private boolean checkShielding = false, wasTurned; // this is a very specific case, but still needed.

		@Override public boolean attachTo(Char target) {
			// does not bundle.
			if (Char.restoring == target
					|| !tryAttach(target)
					|| target.HP == 0 && target.isAlive() && !(checkShielding = target.shielding() > 0)
					|| !super.attachTo(target)) {
				return false;
			}
			wasTurned = target.buff(AllyBuff.class) != null;
			return true;
		}
		protected abstract boolean tryAttach(Char target);

		public static void process(Char enemy) {
			for(Class<RKPD2LethalMomentumTracker> trackerClass : new Class[]{
					WarriorLethalMomentumTracker.class,
					AssassinLethalMomentumTracker.class}) {
				Buff.append(enemy, trackerClass);
			}
		}

		@Override protected void onRemove() {
			if (target != null &&
					// activates if the enemy was brought to 0 HP this turn.
					(target.HP == 0 && (!checkShielding || target.shielding() == 0) ||
							// also activates if the enemy was corrupted.
							(target.buff(AllyBuff.class) == null) == wasTurned
					)
			) {
				add(() -> {
					hero.timeToNow();
					proc();
				});
			}
		}
		protected void proc() {}

		// template class
		abstract static class Chain extends FlavourBuff {
			{ type = buffType.POSITIVE; }
			@Override public int icon() { return BuffIndicator.CORRUPT; }
			@Override public String desc() {
				String desc = super.desc();
				String effect = Messages.get(this, "effect");
				//noinspection StringEquality
				if(effect != Messages.NO_TEXT_FOUND) desc += "\n" + effect;
				return desc;
			}
		}
	}

	public static class WarriorLethalMomentumTracker extends RKPD2LethalMomentumTracker {
		@Override protected boolean tryAttach(Char target) {
			int points = hero.pointsInTalent(LETHAL_MOMENTUM);
			return points > 0 && points >= Random.Int(3);
		}

		@Override protected void proc() { Buff.affect(hero, Chain.class); }

		public static class Chain extends RKPD2LethalMomentumTracker.Chain // 2x accuracy
		{
			@Override public void tintIcon(Image icon) { icon.invert(); }
		}
	}

	public static class AssassinLethalMomentumTracker extends RKPD2LethalMomentumTracker {
		private Preparation prep = hero.buff(Preparation.class);
		@Override protected boolean tryAttach(Char target) {
			if(prep == null || !hero.hasTalent(LETHAL_MOMENTUM_2)) return false;
			// 60 / 80 / 100% chance to proc
			// todo determine if I should split these mechanics further?
			if(Random.Float() > .2*(2+hero.pointsInTalent(LETHAL_MOMENTUM_2))) return false;
			// preserve half of the levels on average.
			int level = Random.NormalIntRange(0, prep.attackLevel()-1);
			if(level > 0) prep.setAttackLevel(level); else prep = null;
			return true;
		}

		@Override
		protected void proc() {
			if(prep != null) {
				prep.attachTo(hero);
				prep.timeToNow();
				ActionIndicator.setAction(prep);
			}
		}
	}
	public static class StrikingWaveTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{{revivePersists = true;}};
	public static class EmpoweredStrikeTracker extends FlavourBuff{};
	public static class ProtectiveShadowsTracker extends Buff {
		private float incHeal = 1, incShield = 1;

		@Override
		public boolean act() {
			Hero target = (Hero) this.target;
			if (target.invisible > 0) {
				if (target.hasTalent(Talent.MENDING_SHADOWS)
						&& !Buff.affect(target, Hunger.class).isStarving()) {
					// heal every 4/2 turns when not starving. effectively a 1.5x boost to standard protective shadows, plus it doesn't go away.
					incHeal += target.pointsInTalent(Talent.MENDING_SHADOWS) / 4f;
					if (incHeal >= 1 && target.HP < target.HT) {
						incHeal = 0;
						target.HP++;
						target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
					}
				}
				//barrier every 2/1 turns, to a max of 3/5
				if (target.hasTalent(Talent.MENDING_SHADOWS, Talent.NOBLE_CAUSE)) {
					Barrier barrier = Buff.affect(target, Barrier.class);
					int points = target.pointsInTalent(Talent.MENDING_SHADOWS, Talent.NOBLE_CAUSE);
					if (barrier.shielding() < 1 + 2 * points) {
						incShield += 0.5f * points;
					}
					if (incShield >= 1) {
						incShield = 0;
						barrier.incShield(1);
					} else {
						barrier.incShield(0); //resets barrier decay
					}
				}
			} else {
				detach();
			}
			spend( TICK );
			return true;
		}

		private static final String
				BARRIER_INC = "barrier_inc",
				HEAL_INC = "incHeal";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BARRIER_INC, incShield );
			bundle.put( HEAL_INC, incHeal );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			incShield = bundle.getFloat( BARRIER_INC );
			incHeal = bundle.getFloat( HEAL_INC );
		}
	}
	public static class BountyHunterTracker extends FlavourBuff{};
	public static class RejuvenatingStepsCooldown extends Cooldown{
		{ revivePersists = true; }
		@Override public float duration() {
			// if both are present the higher one is used. They don't stack in this implementation.
			int points = hero.shiftedPoints(REJUVENATING_STEPS, POWER_WITHIN);
			if(hero.hasTalent(NATURES_BETTER_AID)) points = max(points, 1);
			return 10*(float)Math.pow(2,1-points);
		}
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
	};
	public static class RejuvenatingStepsFurrow extends CounterBuff{
		{revivePersists = true;}
		/** Track a successful proc of rejuvenating steps.
		 *	Moved logic from Level.java to here so I don't forget what this does.
		 **/
		public static void record() {
			int points = hero.pointsInTalent(false, REJUVENATING_STEPS, POWER_WITHIN);
			if(hero.hasTalent(NATURES_BETTER_AID)) points = max(points, 1);
			count(hero, Talent.RejuvenatingStepsFurrow.class, 3 - points);
		}
	};
	public static class SeerShotCooldown extends Cooldown{
		@Override public float duration() {
			return hero.hasTalent(SEER_SHOT)
					? 15 * hero.pointsInTalent(SEER_SHOT)
					: 20;
		}
		public int icon() { return target.buff(RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
	};
	public static class SpiritBladesTracker extends FlavourBuff{
		// todo should I have enchant have increased proc chances for Wrath?
		public float getModifier() {
			return hero.pointsInTalent(SPIRIT_BLADES, SEA_OF_BLADES) < 4 ? 1f : 1.1f;
		}
		public void setModifier(float modifier) {/* ignored by default */}

		public static float getProcModifier() {
			SpiritBladesTracker tracker = hero.buff(SpiritBladesTracker.class, false);
			return tracker != null ? tracker.getModifier() : 1f;
		}
	};

	public static class PatientStrikeTracker extends FlavourBuff{};
	public static class AggressiveBarrierCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};;
	public static class RestoredAgilityTracker extends FlavourBuff{};
	public static class LethalHasteCooldown extends Cooldown {
		private static boolean enabled = true; // so it doesn't double proc
		public static void applyLethalHaste(Hero hero, boolean viaAbility) {
			if (!hero.hasTalent(LETHAL_HASTE)) return;
			if (!viaAbility && !enabled) {
				enabled = true;
				return;
			}
			float duration = 1.67f + hero.pointsInTalent(Talent.LETHAL_HASTE);
			Buff.prolong(hero, Haste.class, duration);
			Buff.prolong(hero, Adrenaline.class, duration); // :D
			if (viaAbility) {
				enabled = false; // prevent double proccing
				// using it as intended removes the cooldown.
				// You'll always be able to use it at least once in a chain. Very balanced I know.
				detach(hero, LethalHasteCooldown.class);
			} else affectHero(LethalHasteCooldown.class);
		}
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		@Override
		public float duration() {
			return hero.heroClass == HeroClass.DUELIST ? 100 : 50;
		}
	};
	public static class SwiftEquipCooldown extends FlavourBuff{
		public boolean secondUse;
		public boolean hasSecondUse(){
			return secondUse && cooldown() > 14f;
		}

		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) {
			if (hasSecondUse()) icon.hardlight(0.85f, 0f, 1.0f);
			else                icon.hardlight(0.35f, 0f, 0.7f);
		}
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / 20f, 1); }
		private static final String SECOND_USE = "second_use";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SECOND_USE, secondUse);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			secondUse = bundle.getBoolean(SECOND_USE);
		}
	};
	public static class DeadlyFollowupTracker extends FlavourBuff{};
	public static class CombinedLethalityAbilityTracker extends FlavourBuff{
		public MeleeWeapon weapon;
	};
	public static class CombinedLethalityTriggerTracker extends FlavourBuff{
		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.CORRUPT; }
		public void tintIcon(Image icon) { icon.hardlight(0.6f, 0.15f, 0.6f); }
	};
	public static class CombinedEnergyAbilityTracker extends FlavourBuff{
		public int energySpent = -1;
		public boolean wepAbilUsed = false;
	}
	public static class CounterAbilityTacker extends FlavourBuff{};

	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21/*+4*/, 31};

	public static int getMaxPoints(int tier) {
		int max = tierLevelThresholds[tier+1] - tierLevelThresholds[tier];
		if(tier == 3) max += 4;
		return max;
	}

	Talent( int icon ){
		this(icon, 2);
	}

	Talent( int icon, int maxPoints ){
		this.icon = icon;
		this.maxPoints = maxPoints;
	}

	public interface TalentCallback {
		void call(Talent talent, int points);
	}

	public int icon(){
		return icon;
	}

	public int maxPoints(){
		return maxPoints;
	}

	public String title(){
		return Messages.get(this, name() + ".title");
	}

	public final String desc(){
		return desc(false);
	}

	// this is an absurd way to check but it..works.
	public boolean isClassTalent(HeroClass cls) {
		ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();
		initClassTalents(cls, talents);
		for (LinkedHashMap<Talent, Integer> tier : talents) if (tier.containsKey(this)) return true;
		return false;
	}

    // fixme there's gotta be a way to truncate the sheer amount of extra text that's about to show up.
    // todo also should decide if I want the comment to show up before or after the meta desc. currently it is set after
	public String desc(boolean metamorphed){
        String desc = Messages.get(this, name() + ".desc");
		if (metamorphed){
			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)){
				desc += "\n\n" + metaDesc;
			}
		}
		String comment = Messages.get(this, name() + ".comment");
		//noinspection StringEquality
		return comment == Messages.NO_TEXT_FOUND ? desc : desc + "\n\n" + comment;
	}

	public static void onTalentUpgraded( Hero hero, Talent talent){
		int points = hero.pointsInTalent(talent);
		switch(talent) {
		    case IRON_WILL: case NOBLE_CAUSE:
				// lazily implementing this without checking hero class.
				Buff.affect(hero, BrokenSeal.WarriorShield.class);
				break;
			case VETERANS_INTUITION:
			case ADVENTURERS_INTUITION:
			case THIEFS_INTUITION:
			case ROYAL_INTUITION:
				for (Item item : hero.belongings) {
					// rerun these.
					item.collected = false;
					onItemCollected(hero, item);
					if (item.isEquipped(hero)) onItemEquipped(hero, item);
				}
				break;
			case SCHOLARS_INTUITION:
				for (Item item : hero.belongings) {
					if (item instanceof Scroll || item instanceof Potion) {
						for (int i = 0; i < item.quantity() && !item.isIdentified(); i++) {
							if (Random.Int(3 * points) == 0)
								item.identify(); // adjusts for the difference in chance.
						}
					}
				}
				break;
			case LIGHT_CLOAK:
				// shpd does a check to make sure it's the rogue before doing this, but I don't see why I should bother checking.
				break;
			case RK_FREERUNNER:
				if (hero.pointsInTalent(LIGHT_CLOAK, RK_FREERUNNER) == 1) {
					for (Item item : hero.belongings.backpack) {
						if (item instanceof CloakOfShadows) {
							if (hero.buff(LostInventory.class) == null || item.keptThoughLostInvent) {
								((CloakOfShadows) item).activate(hero);
							}
						}
					}
				}
				break;
//			case BERSERKING_STAMINA: // takes immediate effect
//				Berserk berserk = hero.buff(Berserk.class);
//				if(berserk != null) berserk.recover(Berserk.STAMINA_REDUCTION);
//				break;
			case SEER_SHOT:
				float mod = points == 1 ? 0 : 1f/(points-1);
				for(RevealedArea buff : hero.buffs(RevealedArea.class)) buff.postpone(buff.cooldown() * mod);
				break;
			case HEIGHTENED_SENSES: case FARSIGHT: case RK_SNIPER: case KINGS_VISION:
				Dungeon.observe();
				break;
			case SECONDARY_CHARGE: case TWIN_UPGRADES:
				Item.updateQuickslot();
				break;
			case UNENCUMBERED_SPIRIT:
				if (points < 3) break;
				Item toGive = new ClothArmor().identify();
				if (!toGive.collect()){
					Dungeon.level.drop(toGive, hero.pos).sprite.drop();
				}
				toGive = new Gloves().identify();
				if (!toGive.collect()) {
					Dungeon.level.drop(toGive, hero.pos).sprite.drop();
					break;
				}
		}
	}

	public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}};
	public static class NatureBerriesAvailable extends CounterBuff{{revivePersists = true;}}; //for pre-1.3.0 saves
	public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		final int[] heartyMeal = new int[2];
		hero.byTalent( (talent, points) -> {
			// somehow I managed to make it even more confusing than before.
			int factor = talent == HEARTY_MEAL ? 3 : 4;
			double missingHP = 1-(double)hero.HP/hero.HT;
			int strength = (int)(missingHP * factor);
			if(talent != HEARTY_MEAL) strength--; // missing 1/4 hp is not rewarded with healing normally.
			if(strength-- == 0) return; // adjusting for the addition of one point.
			strength += points;
			// hearty meal heals for (2.5/4)/(4/6). priv heals for (2/3)/(3/5)
			int boost = talent == HEARTY_MEAL && strength == 1
					? Random.round(2.5f) // simulate 2.5
					: (int) Math.ceil( (talent == HEARTY_MEAL ? 2.5 : 2) * Math.pow(1.5,strength-1) );
			heartyMeal[0] += boost;
			heartyMeal[1] += Math.round(boost*2f/3);
		}, ROYAL_PRIVILEGE, HEARTY_MEAL);
		if(heartyMeal[0] > 0) {
			hero.HP += heartyMeal[0];
			hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), heartyMeal[1]);
		}
		if (hero.hasTalent(IRON_STOMACH,ROYAL_MEAL)){
			if (hero.cooldown() > 0) {
				Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
			}
		}
		boolean charge = false;
		if (hero.hasTalent(ROYAL_PRIVILEGE)){ // SHPD empowering meal talent
			//2/3 bonus wand damage for next 3 zaps
			int bonus = 1+hero.pointsInTalent(ROYAL_PRIVILEGE);
			Buff.affect( hero, WandEmpower.class).set(bonus, 3);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL_I,ROYAL_MEAL)) {
			//5/8 turns of recharging.
			int points = hero.pointsInTalent(ENERGIZING_MEAL_I,ROYAL_MEAL);
			int duration = 2 + 3*points;
			if(hero.hasTalent(ENERGIZING_MEAL_I)) Buff.affect( hero, Recharging.class, duration);
			if(hero.hasTalent(ROYAL_MEAL)) Buff.prolong(hero, Recharging.class, duration);
			charge = true;
		}
		if (hero.hasTalent(ENERGIZING_MEAL_II)) {
			// 1/1.5 charges instantly replenished.
			hero.belongings.charge(0.5f*(1+hero.pointsInTalent(ENERGIZING_MEAL_II)),true);
			charge = true;
		}
		if(charge) ScrollOfRecharging.charge(hero);

		hero.byTalent( (talent, points) -> {
			//3/5 turns of recharging
			int duration = 1 + 2*points;
			ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
			boolean newBuff = false;
			if(talent == MYSTICAL_MEAL) {
				if(buff.left() == 0) newBuff = true;
				buff.prolong((float)Math.ceil(duration*1.5)); // 5-8 turns of recharge!!!
			}
			else if(buff.left() < duration){
				newBuff = true;
				buff.set(duration);
			}
			if(newBuff) buff.ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
		}, ROYAL_MEAL, MYSTICAL_MEAL );

		// 4.5/6 tiles -> 3/5 turns
		hero.byTalent( (talent, points) -> Buff.affect(hero, Adrenaline.class, 2+2*points),
				INVIGORATING_MEAL);
		hero.byTalent( (talent, points) -> {
			//effectively 1/2 turns of haste
			Buff.prolong( hero, Haste.class, 0.67f+points);
			hero.sprite.emitter().burst(Speck.factory(Speck.JET), 4*points);
		}, ROYAL_MEAL);
			if (hero.hasTalent(STRENGTHENING_MEAL)){
				//3 bonus physical damage for next 2/3 attacks
				Buff.affect( hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(STRENGTHENING_MEAL));
			}
			if (hero.hasTalent(FOCUSED_MEAL)){
				if (hero.heroClass == HeroClass.DUELIST){
					//1/1.5 charge for the duelist
					Buff.affect( hero, MeleeWeapon.Charger.class ).gainCharge(0.5f*(hero.pointsInTalent(FOCUSED_MEAL)+1));
				} else {
					// lvl/3 / lvl/2 bonus dmg on next hit for other classes
					Buff.affect( hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(FOCUSED_MEAL))), 1);
				}
			}
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

	// royal intuition is additive, separate talents are multiplictive, however.
	public static float itemIDSpeedFactor( Hero hero, Item item ){
		float factor = 1f;

		// all royal intuition is now handled here.
		factor *= 1 + hero.pointsInTalent(ROYAL_INTUITION) * (0.75f + (
				item instanceof MeleeWeapon || item instanceof Armor ? 2 // armsmaster
						: item instanceof Ring ? 2 // thief's intuition
						: item instanceof Wand ? 3 // scholar's intuition
						: 0));

		factor *= 1 + 0.75f * 1.5f * hero.pointsInTalent(SURVIVALISTS_INTUITION);
		// 2x innate (+0) / instant for Warrior (see onItemEquipped)
		/*
		if (item instanceof MeleeWeapon || item instanceof Armor){
				factor *= 1f + hero.shiftedPoints(ARMSMASTERS_INTUITION);
		}
		*/
		// Affected by both Warrior(1.75x/2.5x) and Duelist(2.5x/inst.) talents
		if (item instanceof MeleeWeapon){
			factor *= 1f + 1.5f*hero.pointsInTalent(ADVENTURERS_INTUITION); //instant at +2 (see onItemEquipped)
			factor *= 1f + 0.75f*hero.pointsInTalent(VETERANS_INTUITION);
		}
		// Affected by both Warrior(2.5x/inst.) and Duelist(1.75x/2.5x) talents
		if (item instanceof Armor){
		    factor *= 1f + 0.75f*hero.pointsInTalent(ADVENTURERS_INTUITION);
		    factor *= 1f + hero.pointsInTalent(VETERANS_INTUITION); //instant at +2 (see onItemEquipped)
		}
		// 3x/instant for Mage (see Wand.wandUsed()), 4.5x/instant for rk
		// not shifted for Mage right now.
		if (item instanceof Wand){
			factor *= 1f + 2.0f*hero.pointsInTalent(SCHOLARS_INTUITION);
		}
		// 2x/instant for Rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.shiftedPoints(THIEFS_INTUITION);
		}
		return factor;
	}

	public static void onHealingPotionUsed( Hero hero ){
		if (hero.hasTalent(RESTORED_WILLPOWER,RESTORATION)){
			float[] multiplier = {0/*seal*/,0/*noSeal*/};
			hero.byTalent( (talent, points) -> {
				points++;
				multiplier[0] += points/(talent == RESTORED_WILLPOWER ? 2f : 3f);
				float metamorphShield = talent == RESTORED_WILLPOWER ? 0.04f : 0.025f;
				multiplier[1] += metamorphShield * points;
			}, RESTORED_WILLPOWER, RESTORATION);
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			int shieldToGive;
			if (shield != null) {
				shieldToGive = Math.round(shield.maxShield()*multiplier[0]);
				shield.supercharge(shieldToGive);
			} else {
				shieldToGive = Math.round(hero.HT * multiplier[1]);
				Buff.affect(hero, Barrier.class).setShield(shieldToGive);
			}
		}
		if (hero.hasTalent(RESTORED_NATURE,RESTORATION)){

			if(hero.hasTalent(RESTORED_NATURE)) {
				float time = Math.min(hero.cooldown(), Potion.TIME_TO_DRINK);
				if(time > 0) hero.spend(-time);
			}

			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				grassCells.add(hero.pos+i);
			}
			Random.shuffle(grassCells);
			for (int cell : grassCells){
				Char ch = Actor.findChar(cell);
				if (ch != null && ch.alignment == Char.Alignment.ENEMY){
					int duration = 1+hero.pointsInTalent(false, RESTORED_NATURE,RESTORATION);
					// please do note that this can be stacked, assuming you throw away healing or shielding, of course.
					Buff.affect(ch, Roots.class, duration);
				}
				if (Dungeon.level.map[cell] == Terrain.EMPTY ||
						Dungeon.level.map[cell] == Terrain.EMBERS ||
						Dungeon.level.map[cell] == Terrain.EMPTY_DECO){
					Level.set(cell, Terrain.GRASS);
					GameScene.updateMap(cell);
				}
				HighGrass.playVFX(cell);
			}
			int grassToSpawn = 2 + 3*hero.pointsInTalent(false, RESTORED_NATURE, RESTORATION); // 5/8
			if( hero.hasTalent(RESTORED_NATURE) ) {
				grassCells.add(hero.pos); // it can spawn in the hero's position if huntress's talent.
			}
			for (int cell : grassCells){
				if(grassToSpawn == 0) break;
				if( HighGrass.plant(cell) ) {
					grassToSpawn--;
					if(cell == hero.pos) HighGrass.playVFX(cell); // it wasn't played before.
				}
			}
			Dungeon.observe();
		}
		if (hero.hasTalent(RESTORED_AGILITY)){
			Buff.prolong(hero, RestoredAgilityTracker.class, hero.cooldown());
		}
	}

	public static void onUpgradeScrollUsed( Hero hero ){
		// fixme it's probably going to look like someone threw up spellsprites
		if (hero.hasTalent(ENERGIZING_UPGRADE,RESTORATION)){
			int charge = 2+2*hero.pointsInTalent(ENERGIZING_UPGRADE);
			MagesStaff staff = hero.belongings.getItem(MagesStaff.class);
			int pointDiff = hero.pointsInTalent(RESTORATION) - hero.pointsInTalent(ENERGIZING_UPGRADE);
			boolean charged = false;
			if(hero.hasTalent(ENERGIZING_UPGRADE)) {
				hero.belongings.charge(charge, true);
			}
			if (staff != null && pointDiff > 0){
				staff.gainCharge( charge + 2*pointDiff, true);
				charged = true;
			}
			if(staff == null && hero.hasTalent(RESTORATION)) {
			    Buff.affect(hero, Recharging.class, 4 + 8 * hero.pointsInTalent(RESTORATION));
			    charged = true;
			}
			if(charged) {
				ScrollOfRecharging.charge(hero);
				SpellSprite.show( hero, SpellSprite.CHARGE );
			}
		}
		if (hero.hasTalent(MYSTICAL_UPGRADE,RESTORATION)){
			boolean charge = false;
			if(hero.hasTalent(MYSTICAL_UPGRADE)) {
				for(Artifact.ArtifactBuff buff : hero.buffs(Artifact.ArtifactBuff.class)) {
					if(buff.artifactClass() != CloakOfShadows.class) {
						buff.charge(hero,4*(1+hero.pointsInTalent(MYSTICAL_UPGRADE))); // 8/12 turns sounds legit...
						charge = true;
					}
				}
			}
			CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
			if (cloak != null){
				cloak.overCharge(1+hero.pointsInTalent(false, MYSTICAL_UPGRADE, RESTORATION));
				charge = true;
			} else if(hero.hasTalent(RESTORATION)) {
			    Buff.affect(hero, ArtifactRecharge.class).set( 2 + 4*hero.pointsInTalent(RESTORATION) ).ignoreHornOfPlenty = false;
			    charge = true;
			}
			if(charge) {
				ScrollOfRecharging.charge(hero);
				SpellSprite.show( hero, SpellSprite.CHARGE, 0, 1, 1 );
			}
		}
	}

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS,RK_ASSASSIN)){
			float duration = 3f*hero.pointsInTalent(ENHANCED_RINGS,RK_ASSASSIN);
			if(hero.hasTalent(ENHANCED_RINGS)) Buff.affect(hero, EnhancedRings.class, duration);
			else Buff.prolong(hero, EnhancedRings.class, duration);
		}
	}
	public static void onItemEquipped( Hero hero, Item item ) {
		if (item.isIdentified()) return; // all talent interactions here regard identification
		boolean id = false;
		if (hero.pointsInTalent(ROYAL_INTUITION) == 2) {
			id = true;
		} else {
			// Armsmaster
			Talent shiftedTalent, otherTalent;
			if (item instanceof Armor) {
				shiftedTalent = VETERANS_INTUITION;
				otherTalent = ADVENTURERS_INTUITION;
			} else if (item instanceof Weapon) {
				shiftedTalent = ADVENTURERS_INTUITION;
				otherTalent = VETERANS_INTUITION;
			} else {
				shiftedTalent = otherTalent = null;
			}
			if (shiftedTalent != null) {
				// +1 for dedicated talent, +1 otherwise.
				id = hero.shiftedPoints(shiftedTalent, otherTalent) == 2;
			} else if (item instanceof Ring) {
				// Thief's Intuition
				switch(hero.shiftedPoints(THIEFS_INTUITION, ROYAL_INTUITION)) {
					case 2: case 3: id = true; break;
					case 1: ((Ring) item).setKnown(); break;
				}
			}
		}
		if (id) {
			item.identify();
			if (hero.sprite.emitter() != null) hero.sprite.emitter().burst(
					Speck.factory(Speck.QUESTION),
					1
			);
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if(item.isIdentified() || item.collected) return;
		item.collected = true;
		boolean id = false, curseID = item.cursedKnown;
		if (item instanceof Ring || item instanceof Artifact) {
			switch (hero.shiftedPoints(THIEFS_INTUITION, ROYAL_INTUITION)) {
				case 3: id = true; break;
				case 2:
					if (item instanceof Ring) ((Ring) item).setKnown();
					// +1 effect has a chance to id curses if it's actually cursed.
					curseID = curseID || hero.hasTalent(THIEFS_INTUITION) && item.cursed
							&& Random.Int(3) == 0;
					break;
			}
		}
		if (item instanceof Weapon || item instanceof Armor) {
			for (Talent talent : new Talent[]{VETERANS_INTUITION, ADVENTURERS_INTUITION}) {
				int points = hero.pointsInTalent(talent)-1; // we care about +1 +2 boosted +2
				// match talent to equipment type gets boost
				if (talent == VETERANS_INTUITION ^ item instanceof Weapon) points++;
				// 0%/0%/30%/60% to identify on pick-up with armsmaster talents
				if (0.3f * points > Random.Float()) {
					id = true;
					break;
				}
			}
		}
		// survivalist's intuition curse checking (.2/.4 chance)
		curseID = curseID || (item instanceof Wand || item instanceof EquipableItem && !(item instanceof MissileWeapon))
				&& Random.Int(5) < hero.pointsInTalent(SURVIVALISTS_INTUITION);
		// scholar's intuition consumable identification
		id = id || (item instanceof Scroll || item instanceof Potion)
				&& hero.hasTalent(SCHOLARS_INTUITION)
				&& Random.Int(4-hero.pointsInTalent(SCHOLARS_INTUITION)) == 0;

		if(id || curseID && !item.cursedKnown) {
			if (id) item.identify();
			else {
				// fixme this doesn't use .properties file.
				GLog.w("The %s is %s",
						item.name(),
						item.visiblyCursed() ? "cursed!" : "free of malevolent magic.");
			}
			if (hero.sprite.emitter() != null) hero.sprite.emitter().burst(
					Speck.factory(Speck.QUESTION),1);
		}
	}

	//note that IDing can happen in alchemy scene, so be careful with VFX here
	// near-identical talents in this area do stack, they're simple enough where it's really quite trivial to do so.
	public static void onItemIdentified( Hero hero, Item item ){
		int heal = 0;
		for(Talent talent : new Talent[]{TEST_SUBJECT, KINGS_WISDOM}) {
			//heal for 2/3 HP
			int points = hero.pointsInTalent(talent);
			if( points == 0 ) continue;
			heal += 1 + points;
			if(talent == TEST_SUBJECT) heal += points == 1 ? Random.Int(2) : 1; // 2-3/4
		}
		heal = Math.min(heal, hero.HT-hero.HP);
		if(heal > 0) {
			hero.HP += heal;
			if(hero.sprite != null) {
				Emitter e = hero.sprite.emitter();
				if (e != null) e.burst(Speck.factory(Speck.HEALING), max(1,Math.round(heal*2f/3)));
			}
		}
		hero.byTalent( (talent, points) -> {
			//2/3 turns of wand recharging
			int duration = 1 + points;
			if(talent == TESTED_HYPOTHESIS) duration = (int)Math.ceil(duration*1.5f); // 3/5
			Buff.affect(hero, Recharging.class, duration);
			ScrollOfRecharging.charge(hero);
		}, TESTED_HYPOTHESIS, KINGS_WISDOM);
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){
		if (hero.hasTalent(Talent.SUCKER_PUNCH,KINGS_WISDOM)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(SuckerPunchTracker.class) == null){
			int bonus = 0;
			if(hero.hasTalent(SUCKER_PUNCH)) bonus += 1+hero.pointsInTalent(SUCKER_PUNCH);  // 2/3
			if(hero.hasTalent(KINGS_WISDOM)) bonus += Random.round(0.5f*(2+hero.pointsInTalent(KINGS_WISDOM))); // 1-2/2
			dmg += bonus;
			Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE,KINGS_WISDOM)) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				Buff.affect(enemy, FollowupStrikeTracker.class);
			} else if (enemy.buff(FollowupStrikeTracker.class) != null){
				int bonus = 0;
				if(hero.hasTalent(KINGS_WISDOM)) bonus += hero.shiftedPoints(KINGS_WISDOM); // 2/3
				if(hero.hasTalent(FOLLOWUP_STRIKE)) bonus += Random.round(hero.shiftedPoints(FOLLOWUP_STRIKE) * 1.5f); // 3/4-5
				dmg += bonus;
				if (!(enemy instanceof Mob) || !((Mob) enemy).surprisedBy(hero)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
				}
				enemy.buff(FollowupStrikeTracker.class).detach();
			}
		}

		/* fixme figure out if this is needed
		if (hero.buff(Talent.SpiritBladesTracker.class) != null
				&& Random.Int(10) < 3*hero.pointsInTalent(Talent.SPIRIT_BLADES)){
			SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
			if (bow != null) dmg = bow.proc( hero, enemy, dmg );
			hero.buff(Talent.SpiritBladesTracker.class).detach();
		}*/

		if (hero.hasTalent(PATIENT_STRIKE)){
			if (hero.buff(PatientStrikeTracker.class) != null
					&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)){
				hero.buff(PatientStrikeTracker.class).detach();
				dmg += Random.IntRange(hero.pointsInTalent(Talent.PATIENT_STRIKE), 2);
				if (!(enemy instanceof Mob) || !((Mob) enemy).surprisedBy(hero)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
				}
			}
		}

		if (hero.hasTalent(DEADLY_FOLLOWUP)) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				Buff.prolong(enemy, DeadlyFollowupTracker.class, 5f);
			} else if (enemy.buff(DeadlyFollowupTracker.class) != null){
				dmg = Math.round(dmg * (1.0f + .08f*hero.pointsInTalent(DEADLY_FOLLOWUP)));
				if (!(enemy instanceof Mob) || !((Mob) enemy).surprisedBy(hero)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
				}
			}
		}

		return dmg;
	}

	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends Buff{};

	public static final int MAX_TALENT_TIERS = 4;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents, hero.metamorphedTalents );
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents){
		initClassTalents( cls, talents, new LinkedHashMap<>());
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements ){
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 1
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, TEST_SUBJECT, IRON_WILL);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL_I, SCHOLARS_INTUITION, TESTED_HYPOTHESIS, BACKUP_BARRIER);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, CACHED_RATIONS, THIEFS_INTUITION, SUCKER_PUNCH, MENDING_SHADOWS);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, NATURES_BOUNTY, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, STRENGTHENING_MEAL, ADVENTURERS_INTUITION, PATIENT_STRIKE, AGGRESSIVE_BARRIER);
				break;
			case RAT_KING:
				Collections.addAll(tierTalents, ROYAL_PRIVILEGE, ROYAL_INTUITION, KINGS_WISDOM, NOBLE_CAUSE);
				break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, IRON_STOMACH, RESTORED_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL_II, ENERGIZING_UPGRADE, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, MYSTICAL_MEAL, MYSTICAL_UPGRADE, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, INVIGORATING_MEAL, RESTORED_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, FOCUSED_MEAL, RESTORED_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP);
				break;
			case RAT_KING:
				Collections.addAll(tierTalents, ROYAL_MEAL, RESTORATION, POWER_WITHIN, KINGS_VISION, PURSUIT);
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(1).put(talent, 0);
		}
		tierTalents.clear();

		//tier 3
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, HOLD_FAST, STRONGMAN);
				break;
			case MAGE:
				Collections.addAll(tierTalents, EMPOWERING_SCROLLS, ALLY_WARP);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, POINT_BLANK, SEER_SHOT);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, LIGHTWEIGHT_CHARGE, DEADLY_FOLLOWUP);
				break;
			case RAT_KING: break; // no unique talents... :(
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

		//tier4
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		initSubclassTalents( hero.subClass, hero.talents );
	}

	public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (cls == HeroSubClass.NONE) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 3
		switch (cls){
			case BERSERKER: default:
				Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST, ONE_MAN_ARMY);
				break;
			case GLADIATOR:
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO, SKILL);
				break;
			case BATTLEMAGE:
				Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE, SORCERY);
				break;
			case WARLOCK:
				Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS, WARLOCKS_TOUCH);
				break;
			case ASSASSIN:
				Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER, LETHAL_MOMENTUM_2);
				break;
			case FREERUNNER:
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH, FAST_RECOVERY);
				break;
			case SNIPER:
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES, MULTISHOT);
				break;
			case WARDEN:
				Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, SHIELDING_DEW,
				    NATURES_BETTER_AID);
				break;
		    case CHAMPION:
				Collections.addAll(tierTalents, SECONDARY_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY);
				break;
		    case MONK:
				Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY);
				break;
			case KING: // this should be *lovely*
				Collections.addAll(tierTalents, RK_BERSERKER, RK_BATTLEMAGE, RK_ASSASSIN, RK_SNIPER, RK_GLADIATOR, RK_WARLOCK, RK_FREERUNNER, RK_WARDEN);
		}
		for (Talent talent : tierTalents){
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

	}

	public static void initArmorTalents( Hero hero ){
		initArmorTalents( hero.armorAbility, hero.talents);
	}
	public static ArrayList<LinkedHashMap<Talent, Integer>> initArmorTalents(ArmorAbility abil){
		return initArmorTalents(abil, new ArrayList());
	}
	public static ArrayList<LinkedHashMap<Talent, Integer>> initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (abil == null) return talents;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()){
			talents.get(3).put(t, 0);
		}
		return talents;
	}

	private static final String TALENT_TIER = "talents_tier_";

	public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = new Bundle();

			for (Talent talent : tier.keySet()){
				if (tier.get(talent) > 0){
					tierBundle.put(talent.name(), tier.get(talent));
				}
				if (tierBundle.contains(talent.name())){
					tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
				}
			}
			bundle.put(TALENT_TIER+(i+1), tierBundle);
		}

		Bundle replacementsBundle = new Bundle();
		for (Talent t : hero.metamorphedTalents.keySet()){
			replacementsBundle.put(t.name(), hero.metamorphedTalents.get(t));
		}
		bundle.put("replacements", replacementsBundle);
	}

	private static final HashMap<String, String> renamedTalents = new HashMap(){{
	    put("ARMSMASTERS_INTUITION",     "VETERANS_INTUITION");
	}};

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (bundle.contains("replacements")){
			Bundle replacements = bundle.getBundle("replacements");
			for (String key : replacements.getKeys()){
				String value = replacements.getString(key);
				if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
				if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
				hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
			}
		}

		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;

			if (tierBundle != null){
				for (String tName : tierBundle.getKeys()){
					int points = tierBundle.getInt(tName);
					if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
					try {
						Talent talent = Talent.valueOf(tName);
						if (tier.containsKey(talent)) {
							tier.put(talent, Math.min(points, talent.maxPoints()));
						}
					} catch (Exception e){
						ShatteredPixelDungeon.reportException(e);
					}
				}
			}
		}
	}

}
