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

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Adrenaline;
import com.zrp200.rkpd2.actors.buffs.ArtifactRecharge;
import com.zrp200.rkpd2.actors.buffs.Berserk;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.CounterBuff;
import com.zrp200.rkpd2.actors.buffs.EnhancedRings;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.actors.buffs.Haste;
import com.zrp200.rkpd2.actors.buffs.Recharging;
import com.zrp200.rkpd2.actors.buffs.Roots;
import com.zrp200.rkpd2.actors.buffs.WandEmpower;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.effects.SpellSprite;
import com.zrp200.rkpd2.effects.particles.LeafParticle;
import com.zrp200.rkpd2.items.BrokenSeal;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.Armor;
import com.zrp200.rkpd2.items.artifacts.Artifact;
import com.zrp200.rkpd2.items.artifacts.CloakOfShadows;
import com.zrp200.rkpd2.items.artifacts.HornOfPlenty;
import com.zrp200.rkpd2.items.potions.Potion;
import com.zrp200.rkpd2.items.rings.Ring;
import com.zrp200.rkpd2.items.scrolls.Scroll;
import com.zrp200.rkpd2.items.scrolls.ScrollOfRecharging;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.melee.MagesStaff;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.items.weapon.missiles.MissileWeapon;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), ARMSMASTERS_INTUITION(1), TEST_SUBJECT(2), IRON_WILL(3),
	//Warrior T2
	IRON_STOMACH(4),
	RESTORED_WILLPOWER(5),
	RUNIC_TRANSFERENCE(6), // TODO buff
	LETHAL_MOMENTUM(7),
	IMPROVISED_PROJECTILES(8), // TODO buff
	//Warrior T3
	HOLD_FAST(9, 3), STRONGMAN(10, 3),
	//Berserker T3
	ENDLESS_RAGE(11, 3), BERSERKING_STAMINA(12, 3), ENRAGED_CATALYST(13, 3), ONE_MAN_ARMY(7,3),
	//Gladiator T3
	CLEAVE(14, 3), LETHAL_DEFENSE(15, 3), ENHANCED_COMBO(16, 3), SKILL(98,3),

	//Mage T1
	ENERGIZING_MEAL_I(36), SCHOLARS_INTUITION(33), TESTED_HYPOTHESIS(34), BACKUP_BARRIER(35),
	//Mage T2
	ENERGIZING_MEAL_II(36), ENERGIZING_UPGRADE(37), WAND_PRESERVATION(38), ARCANE_VISION(39), SHIELD_BATTERY(40),
	//Mage T3
	EMPOWERING_SCROLLS(41, 3), ALLY_WARP(42, 3),
	//Battlemage T3
	EMPOWERED_STRIKE(43, 3), MYSTICAL_CHARGE(44, 3), EXCESS_CHARGE(45, 3), SORCERY(38,3),
	//Warlock T3
	SOUL_EATER(46, 3), SOUL_SIPHON(47, 3), NECROMANCERS_MINIONS(48, 3), WARLOCKS_TOUCH (113, 3),

	//Rogue T1
	CACHED_RATIONS(64), THIEFS_INTUITION(65), SUCKER_PUNCH(66), MENDING_SHADOWS(67),
	//Rogue T2
	MYSTICAL_MEAL(68), MYSTICAL_UPGRADE(69), WIDE_SEARCH(70), SILENT_STEPS(71), ROGUES_FORESIGHT(72),
	//Rogue T3
	ENHANCED_RINGS(73, 3), LIGHT_CLOAK(74, 3),
	//Assassin T3
	ENHANCED_LETHALITY(75, 3), ASSASSINS_REACH(76, 3), BOUNTY_HUNTER(77, 3), LETHAL_MOMENTUM_2(7,3),
	//Freerunner T3
	EVASIVE_ARMOR(78, 3), PROJECTILE_MOMENTUM(79, 3), SPEEDY_STEALTH(80, 3),

	//Huntress T1
	NATURES_BOUNTY(96), SURVIVALISTS_INTUITION(97), FOLLOWUP_STRIKE(98), NATURES_AID(99),
	//Huntress T2
	INVIGORATING_MEAL(100), RESTORED_NATURE(101), REJUVENATING_STEPS(102), HEIGHTENED_SENSES(103), DURABLE_PROJECTILES(104),
	//Huntress T3
	POINT_BLANK(105, 3), SEER_SHOT(106, 3),
	//Sniper T3
	FARSIGHT(107, 3), SHARED_ENCHANTMENT(108, 3), SHARED_UPGRADES(109, 3), RANGER(98,1),
	//Warden T3
	DURABLE_TIPS(110, 3), BARKSKIN(111, 3), SHIELDING_DEW(112, 3), NATURES_BETTER_AID(102,3),
	// TODO add unique icons
	ROYAL_PRIVILEGE(32), // food related talents, uses empowering icon
	ROYAL_INTUITION(97), // intuition-related talents, uses survivalist's icon
	KINGS_WISDOM(34), // on-id + combat talents, uses tested hypothesis
	NOBLE_CAUSE(3), // other ones. uses iron will
	ROYAL_MEAL(68), //// all on-eat talents for tier 2. uses arcane meal
	RESTORATION(5), // all upgrade/potion of healing talents, uses restored willpower icon
	POWER_WITHIN(72), // runic (3), wand preservation (3), rogue's foresight (5), rejuvenating steps (3), uses foresight.
	KINGS_VISION(103), // improvised projectiles (4), arcane vision(4), wide search(3), heightened senses(4), uses heightened senses
	PURSUIT(98), // durable projectiles (5),silent steps(4),lethal momentum (3),shield battery(5), uses FUS
	// Rat King T3
	RK_BERSERKER(11,3), RK_GLADIATOR(16,3), RK_BATTLEMAGE(43,3), RK_WARLOCK(47,3), RK_ASSASSIN(75,3), RK_FREERUNNER(78,3), RK_SNIPER(109,3), RK_WARDEN(102,3);
	// TODO is splitting up t2s arbitrarily really a good idea?
	public static class ImprovisedProjectileCooldown extends FlavourBuff{};
	public static class LethalMomentumTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{};
	public static class EmpoweredStrikeTracker extends FlavourBuff{};
	public static class BountyHunterTracker extends FlavourBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{};
	public static class SeerShotCooldown extends FlavourBuff{};


	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21+4, 31};

	Talent(int icon ){
		this(icon, 2);
	}

	Talent( int icon, int maxPoints ){
		this.icon = icon;
		this.maxPoints = maxPoints;
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

	public String desc(){
		return Messages.get(this, name() + ".desc");
	}

	public static void onTalentUpgraded( Hero hero, Talent talent){
		int points = hero.pointsInTalent(talent);
		switch(talent) {
			case SCHOLARS_INTUITION:
				for(Item item : hero.belongings) {
					if(item instanceof Scroll || item instanceof Potion) {
						for(int i=0; i < item.quantity() && !item.isIdentified(); i++) {
							if(Random.Int(3) == 0) item.identify(); // it increases by 33%, so we just reroll it with a 33% chance.
						}
					}
				}
				break;
			case ARMSMASTERS_INTUITION: case THIEFS_INTUITION: case ROYAL_INTUITION:
				for(Item item : hero.belongings)
				{
					// rerun these.
					onItemCollected(hero, item);
					if(item.isEquipped(hero)) onItemEquipped(hero,item);
				}
				break;
			case ROYAL_PRIVILEGE: case NATURES_BOUNTY:
				int count = talent == NATURES_BOUNTY ? 3 : 2;
				if(points == 1) count *= 2; // for the initial upgrade.
				Buff.count(hero, NatureBerriesAvailable.class, count*points);
				break;
			case BERSERKING_STAMINA:
				Berserk berserk = hero.buff(Berserk.class);
				if(berserk != null) berserk.recover(talent == RK_BERSERKER ? 1/3f : .5f);
				break;
		}
		if (talent == FARSIGHT || talent == RK_SNIPER){
			Dungeon.observe();
		}

	}

	public static class CachedRationsDropped extends CounterBuff{};
	public static class NatureBerriesAvailable extends CounterBuff{};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		if (hero.hasTalent(HEARTY_MEAL,ROYAL_PRIVILEGE)){
			// somehow I managed to make it even more confusing than before.
			int points = hero.pointsInTalent(HEARTY_MEAL, ROYAL_PRIVILEGE);
			int factor = hero.hasTalent(HEARTY_MEAL) ? 3 : 4;
			double missingHP = 1-(double)hero.HP/hero.HT;
			int strength = (int)(missingHP*factor);
			if(!hero.hasTalent(HEARTY_MEAL)) strength--; // missing 1/4 hp is not rewarded with healing normally.
			if(strength-- > 0) { // adjusting for the addition of one point.
				strength += points;
				// hearty meal heals for (2.5/4)/(4/6). priv heals for (2/3)/(3/5)
				hero.HP += hero.hasTalent(HEARTY_MEAL) && strength == 1
						? Random.round(2.5f) // simulate 2.5
						: (int) Math.ceil(( hero.hasTalent(HEARTY_MEAL) ? 2.5 : 2 )*Math.pow(1.5,strength-1));
				hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), strength);
			}
		}
		if (hero.hasTalent(IRON_STOMACH,ROYAL_MEAL)){
			if (hero.cooldown() > 0) {
				Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
			}
		}
		if (hero.hasTalent(ROYAL_PRIVILEGE)){ // SHPD empowering meal talent
			//2/3 bonus wand damage for next 3 zaps
			int bonus = 1+hero.pointsInTalent(ROYAL_PRIVILEGE);
			Buff.affect( hero, WandEmpower.class).set(bonus, 3);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL_I,ROYAL_MEAL)){
			//5/8 turns of recharging.
			int points = hero.pointsInTalent(ENERGIZING_MEAL_I,ROYAL_MEAL);
			int duration = 2 + 3*points;
			if(hero.hasTalent(ENERGIZING_MEAL_I)) Buff.affect( hero, Recharging.class, duration);
			else								  Buff.prolong(hero, Recharging.class, duration);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL_II)) {
			// 1/1.5 charges instantly replenished, does not overcharge at this time even though I implemented functionality for it.
			hero.belongings.charge(0.5f*(1+hero.pointsInTalent(ENERGIZING_MEAL_II)),hero.pointsInTalent(ENERGIZING_MEAL_II) == 2);
			if(!hero.hasTalent(ENERGIZING_MEAL_I)) ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(MYSTICAL_MEAL,ROYAL_MEAL)){
			//3/5 turns of recharging
			int duration = 1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL)+hero.pointsInTalent(ROYAL_MEAL));
			ArtifactRecharge artifactRecharge = Buff.affect( hero, ArtifactRecharge.class);
			if(hero.hasTalent(MYSTICAL_MEAL)) artifactRecharge.prolong((float)Math.ceil(duration*1.5)); // 5-8 turns of recharge!!!
			else artifactRecharge.set(duration).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty && !hero.hasTalent(MYSTICAL_MEAL);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(INVIGORATING_MEAL)) {
			// 4.5/6 tiles -> 3/5 turns
			Buff.affect(hero, Adrenaline.class, 2+2*hero.pointsInTalent(INVIGORATING_MEAL));
		}
		if (hero.hasTalent(ROYAL_MEAL)) {
			//effectively 1/2 turns of haste
			Buff.prolong( hero, Haste.class, 0.67f+hero.pointsInTalent(ROYAL_MEAL));
			hero.sprite.emitter().burst(Speck.factory(Speck.JET), 4*hero.pointsInTalent(ROYAL_MEAL));
		}
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		// 1.75x/2.5x speed with huntress talent
		float factor = 1f + hero.pointsInTalent(ROYAL_INTUITION,SURVIVALISTS_INTUITION) *0.75f;
		if(hero.pointsInTalent(SURVIVALISTS_INTUITION) > 0) factor *= 1.5;

		// 2x/instant for Warrior (see onItemEquipped)
		if (item instanceof MeleeWeapon || item instanceof Armor){
			int points = hero.pointsInTalent(ROYAL_INTUITION,ARMSMASTERS_INTUITION);
			// basically an innate +1 for armsmaster
			if(hero.hasTalent(ARMSMASTERS_INTUITION)) points++;
			factor *= 1f + points;
		}
		// 3x/instant for mage (see Wand.wandUsed())
		if (item instanceof Wand){
			factor *= 1f + 2*(hero.pointsInTalent(SCHOLARS_INTUITION) + hero.pointsInTalent(ROYAL_INTUITION));
		}
		// 2x/instant for rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.pointsInTalent(THIEFS_INTUITION) + hero.pointsInTalent(ROYAL_INTUITION);
		}
		return factor;
	}

	public static void onHealingPotionUsed( Hero hero ){
		if (hero.hasTalent(RESTORED_WILLPOWER,RESTORATION)){
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			if (shield != null){
				double multiplier = 0.33f*(1+hero.pointsInTalent(RESTORED_WILLPOWER,RESTORATION));
				if(hero.hasTalent(RESTORED_WILLPOWER)) multiplier *= 1.5;
				shield.supercharge((int)Math.round(shield.maxShield()*multiplier));
			}
		}
		if (hero.hasTalent(RESTORED_NATURE,RESTORATION)){
			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				grassCells.add(hero.pos+i);
			}
			Random.shuffle(grassCells);
			for (int cell : grassCells){
				Char ch = Actor.findChar(cell);
				if (ch != null){
					int duration = 1+hero.pointsInTalent(RESTORED_NATURE,RESTORATION);
					if(hero.heroClass == HeroClass.HUNTRESS) duration *= 1.5;
					Buff.affect(ch, Roots.class, duration);
				}
				if (Dungeon.level.map[cell] == Terrain.EMPTY ||
						Dungeon.level.map[cell] == Terrain.EMBERS ||
						Dungeon.level.map[cell] == Terrain.EMPTY_DECO){
					Level.set(cell, Terrain.GRASS);
					GameScene.updateMap(cell);
				}
				CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			}
			if (hero.pointsInTalent(RESTORED_NATURE,RESTORATION) == 1){
				grassCells.remove(0);
				grassCells.remove(0);
				grassCells.remove(0);
			}
			for (int cell : grassCells){
				int t = Dungeon.level.map[cell];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(cell) == null){
					Level.set(cell, Terrain.HIGH_GRASS);
					GameScene.updateMap(cell);
				}
			}
			Dungeon.observe();
		}
	}

	public static void onUpgradeScrollUsed( Hero hero ){
		if (hero.hasTalent(ENERGIZING_UPGRADE,RESTORATION)){
			int charge = 1+hero.pointsInTalent(ENERGIZING_UPGRADE,RESTORATION);
			MagesStaff staff = hero.belongings.getItem(MagesStaff.class);
			if(hero.hasTalent(ENERGIZING_UPGRADE)) {
				hero.belongings.charge(charge, true);
				ScrollOfRecharging.charge( Dungeon.hero );
				SpellSprite.show( hero, SpellSprite.CHARGE );
			} else if (staff != null){
				staff.gainCharge( charge, true);
				ScrollOfRecharging.charge( Dungeon.hero );
				SpellSprite.show( hero, SpellSprite.CHARGE );
			}
		}
		if (hero.hasTalent(MYSTICAL_UPGRADE,RESTORATION)){
			CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
			if (cloak != null){
				cloak.overCharge(1+hero.pointsInTalent(MYSTICAL_UPGRADE,RESTORATION));
				ScrollOfRecharging.charge( Dungeon.hero );
				SpellSprite.show( hero, SpellSprite.CHARGE );
			}
		}
	}

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS,RK_ASSASSIN)){
			Buff.prolong(hero, EnhancedRings.class, 3f*hero.pointsInTalent(ENHANCED_RINGS,RK_ASSASSIN));
		}
	}
	public static void onItemEquipped( Hero hero, Item item ){
		if (hero.pointsInTalent(ARMSMASTERS_INTUITION,ROYAL_INTUITION) >= (hero.hasTalent(ARMSMASTERS_INTUITION) ? 1 : 2) && (item instanceof Weapon || item instanceof Armor)){
			item.identify();
		}
		if ((hero.heroClass == HeroClass.ROGUE || hero.hasTalent(ROYAL_INTUITION)) && item instanceof Ring){
			int points = hero.pointsInTalent(THIEFS_INTUITION,ROYAL_INTUITION);
			if(hero.heroClass == HeroClass.ROGUE ) points++; // essentially this is a 50% boost.
			if (points >= 2){
				item.identify();
			} else {
				((Ring) item).setKnown();
			}
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if(item.isIdentified()) return;
		boolean id = false;
		if (hero.heroClass == HeroClass.ROGUE || hero.hasTalent(THIEFS_INTUITION,ROYAL_INTUITION)){
			int points = hero.pointsInTalent(THIEFS_INTUITION,ROYAL_INTUITION);
			if(hero.heroClass == HeroClass.ROGUE) points++;
			if (points == 3 && (item instanceof Ring || item instanceof Artifact)) {
				item.identify();
				id = true;
			}
			else if (points == 2 && item instanceof Ring) ((Ring) item).setKnown();
		}
		if (hero.pointsInTalent(ARMSMASTERS_INTUITION) == 2 &&
				(item instanceof Weapon || item instanceof Armor)) {
			item.identify();
			id = true;
		}
		if(!item.collected && Random.Int(5) < hero.pointsInTalent(SURVIVALISTS_INTUITION)){
			item.cursedKnown = true;
			id = true;
		}
		if( (item instanceof Scroll || item instanceof Potion) && !item.isIdentified() && hero.hasTalent(SCHOLARS_INTUITION) ) {
			if(!item.collected && Random.Int(3) >= 3-hero.pointsInTalent(SCHOLARS_INTUITION)) {
				item.identify();
				id = true;
			}
		}
		if(id && hero.sprite.emitter() != null) hero.sprite.emitter().burst(Speck.factory(Speck.QUESTION),1);

	}

	//note that IDing can happen in alchemy scene, so be careful with VFX here
	public static void onItemIdentified( Hero hero, Item item ){
		if (hero.hasTalent(TEST_SUBJECT,KINGS_WISDOM)){
			//heal for 2/3 HP
			int points = hero.pointsInTalent(TEST_SUBJECT,KINGS_WISDOM);
			int heal = 1 + points;
			if(hero.hasTalent(TEST_SUBJECT)) heal += points == 1 ? Random.Int(2) : 1; // 2-3/4
			heal = Math.min(heal, hero.HT-hero.HP);
			hero.HP += heal;
			Emitter e = hero.sprite.emitter();
			if (e != null && heal > 0) e.burst(Speck.factory(Speck.HEALING), heal == 1 ? 1 : points);
		}
		if (hero.hasTalent(TESTED_HYPOTHESIS,KINGS_WISDOM)){
			//2/3 turns of wand recharging
			int duration = 1 + hero.pointsInTalent(TESTED_HYPOTHESIS,KINGS_WISDOM);
			if(hero.hasTalent(TESTED_HYPOTHESIS)) duration = (int)Math.ceil(duration*1.5f); // 3/5
			Buff.affect(hero, Recharging.class, duration);
			ScrollOfRecharging.charge(hero);
		}
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){
		if (hero.hasTalent(Talent.SUCKER_PUNCH,KINGS_WISDOM)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(SuckerPunchTracker.class) == null){
			int bonus = hero.hasTalent(SUCKER_PUNCH)
					? 1+hero.pointsInTalent(SUCKER_PUNCH)  // 2/3
					: Random.round(0.5f*(2+hero.pointsInTalent(KINGS_WISDOM))); // 1-2/2
			dmg += bonus;
			if(!hero.hasTalent(SUCKER_PUNCH)) Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE,KINGS_WISDOM)) {
			if (hero.belongings.weapon instanceof MissileWeapon) {
				Buff.affect(enemy, FollowupStrikeTracker.class);
			} else if (enemy.buff(FollowupStrikeTracker.class) != null){
				int bonus = 1 + hero.pointsInTalent(FOLLOWUP_STRIKE,KINGS_WISDOM); // 2/3
				if(hero.heroClass == HeroClass.HUNTRESS) {
					bonus = Random.round( bonus*1.5f ); // 3/4-5
				};
				dmg += bonus;
				if (!(enemy instanceof Mob) || !((Mob) enemy).surprisedBy(hero)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
				}
				enemy.buff(FollowupStrikeTracker.class).detach();
			}
		}

		return dmg;
	}

	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends Buff{};

	public static final int MAX_TALENT_TIERS = 3;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents );
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 1
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, HEARTY_MEAL, ARMSMASTERS_INTUITION, TEST_SUBJECT, IRON_WILL);
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
			case RAT_KING:
				Collections.addAll(tierTalents, ROYAL_PRIVILEGE, ROYAL_INTUITION, KINGS_WISDOM, NOBLE_CAUSE);
				break;
		}
		for (Talent talent : tierTalents){
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
			case RAT_KING:
				Collections.addAll(tierTalents, ROYAL_MEAL, RESTORATION, POWER_WITHIN, KINGS_VISION, PURSUIT);
		}
		for (Talent talent : tierTalents){
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
			case RAT_KING: break; // no unique talents... :(
		}
		for (Talent talent : tierTalents){
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
				Collections.addAll(tierTalents, ENDLESS_RAGE, BERSERKING_STAMINA, ENRAGED_CATALYST, ONE_MAN_ARMY);
				break;
			case GLADIATOR:
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO, SKILL);
				break;
			case BATTLEMAGE:
				Collections.addAll(tierTalents, SORCERY, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE);
				break;
			case WARLOCK:
				Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS, WARLOCKS_TOUCH);
				break;
			case ASSASSIN:
				Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER, LETHAL_MOMENTUM_2);
				break;
			case FREERUNNER:
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH);
				break;
			case SNIPER:
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES, RANGER);
				break;
			case WARDEN:
				Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, SHIELDING_DEW, NATURES_BETTER_AID);
				break;
			case KING: // this should be *lovely*
				Collections.addAll(tierTalents, RK_BERSERKER, RK_BATTLEMAGE, RK_ASSASSIN, RK_SNIPER, RK_GLADIATOR, RK_WARLOCK, RK_FREERUNNER, RK_WARDEN);
		}
		for (Talent talent : tierTalents){
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

		//tier4
		//TBD
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
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (hero.heroClass != null) initClassTalents(hero);
		if (hero.subClass != null)  initSubclassTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;
			//pre-0.9.1 saves
			if (tierBundle == null && i == 0 && bundle.contains("talents")){
				tierBundle = bundle.getBundle("talents");
			}

			if (tierBundle != null){
				for (Talent talent : tier.keySet()){
					if (tierBundle.contains(talent.name())){
						tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
					}
				}
			}
		}
	}

}
