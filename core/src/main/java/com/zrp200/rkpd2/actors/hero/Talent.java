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
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.CounterBuff;
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
import com.zrp200.rkpd2.items.potions.exotic.ExoticPotion;
import com.zrp200.rkpd2.items.rings.Ring;
import com.zrp200.rkpd2.items.scrolls.Scroll;
import com.zrp200.rkpd2.items.scrolls.ScrollOfRecharging;
import com.zrp200.rkpd2.items.scrolls.exotic.ExoticScroll;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;

public enum Talent {

	HEARTY_MEAL(0),
	ARMSMASTERS_INTUITION(1),
	TEST_SUBJECT(2),
	IRON_WILL(3),
	IRON_STOMACH(4),
	RESTORED_WILLPOWER(5),
	RUNIC_TRANSFERENCE(6),
	LETHAL_MOMENTUM(7),
	IMPROVISED_PROJECTILES(8),

	ENERGIZING_MEAL_I(20),
	SCHOLARS_INTUITION(17),
	TESTED_HYPOTHESIS(18),
	BACKUP_BARRIER(19),
	ENERGIZING_MEAL_II(20),
	ENERGIZING_UPGRADE(21),
	WAND_PRESERVATION(22),
	ARCANE_VISION(23), // TODO adjust
	SHIELD_BATTERY(24),

	CACHED_RATIONS(32),
	THIEFS_INTUITION(33),
	SUCKER_PUNCH(34),
	MENDING_SHADOWS(35),
	MYSTICAL_MEAL(36),
	MYSTICAL_UPGRADE(37),
	WIDE_SEARCH(38),
	SILENT_STEPS(39),
	ROGUES_FORESIGHT(40),

	NATURES_BOUNTY(48),
	SURVIVALISTS_INTUITION(49),
	FOLLOWUP_STRIKE(50),
	NATURES_AID(51),
	INVIGORATING_MEAL(52),
	RESTORED_NATURE(53),
	REJUVENATING_STEPS(54),
	HEIGHTENED_SENSES(55),
	DURABLE_PROJECTILES(56),
	// TODO add unique icons
	ROYAL_PRIVILEGE(16), // food related talents, uses empowering icon
	ROYAL_INTUITION(49), // intuition-related talents, uses survivalist's icon
	KINGS_WISDOM(18), // on-id + combat talents, uses tested hypothesis
	NOBLE_CAUSE(3), // other ones. uses iron will
	ROYAL_MEAL(20), //// all on-eat talents for tier 2. uses energizing meal
	RESTORATION(5), // all upgrade/potion of healing talents, uses restored willpower icon
	POWER_WITHIN(40), // runic (3), wand preservation (3), rogue's foresight (5), rejuvenating steps (3), uses foresight.
	KINGS_VISION(55), // improvised projectiles (4), arcane vision(4), wide search(3), heightened senses(4), uses heightened senses
	PURSUIT(50); // durable projectiles (5),silent steps(4),lethal momentum (3),shield battery(5), uses FUS
	// TODO is splitting up t2s arbitrarily really a good idea?
	public static class ImprovisedProjectileCooldown extends FlavourBuff{};
	public static class LethalMomentumTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{};

	int icon;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	Talent(int icon ){
		this.icon = icon;
	}

	public int icon(){
		return icon;
	}

	public int maxPoints(){
		return 2;
	}

	public String title(){
		return Messages.get(this, name() + ".title");
	}

	public String desc(){
		return Messages.get(this, name() + ".desc");
	}

	public static class ScholarsIntuitionTracker extends Buff { // this is for +1 mages intuition
		// tracks failed identifies.
		private HashSet<Class> failed = new HashSet<>();

		public boolean attemptIntuition(Item item) {
			if(!appliesTo(item)) return false;
			Class itemClass = item.getClass();
			if(item instanceof ExoticScroll) itemClass = ExoticScroll.exoToReg.get(itemClass);
			if(item instanceof ExoticPotion) itemClass = ExoticPotion.exoToReg.get(itemClass);
			if(Random.Int(3) > 0) failed.add(itemClass);
			return !failed.contains(itemClass);
		}
		public boolean appliesTo(Item item) {
			return item instanceof Scroll || item instanceof Potion;
		}
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put("failed",failed.toArray(new Class[0]));
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			failed.addAll(Arrays.asList(bundle.getClassArray("failed")));
		}
	}

	public static void onTalentUpgraded( Hero hero, Talent talent){
		int points = hero.pointsInTalent(talent);
		switch(talent) {
			case ARMSMASTERS_INTUITION: case SCHOLARS_INTUITION: case THIEFS_INTUITION: case ROYAL_INTUITION:
				for(Item item : hero.belongings)
				{
					// rerun these.
					onItemCollected(hero, item);
					if(item.isEquipped(hero)) onItemEquipped(hero,item);
				}
				break;
			case ROYAL_PRIVILEGE: case NATURES_BOUNTY:
				if(hero.pointsInTalent(NATURES_BOUNTY) > 0) points++;
				Buff.count(hero, NatureBerriesAvailable.class, 2*points);
				break;
		}
	}

	public static class CachedRationsDropped extends CounterBuff{};
	public static class NatureBerriesAvailable extends CounterBuff{};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		if (hero.hasTalent(HEARTY_MEAL,ROYAL_PRIVILEGE)){
			// somehow I managed to make it even more confusing than before.
			int points = hero.pointsInTalents(HEARTY_MEAL, ROYAL_PRIVILEGE);
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
			int bonus = 1+hero.pointsInTalents(ROYAL_PRIVILEGE);
			Buff.affect( hero, WandEmpower.class).set(bonus, 3);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL_I,ROYAL_MEAL)){
			//5/8 turns of recharging for rat king, 4/6 for mage.
			int points = hero.pointsInTalents(ENERGIZING_MEAL_I,ROYAL_MEAL);
			Buff.affect( hero, Recharging.class, 2 + 3*points - hero.pointsInTalent(ENERGIZING_MEAL_I) );
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL_II)) {
			// 1/1.5 charges instantly replenished, does not overcharge at this time even though I implemented functionality for it.
			hero.belongings.charge(0.5f*(1+hero.pointsInTalent(ENERGIZING_MEAL_II)),false);
			if(!hero.hasTalent(ENERGIZING_MEAL_I)) ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(MYSTICAL_MEAL,ROYAL_MEAL)){
			//3/5 turns of recharging
			int duration = 1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL)+hero.pointsInTalent(ROYAL_MEAL));
			if(hero.hasTalent(MYSTICAL_MEAL)) duration *= 2;
			Buff.affect( hero, ArtifactRecharge.class).set(duration).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty && !hero.hasTalent(MYSTICAL_MEAL);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(INVIGORATING_MEAL)) {
			// 4.5/6 tiles -> 3/5 turns
			Buff.affect(hero, Adrenaline.class, 2+2*hero.pointsInTalent(INVIGORATING_MEAL));
		}
		if (hero.hasTalent(ROYAL_MEAL)) {
			//effectively 1/2 turns of haste
			Buff.affect( hero, Haste.class, 0.67f+hero.pointsInTalent(ROYAL_MEAL));
		}
	}

	public static class WarriorFoodImmunity extends FlavourBuff{};

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		// 1.75x/2.5x speed with huntress talent
		float factor = 1f + (hero.pointsInTalent(ROYAL_INTUITION) + hero.pointsInTalent(ROYAL_INTUITION))*0.75f;
		if(hero.pointsInTalent(SURVIVALISTS_INTUITION) > 0) factor *= 2.5;

		// 2x/instant for Warrior (see onItemEquipped)
		if (item instanceof MeleeWeapon || item instanceof Armor){
			int points = hero.pointsInTalents(ROYAL_INTUITION,ARMSMASTERS_INTUITION);
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
				int shieldToGive = Math.round(shield.maxShield() * 0.33f*(1+hero.pointsInTalents(RESTORED_WILLPOWER,RESTORATION)));
				shield.supercharge(shieldToGive);
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
					int duration = 1+hero.pointsInTalents(RESTORED_NATURE,RESTORATION);
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
			if (hero.pointsInTalents(RESTORED_NATURE,RESTORATION) == 1){
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
			int charge = hero.pointsInTalents(ENERGIZING_UPGRADE,RESTORATION);
			if(hero.hasTalent(ENERGIZING_UPGRADE)) charge = (int)Math.ceil(charge*1.5f);
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
				cloak.overCharge(hero.pointsInTalents(MYSTICAL_UPGRADE,RESTORATION));
				ScrollOfRecharging.charge( Dungeon.hero );
				SpellSprite.show( hero, SpellSprite.CHARGE );
			}
		}
	}

	public static void onItemEquipped( Hero hero, Item item ){
		if (hero.pointsInTalents(ARMSMASTERS_INTUITION,ROYAL_INTUITION) >= (hero.hasTalent(ARMSMASTERS_INTUITION) ? 1 : 2) && (item instanceof Weapon || item instanceof Armor)){
			item.identify();
		}
		if ((hero.heroClass == HeroClass.ROGUE || hero.hasTalent(ROYAL_INTUITION)) && item instanceof Ring){
			int points = hero.pointsInTalents(THIEFS_INTUITION,ROYAL_INTUITION);
			if(hero.heroClass == HeroClass.ROGUE ) points++; // essentially this is a 50% boost.
			if (points >= 2){
				item.identify();
			} else {
				((Ring) item).setKnown();
			}
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (hero.heroClass == HeroClass.ROGUE || hero.hasTalent(THIEFS_INTUITION,ROYAL_INTUITION)){
			int points = hero.pointsInTalents(THIEFS_INTUITION,ROYAL_INTUITION);
			if(hero.heroClass == HeroClass.ROGUE) points++;
			if (points == 3 && (item instanceof Ring || item instanceof Artifact)) item.identify();
			else if (points == 2 && item instanceof Ring) ((Ring) item).setKnown();
		}
		if (hero.pointsInTalent(ARMSMASTERS_INTUITION) == 2 &&
				(item instanceof Weapon || item instanceof Armor)) item.identify();
		// TODO revisit this is easily exploitable
		if( hero.pointsInTalent(SURVIVALISTS_INTUITION) == 2 && Random.Int(3) == 0){
			item.cursedKnown = true;
		}
		if( (item instanceof Scroll || item instanceof Potion) && !item.isIdentified() && hero.hasTalent(SCHOLARS_INTUITION) ) {
			if(hero.pointsInTalent(SCHOLARS_INTUITION) == 2 || Buff.affect(hero, ScholarsIntuitionTracker.class).attemptIntuition(item)) {
				item.identify();
				// this gets distracting if it happens every time, so it only does it at +1.
				if(hero.pointsInTalent(SCHOLARS_INTUITION) == 1) hero.sprite.emitter().burst(Speck.factory(Speck.QUESTION),1);
			}
		}
	}

	//note that IDing can happen in alchemy scene, so be careful with VFX here
	public static void onItemIdentified( Hero hero, Item item ){
		if (hero.hasTalent(TEST_SUBJECT,KINGS_WISDOM)){
			//heal for 2/3 HP
			int points = hero.pointsInTalents(TEST_SUBJECT,KINGS_WISDOM);
			int heal = 1 + points;
			if(hero.hasTalent(TEST_SUBJECT)) heal += points == 1 ? Random.Int(2) : 1; // 2-3/4
			heal = Math.min(heal, hero.HT-hero.HP);
			hero.HP += heal;
			Emitter e = hero.sprite.emitter();
			if (e != null && heal > 0) e.burst(Speck.factory(Speck.HEALING), heal == 1 ? 1 : points);
		}
		if (hero.hasTalent(TESTED_HYPOTHESIS,KINGS_WISDOM)){
			//2/3 turns of wand recharging
			int duration = 1 + hero.pointsInTalents(TESTED_HYPOTHESIS,KINGS_WISDOM);
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
					: Random.round(0.5f*(2+hero.pointsInTalents(KINGS_WISDOM))); // 1-2/2
			dmg += bonus;
			if(!hero.hasTalent(SUCKER_PUNCH)) Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE,KINGS_WISDOM)) {
			if (hero.belongings.weapon instanceof MissileWeapon) {
				Buff.affect(enemy, FollowupStrikeTracker.class);
			} else if (enemy.buff(FollowupStrikeTracker.class) != null){
				int bonus = 1 + hero.pointsInTalents(FOLLOWUP_STRIKE,KINGS_WISDOM); // 2/3
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

	public static final int MAX_TALENT_TIERS = 2;

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

		//tier 2+
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


		//tier 3+
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		//Nothing here yet. Hm.....
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
