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
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Cooldown;
import com.zrp200.rkpd2.actors.buffs.CounterBuff;
import com.zrp200.rkpd2.actors.buffs.EnhancedRings;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.actors.buffs.GreaterHaste;
import com.zrp200.rkpd2.actors.buffs.Haste;
import com.zrp200.rkpd2.actors.buffs.Hunger;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.buffs.Invulnerability;
import com.zrp200.rkpd2.actors.buffs.PhysicalEmpower;
import com.zrp200.rkpd2.actors.buffs.Preparation;
import com.zrp200.rkpd2.actors.buffs.Recharging;
import com.zrp200.rkpd2.actors.buffs.RevealedArea;
import com.zrp200.rkpd2.actors.buffs.Roots;
import com.zrp200.rkpd2.actors.buffs.ScrollEmpower;
import com.zrp200.rkpd2.actors.buffs.WandEmpower;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.actors.hero.abilities.Ratmogrify;
import com.zrp200.rkpd2.actors.hero.spells.DivineSense;
import com.zrp200.rkpd2.actors.hero.spells.RecallInscription;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.Flare;
import com.zrp200.rkpd2.effects.FloatingText;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.effects.SpellSprite;
import com.zrp200.rkpd2.items.BrokenSeal;
import com.zrp200.rkpd2.items.EquipableItem;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.Armor;
import com.zrp200.rkpd2.items.armor.ClothArmor;
import com.zrp200.rkpd2.items.artifacts.Artifact;
import com.zrp200.rkpd2.items.artifacts.CloakOfShadows;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.items.artifacts.HornOfPlenty;
import com.zrp200.rkpd2.items.potions.Potion;
import com.zrp200.rkpd2.items.rings.Ring;
import com.zrp200.rkpd2.items.scrolls.Scroll;
import com.zrp200.rkpd2.items.scrolls.ScrollOfRecharging;
import com.zrp200.rkpd2.items.scrolls.ScrollOfUpgrade;
import com.zrp200.rkpd2.items.stones.Runestone;
import com.zrp200.rkpd2.items.stones.StoneOfIntuition;
import com.zrp200.rkpd2.items.trinkets.ShardOfOblivion;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.melee.Gloves;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.items.weapon.missiles.MissileWeapon;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.levels.features.HighGrass;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), VETERANS_INTUITION(1), PROVOKED_ANGER(2), IRON_WILL(3),
	//Warrior T2
	IRON_STOMACH(4), LIQUID_WILLPOWER(5), RUNIC_TRANSFERENCE(6), LETHAL_MOMENTUM(7), IMPROVISED_PROJECTILES(8),
	//Warrior T3
	HOLD_FAST(9, 3), STRONGMAN(10, 3),
	//Berserker T3
	// yes, yes, I kept both talents and then swapped their icons... deal with it. Why does the one that protects you from death have a + next to it?
	ENDLESS_RAGE(11, 3), DEATHLESS_FURY(28, 3), BERSERKING_STAMINA(12, 3), ENRAGED_CATALYST(13, 3), ONE_MAN_ARMY(29,3),
	//Gladiator T3
	CLEAVE(14, 3), LETHAL_DEFENSE(15, 3), ENHANCED_COMBO(16, 3), SKILL(30, 3), EVERYTHING_IS_A_WEAPON(31, 3),
	//Heroic Leap T4
	BODY_SLAM(17, 4), IMPACT_WAVE(18, 4), DOUBLE_JUMP(19, 4),
	//Shockwave T4
	EXPANDING_WAVE(20, 4), STRIKING_WAVE(21, 4), SHOCK_FORCE(22, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION(23, 4), SHRUG_IT_OFF(24, 4), EVEN_THE_ODDS(25, 4),

	//Mage T1
	ENERGIZING_MEAL_I(36), SCHOLARS_INTUITION(33), LINGERING_MAGIC(34), BACKUP_BARRIER(35),
	//Mage T2
	ENERGIZING_MEAL_II(36), INSCRIBED_POWER(37), WAND_PRESERVATION(38), ARCANE_VISION(39), SHIELD_BATTERY(40),
	//Mage T3
	DESPERATE_POWER(41, 3), ALLY_WARP(42, 3),
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
	MYSTICAL_MEAL(68), INSCRIBED_STEALTH(69), WIDE_SEARCH(70), SILENT_STEPS(71), ROGUES_FORESIGHT(72),
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
	INVIGORATING_MEAL(100), LIQUID_NATURE(101), REJUVENATING_STEPS(102), HEIGHTENED_SENSES(103), DURABLE_PROJECTILES(104),
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
	FOCUSED_MEAL(132), LIQUID_AGILITY(133), WEAPON_RECHARGING(134), LETHAL_HASTE(135), SWIFT_EQUIP(136),
	//Duelist T3
	PRECISE_ASSAULT(137, 3), DEADLY_FOLLOWUP(138, 3),
	//Champion T3
	VARIED_CHARGE(139, 3), TWIN_UPGRADES(140, 3), COMBINED_LETHALITY(141, 3), ELITE_DEXTERITY(158, 3),
	//Monk T3
	UNENCUMBERED_SPIRIT(142, 3), MONASTIC_VIGOR(143, 3), COMBINED_ENERGY(144, 3), MONASTIC_MIGHT(159, 3),
	//Challenge T4
	CLOSE_THE_GAP(145, 4), INVIGORATING_VICTORY(146, 4), ELIMINATION_MATCH(147, 4),
	//Elemental Strike T4
	ELEMENTAL_REACH(148, 4), STRIKING_FORCE(149, 4), DIRECTED_POWER(150, 4),
	//Feint T4
	FEIGNED_RETREAT(151, 4), EXPOSE_WEAKNESS(152, 4), COUNTER_ABILITY(153, 4),

	//Cleric T1
	SATIATED_SPELLS(160), HOLY_INTUITION(161), SEARING_LIGHT(162), SHIELD_OF_LIGHT(163),
	//Cleric T2
	ENLIGHTENING_MEAL(164), RECALL_INSCRIPTION(165), SUNRAY(166), DIVINE_SENSE(167), BLESS(168),
	//Cleric T3
	CLEANSE(169, 3), LIGHT_READING(170, 3),
	//Priest T3
	HOLY_LANCE(171, 3), HALLOWED_GROUND(172, 3), MNEMONIC_PRAYER(173, 3), DIVINE_ADVENT(188, 3), ENDURING_LIGHT(189, 3),
	//Paladin T3
	LAY_ON_HANDS(174, 3), AURA_OF_PROTECTION(175, 3), WALL_OF_LIGHT(176, 3), LIMIT_BREAK(190, 3), TRIAGE(191, 3),
	//Ascended Form T4
	DIVINE_INTERVENTION(177, 4), JUDGEMENT(178, 4), FLASH(179, 4),
	//Trinity T4
	BODY_FORM(180, 4), MIND_FORM(181, 4), SPIRIT_FORM(182, 4),
	//Power of Many T4
	BEAMING_RAY(183, 4), LIFE_LINK(184, 4), STASIS(185, 4),

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
                case CLERIC: return 186;
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
	AFTERSHOCK(RK_MONK.icon+10,4), RAT_BLAST(AFTERSHOCK.icon+1,4), SMOKE_AND_MIRRORS(AFTERSHOCK.icon+2,4), SEA_OF_BLADES(AFTERSHOCK.icon+3,4);

	protected String[] aliases = new String[0];

	// TODO is splitting up t2s arbitrarily really a good idea?
	public static class ImprovisedProjectileCooldown extends Cooldown {
		public float duration() { return hero.hasTalent(IMPROVISED_PROJECTILES) ? 25 : 50; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
	};
	public static class LethalMomentumTracker extends FlavourBuff{
		public static void process() { hero.byTalent(process, PURSUIT); }
		private static final TalentCallback process = (talent, points) -> {
			if( Random.Float() < ( (talent == LETHAL_MOMENTUM ? 2 : 1) + points )
					/ (talent == PURSUIT ? 3f : 4f) ) {
				Buff.prolong(hero, LethalMomentumTracker.class, 0f);
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
	public static class EmpoweredStrikeTracker extends FlavourBuff{
		//blast wave on-hit doesn't resolve instantly, so we delay detaching for it
		public boolean delayedDetach = false;
	};
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
	public static class PatientStrikeTracker extends Buff {
		public int pos;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		@Override
		public boolean act() {
			if (pos != target.pos) {
				detach();
			} else {
				spend(TICK);
			}
			return true;
		}
		private static final String POS = "pos";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
		}
	};
	public static class AggressiveBarrierCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LiquidAgilEVATracker extends FlavourBuff{};
	public static class LiquidAgilACCTracker extends FlavourBuff{
		public int uses;

		public static float getDuration() {
			return Dungeon.hero.hasTalent(LIQUID_AGILITY) ? 10 : 5;
		}


		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / getDuration())); }

		private static final String USES = "uses";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(USES, uses);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			uses = bundle.getInt(USES);
		}
	};
	public static class LethalHasteCooldown extends Cooldown {
		public static void applyLethalHaste(Hero hero, boolean viaAbility) {
			int points = hero.shiftedPoints(LETHAL_HASTE);
			if (points == 0) return;
			boolean hasTalent = points > 1;
			if (hasTalent) points--;
			int duration = 2 + 2 * points;
			if (hasTalent) Buff.prolong(hero, Adrenaline.class, duration); // :D
			if (!hero.heroClass.is(HeroClass.DUELIST)) {
				if (hero.buff(LethalHasteCooldown.class) != null) return;
				Cooldown.affectHero(LethalHasteCooldown.class);
			} else if (!viaAbility) return;
			Buff.affect(hero, GreaterHaste.class).set(duration);
		}
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		@Override public float duration() { return 50; }
	};
	public static class SwiftEquipCooldown extends FlavourBuff{
		public boolean secondUse;
		public boolean hasSecondUse(){
			return secondUse;
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
	public static class DeadlyFollowupTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
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
	}
	public static class PreciseAssaultTracker extends FlavourBuff{
		{ type = buffType.POSITIVE; }

		private int left = hero.heroClass == HeroClass.DUELIST ? 2 : 1;

		public static boolean tryUse(Char target) {
			PreciseAssaultTracker tracker = target.buff(PreciseAssaultTracker.class);
			if (tracker == null) return false;
			if (--tracker.left == 0) tracker.detach();
			return true;
		}

		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(1f, 1f, 0.0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }

		private static final String LEFT    = "LEFT";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEFT, left);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			left = bundle.getInt(LEFT);
		}
	};
	public static class VariedChargeTracker extends Buff{
		public Class weapon;

		private static final String WEAPON    = "weapon";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WEAPON, weapon);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			weapon = bundle.getClass(WEAPON);
		}
	}
	public static class CombinedLethalityAbilityTracker extends FlavourBuff{
		public MeleeWeapon weapon;
	};
	public static class CombinedEnergyAbilityTracker extends FlavourBuff{
		public boolean monkAbilused = false;
		public boolean wepAbilUsed = false;

		private static final String MONK_ABIL_USED  = "monk_abil_used";
		private static final String WEP_ABIL_USED   = "wep_abil_used";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MONK_ABIL_USED, monkAbilused);
			bundle.put(WEP_ABIL_USED, wepAbilUsed);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
			wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
		}
	}
	public static class CounterAbilityTacker extends FlavourBuff{}
	public static class SatiatedSpellsTracker extends Buff{
		@Override
		public int icon() {
			return BuffIndicator.SPELL_FOOD;
		}
	}
	//used for metamorphed searing light
	public static class SearingLightCooldown extends FlavourBuff{
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		public void tintIcon(Image icon) { icon.hardlight(0f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	}

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

	// returns the corresponding class for a talent
	public HeroClass getHeroClass() {
		for (HeroClass cls : HeroClass.values()) {
			if (isClassTalent(cls)) return cls;
		}
		return null;
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

	private static boolean upgrading = false;

	public static void onTalentUpgraded( Hero hero, Talent talent ){
		int points = hero.pointsInTalent(talent);
		upgrading = true;
		switch(talent) {
			case IRON_WILL:
			case NOBLE_CAUSE:
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
		}

		switch(talent) {
			case MENDING_SHADOWS: case NOBLE_CAUSE:
				if ( hero.invisible > 0 ){
					Buff.affect(hero, Talent.ProtectiveShadowsTracker.class);
				}
				break;

			case LIGHT_CLOAK:
				if (!hero.heroClass.is(HeroClass.ROGUE)) break;
			case RK_FREERUNNER:
				if (hero.pointsInTalent(LIGHT_CLOAK, RK_FREERUNNER) == 1) {
					for (Item item : hero.belongings.backpack) {
						if (item instanceof CloakOfShadows) {
							if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
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

			case HEIGHTENED_SENSES: case FARSIGHT: case RK_SNIPER: case KINGS_VISION: case DIVINE_SENSE:
				Dungeon.observe();
				break;

			case TWIN_UPGRADES: case DESPERATE_POWER: case STRONGMAN: case MONASTIC_MIGHT: case DURABLE_PROJECTILES:
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

		if (talent == LIGHT_READING && hero.heroClass == HeroClass.CLERIC){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof HolyTome){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((HolyTome) item).activate(Dungeon.hero);
					}
				}
			}
		}

		//if we happen to have spirit form applied with a ring of might
		if (talent == SPIRIT_FORM){
			Dungeon.hero.updateHT(false);
		}
		upgrading = false;
	}

	public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}};
	public static class NatureBerriesAvailable extends CounterBuff{{revivePersists = true;}}; //for pre-1.3.0 saves
	public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		//3/5 HP healed, when hero is below 30% health
		if (hero.HP / (float) hero.HT <= 0.3f) {
			int healing = Math.max(
					3 * hero.shiftedPoints(HEARTY_MEAL),
					(int) Math.ceil(2.5f * hero.pointsInTalent(ROYAL_PRIVILEGE))
			);
			if (healing > 0) {
				hero.HP = Math.min(hero.HP + healing, hero.HT);
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);
			}
		}

		switch (hero.shiftedPoints(IRON_STOMACH, ROYAL_MEAL)) {
			case 3:
				Buff.prolong(hero, Invulnerability.class, hero.cooldown() + 1);
				break;
			case 1:
			case 2:
				if (hero.cooldown() > 0) {
					Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
				}
				break;
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
			if(hero.hasTalent(ENERGIZING_MEAL_I)) Buff.append( hero, Recharging.class, duration);
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
			if (talent == MYSTICAL_MEAL) points++;
			if (points == 0) return;
			//3/5/8 turns of recharging
			int duration = (int)Math.ceil(2.5 * points);
			ArtifactRecharge buff;
			if (talent == MYSTICAL_MEAL) buff = Buff.append(hero, ArtifactRecharge.class);
			else {
				buff = null;
				for (ArtifactRecharge existing : hero.buffs(ArtifactRecharge.class)) {
					if (buff == null || (
							// attempt to select one that matches the horn of plenty setting if possible, otherwise the one that's best boosted.
							buff.ignoreHornOfPlenty == existing.ignoreHornOfPlenty ?
									existing.left() < buff.left()
									: buff.ignoreHornOfPlenty != foodSource instanceof HornOfPlenty
					)) buff = existing;
				}
				if (buff == null) buff = Buff.affect(hero, ArtifactRecharge.class);
			}
			if (buff.left() < duration) {
				buff.set(duration);
				buff.ignoreHornOfPlenty = buff.ignoreHornOfPlenty || foodSource instanceof HornOfPlenty;
			}
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
		}, true, MYSTICAL_MEAL, ROYAL_MEAL); // royal meal acts second to make it largely redundant with mystical meal

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
			Buff.affect( hero, PhysicalEmpower.class).set(3, 1 + 2*hero.pointsInTalent(STRENGTHENING_MEAL));
		}
		if (hero.hasTalent(FOCUSED_MEAL)){
			if (hero.heroClass == HeroClass.DUELIST){
                //1/1.5 charge for the duelist
                Buff.affect( hero, MeleeWeapon.Charger.class ).gainCharge(0.5f*(hero.pointsInTalent(FOCUSED_MEAL)+1));
				ScrollOfRecharging.charge( hero );
			} else {
				// lvl/3 / lvl/2 bonus dmg on next hit for other classes
				Buff.affect( hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(FOCUSED_MEAL))), 1);
			}
		}
		if (hero.canHaveTalent(SATIATED_SPELLS)){
			if (hero.heroClass.is(HeroClass.CLERIC)) {
				Buff.affect(hero, SatiatedSpellsTracker.class);
			} else {
				//2/4/6 shielding, delayed up to 10 turns
				int amount = 2 * hero.shiftedPoints(SATIATED_SPELLS);
				Barrier b = Buff.affect(hero, Barrier.class);
				if (b.shielding() <= amount){
					b.setShield(amount);
					b.delay(Math.max(10-b.cooldown(), 0));
				}
			}
		}
		enlighteningMeal: {
			int points = hero.shiftedPoints(ENLIGHTENING_MEAL);
			if (points == 0) break enlighteningMeal;

			HolyTome tome = hero.belongings.getItem(HolyTome.class);
			if (tome != null) {
				// 1 / 1.5 / 2
				tome.directCharge( 0.5f * (1+points) );
			}
			// cleric gets the recharging too.

			//2/3/5 turns of recharging
			int duration = Math.max(points - 1, 1) + points;
			Buff.append( hero, ArtifactRecharge.class )
					.set(duration)
					.ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
			Buff.append( hero, Recharging.class, duration );
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE);
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

	public static void onPotionUsed( Hero hero, int cell, float factor ){
		if (hero.hasTalent(LIQUID_WILLPOWER,RESTORATION)){
			if (hero.heroClass.is(HeroClass.WARRIOR)) {
				BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
				if (shield != null) {
					// 50/75% of total shield
					int shieldToGive = Math.round(factor * shield.maxShield() * hero.byTalent(/*stacks*/true, /*shifted*/true, LIQUID_WILLPOWER, 0.5f, RESTORATION, 0.25f));
					shield.supercharge(shieldToGive);
				}
			} else {
				// 5/7.5% of max HP
				int shieldToGive = Math.round( factor * hero.HT * hero.byTalent(/*stacks*/true, /*shifted*/true, LIQUID_WILLPOWER, 0.04f, RESTORATION, 0.025f));
				Buff.affect(hero, Barrier.class).setShield(shieldToGive);
			}
		}
		if (hero.hasTalent(LIQUID_NATURE, RESTORATION)){
			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS9){
				grassCells.add(cell+i);
			}
			Random.shuffle(grassCells);
			for (int grassCell : grassCells){
				Char ch = Actor.findChar(grassCell);
				if (ch != null && ch.alignment == Char.Alignment.ENEMY){
					//1/2 turns of roots
					Buff.affect(ch, Roots.class, factor * hero.pointsInTalent(false, LIQUID_NATURE,RESTORATION));
				}
				if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
						Dungeon.level.map[grassCell] == Terrain.EMBERS ||
						Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO){
					Level.set(grassCell, Terrain.GRASS);
					GameScene.updateMap(grassCell);
				}
				HighGrass.playVFX(grassCell);
			}
			// 4/6 cells total
			int totalGrassCells = (int) (factor * hero.byTalent(false,true,LIQUID_NATURE, 3/* 6/9 */, RESTORATION, 2));
			while (grassCells.size() > totalGrassCells){
				grassCells.remove(0);
			}
			for (int grassCell : grassCells){
				int t = Dungeon.level.map[grassCell];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(grassCell) == null){
					Level.set(grassCell, Terrain.HIGH_GRASS);
					GameScene.updateMap(grassCell);
				}
			}
			Dungeon.observe();
		}
		if (hero.hasTalent(LIQUID_AGILITY)){
			float effectiveFactor = factor * 2;
			Buff.prolong(hero, LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0, effectiveFactor-1));
			if (factor >= 0.5f){
				// 1 / 2
				// 2 / 4
				Buff.prolong(hero, LiquidAgilACCTracker.class, LiquidAgilACCTracker.getDuration())
						.uses = Math.round(effectiveFactor);
			}
		}
	}

	public static void onScrollUsed( Hero hero, int pos, float factor, Class<?extends Item> cls ){
		if (hero.hasTalent(INSCRIBED_POWER, RESTORATION)){
			// 2/3 empowered wand zaps, 3 for inscribed power
			// inscribed power overrides restoration
			int left = hero.hasTalent(RESTORATION) ? hero.shiftedPoints(RESTORATION) : 3;
			Buff.affect(hero, ScrollEmpower.class).reset((int) factor * left);
		}
		if (hero.hasTalent(INSCRIBED_STEALTH, RESTORATION)){
			// 3/5 turns of stealth (restoration), . inscribed stealth overrides restoration
			int points;
			if (hero.hasTalent(INSCRIBED_STEALTH)) {
				points = hero.pointsInTalent(INSCRIBED_STEALTH);
				factor *= 2;
			} else {
				points = hero.pointsInTalent(RESTORATION);
			}
			Buff.affect(hero, Invisibility.class, factor * (1 + 2*points));
			Sample.INSTANCE.play( Assets.Sounds.MELD );
		}
        if (RecallInscription.INSTANCE.isVisible(hero) && Scroll.class.isAssignableFrom(cls) && cls != ScrollOfUpgrade.class){
            if (hero.heroClass == HeroClass.CLERIC){
                RecallInscription.UsedItemTracker.track(hero, cls);
            } else {
                // 20/30% (double shattered)
                if (Random.Int(20/2) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
                    Reflection.newInstance(cls).collect();
                    GLog.p("refunded!");
                }
            }
        }
	}

    public static void onRunestoneUsed( Hero hero, int pos, Class<?extends Item> cls ){
        if (RecallInscription.INSTANCE.isVisible(hero) && Runestone.class.isAssignableFrom(cls)){
            if (hero.heroClass == HeroClass.CLERIC){
                RecallInscription.UsedItemTracker.track(hero, cls);
            } else {

                //don't trigger on 1st intuition use
                if (cls.equals(StoneOfIntuition.class) && hero.buff(StoneOfIntuition.IntuitionUseTracker.class) != null){
                    return;
                }
                // 20/30% (double shattered)
                if (Random.Int(20/2) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
                    Reflection.newInstance(cls).collect();
                    GLog.p("refunded!");
                }
            }
        }
    }

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS,RK_ASSASSIN)){
			float duration = 3f*hero.pointsInTalent(ENHANCED_RINGS,RK_ASSASSIN);
			if(hero.hasTalent(ENHANCED_RINGS)) Buff.affect(hero, EnhancedRings.class, duration);
			else Buff.prolong(hero, EnhancedRings.class, duration);
		}

		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.DIVINE_SENSE)){
			Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown()+1);
		}

		// 10/20/30%
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.CLEANSE)
				&& Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.CLEANSE)){
			boolean removed = false;
			for (Buff b : Dungeon.hero.buffs()) {
				if (b.type == Buff.buffType.NEGATIVE) {
					b.detach();
					removed = true;
				}
			}
			if (removed && Dungeon.hero.sprite != null) {
				new Flare( 6, 32 ).color(0xFF4CD2, true).show( Dungeon.hero.sprite, 2f );
			}
		}
	}

	public static void onItemEquipped( Hero hero, Item item ) {
		if (item.isIdentified()) return; // all talent interactions here regard identification
		boolean identify = false;
		if (hero.pointsInTalent(ROYAL_INTUITION) == 2) {
			identify = true;
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
				identify = hero.shiftedPoints(shiftedTalent, otherTalent) >= 2;
			} else if (item instanceof Ring) {
				// Thief's Intuition
				switch(hero.shiftedPoints(THIEFS_INTUITION, ROYAL_INTUITION)) {
					case 2: case 3: identify = true; break;
					case 1: ((Ring) item).setKnown(); break;
				}
			}
		}

		if (identify) {
            if(!ShardOfOblivion.passiveIDDisabled()) {
                item.identify();
            }
            if (hero.sprite.emitter() != null) hero.sprite.emitter().burst(
                    Speck.factory(Speck.QUESTION),
                    1
            );
        }
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (item.isIdentified() || item.collected) return;
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
				item.cursedKnown = true;
				// suppress message if reran during talent upgrading, because it's bound to be confusing
				if (!upgrading) {
					// delegate to proper message instead of creating my own
					GLog.w(Messages.get(item, item.visiblyCursed() ? item instanceof Wand ? "curse_discover" : item instanceof Ring ? "cursed_known" : "cursed" : "not_cursed"));
				}
			}
			if (hero.sprite.emitter() != null) hero.sprite.emitter().burst(
					Speck.factory(Speck.QUESTION),1);
		}
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg){

		if (hero.hasTalent(Talent.PROVOKED_ANGER, Talent.KINGS_WISDOM)) {
			ProvokedAngerTracker provokedAnger = hero.buff(ProvokedAngerTracker.class);
			if (provokedAnger != null) {
				dmg += 1 + Math.max(1, hero.pointsInTalent(false, Talent.PROVOKED_ANGER, KINGS_WISDOM));
				if (--provokedAnger.left <= 0) provokedAnger.detach();
			}

		}

        int points = hero.pointsInTalent(Talent.LINGERING_MAGIC, KINGS_WISDOM);
		if (points > 0
				&& hero.buff(LingeringMagicTracker.class) != null){
			dmg += Random.IntRange(points , 2);
			hero.buff(LingeringMagicTracker.class).detach();
		}

		if (hero.hasTalent(Talent.SUCKER_PUNCH,KINGS_WISDOM)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(SuckerPunchTracker.class) == null){
			int bonus = 0;
			if(hero.hasTalent(SUCKER_PUNCH)) bonus += 1+hero.pointsInTalent(SUCKER_PUNCH);  // 2/3
			if(hero.hasTalent(KINGS_WISDOM)) bonus += Random.round(0.5f*(2+hero.pointsInTalent(KINGS_WISDOM))); // 1-2/2
			dmg += bonus;
			Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE,KINGS_WISDOM) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				Buff.prolong(hero, FollowupStrikeTracker.class, 5f).object = enemy.id();
			} else if (hero.buff(FollowupStrikeTracker.class) != null
					&& hero.buff(FollowupStrikeTracker.class).object == enemy.id()){
				int bonus = 0;
				if(hero.hasTalent(KINGS_WISDOM)) bonus += hero.shiftedPoints(KINGS_WISDOM); // 2/3
				if(hero.hasTalent(FOLLOWUP_STRIKE)) bonus += Random.round(hero.shiftedPoints(FOLLOWUP_STRIKE) * 1.5f); // 3/4-5
				dmg += bonus;
				hero.buff(FollowupStrikeTracker.class).detach();
			}
		}

		Talent.SpiritBladesTracker tracker = hero.buff(Talent.SpiritBladesTracker.class, false);
		if ( tracker != null && Random.Float() < 3*hero.pointsInTalent(Talent.SPIRIT_BLADES, Talent.SEA_OF_BLADES) * SpiritBladesTracker.getProcModifier() ){
			SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
			if (bow != null) dmg = bow.proc( hero, enemy, dmg );
			tracker.detach();
		}

		if (hero.hasTalent(PATIENT_STRIKE)){
			if (hero.buff(PatientStrikeTracker.class) != null
					&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)){
				hero.buff(PatientStrikeTracker.class).detach();
				dmg += hero.pointsInTalent(Talent.PATIENT_STRIKE) == 1 ? 3 : Random.IntRange(4,5);
			}
		}

		if (hero.hasTalent(DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
					Buff.prolong(hero, DeadlyFollowupTracker.class, 5f).object = enemy.id();
				}
			} else if (hero.buff(DeadlyFollowupTracker.class) != null
					&& hero.buff(DeadlyFollowupTracker.class).object == enemy.id()){
				dmg = Math.round(dmg * (1.0f + /*.1f*/.16f*hero.pointsInTalent(DEADLY_FOLLOWUP)));
			}
		}

		return dmg;
	}

	public static class ProvokedAngerTracker extends FlavourBuff {
		{ type = Buff.buffType.POSITIVE; }
		public int left = 1;
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 1.43f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private final String LEFT = "left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEFT, left);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			left = Math.max(1, bundle.getInt(LEFT));
		}
	}
	public static class LingeringMagicTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.75f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
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
	};

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
				Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, PROVOKED_ANGER, IRON_WILL);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL_I, SCHOLARS_INTUITION, LINGERING_MAGIC, BACKUP_BARRIER);
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
			case CLERIC:
				Collections.addAll(tierTalents, SATIATED_SPELLS, HOLY_INTUITION, SEARING_LIGHT, SHIELD_OF_LIGHT);
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
				Collections.addAll(tierTalents, IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL_II, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, MYSTICAL_MEAL, INSCRIBED_STEALTH, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, INVIGORATING_MEAL, LIQUID_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, ENLIGHTENING_MEAL, RECALL_INSCRIPTION, SUNRAY, DIVINE_SENSE, BLESS);
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
				Collections.addAll(tierTalents, DESPERATE_POWER, ALLY_WARP);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, POINT_BLANK, SEER_SHOT);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, PRECISE_ASSAULT, DEADLY_FOLLOWUP);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, CLEANSE, LIGHT_READING);
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
				Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST, BERSERKING_STAMINA, ONE_MAN_ARMY);
				break;
			case GLADIATOR:
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO, SKILL, EVERYTHING_IS_A_WEAPON);
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
				Collections.addAll(tierTalents, VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY, ELITE_DEXTERITY);
				break;
			case MONK:
				Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY, MONASTIC_MIGHT);
				break;
			case PRIEST:
				Collections.addAll(tierTalents, HOLY_LANCE, HALLOWED_GROUND, MNEMONIC_PRAYER, DIVINE_ADVENT, ENDURING_LIGHT);
				break;
			case PALADIN:
				Collections.addAll(tierTalents, LAY_ON_HANDS, AURA_OF_PROTECTION, WALL_OF_LIGHT, LIMIT_BREAK, TRIAGE);
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

	private static final HashSet<String> removedTalents = new HashSet<>();
	static{
		//v2.4.0
		removedTalents.add("TEST_SUBJECT");
		removedTalents.add("TESTED_HYPOTHESIS");
	}
	private static final HashMap<String, String> renamedTalents = new HashMap();
	static {
		//v2.4.0
		renamedTalents.put("SECONDARY_CHARGE",          "VARIED_CHARGE");
	};

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (bundle.contains("replacements")){
			Bundle replacements = bundle.getBundle("replacements");
			for (String key : replacements.getKeys()){
				String value = replacements.getString(key);
				if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
				if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
				if (!removedTalents.contains(key) && !removedTalents.contains(value)) {
                    try {
                        hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
                    } catch (Exception e) {
                        ShatteredPixelDungeon.reportException(e);
                    }
                }
			}
		}

		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;

			if (tierBundle != null){
				// handle my swapping of the two talents
				for (String tName : tierBundle.getKeys()){
					int points = tierBundle.getInt(tName);
					if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
					if (!removedTalents.contains(tName)) {
						try {
							Talent talent = Talent.valueOf(tName);
							if (talent == BERSERKING_STAMINA && !tierBundle.contains(DEATHLESS_FURY.name())) {
								talent = DEATHLESS_FURY;
							}
							if (tier.containsKey(talent)) {
								tier.put(talent, Math.min(points, talent.maxPoints()));
							}
						} catch (Exception e) {
							ShatteredPixelDungeon.reportException(e);
						}
					}
				}
			}
		}
	}

}
