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

package com.zrp200.rkpd2.items.weapon.melee;

import static com.zrp200.rkpd2.Dungeon.hero;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.ArtifactRecharge;
import com.zrp200.rkpd2.actors.buffs.Barrier;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Haste;
import com.zrp200.rkpd2.actors.buffs.LockedFloor;
import com.zrp200.rkpd2.actors.buffs.MonkEnergy;
import com.zrp200.rkpd2.actors.buffs.Recharging;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.KindOfWeapon;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.AttackIndicator;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MeleeWeapon extends Weapon {

	public static String AC_ABILITY = "ABILITY";

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (hasAbility() && ch == hero){
			Buff.affect(ch, Charger.class);
		}
	}

	protected boolean hasAbility() {
		return hero != null && hero.heroClass.is(HeroClass.DUELIST);
	} @Override public String defaultAction() {
		if (hasAbility()){
			return AC_ABILITY;
		} else {
			return super.defaultAction();
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (hasAbility()){
			actions.add(AC_ABILITY);
		}
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(AC_ABILITY)){
			return Messages.upperCase(Messages.get(this, "ability_name"));
		} else {
			return super.actionName(action, hero);
		}
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_ABILITY)){
			if (!isEquipped(hero)) {
				if (hero.hasTalent(Talent.SWIFT_EQUIP)){
					if (hero.buff(Talent.SwiftEquipCooldown.class) == null
						|| hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()){
						execute(hero, AC_EQUIP);
					} else {
						GLog.w(Messages.get(this, "ability_need_equip"));
						usesTargeting = false;
					}
				} else {
					GLog.w(Messages.get(this, "ability_need_equip"));
					usesTargeting = false;
				}
			} else if (STRReq() > hero.STR()){
				GLog.w(Messages.get(this, "ability_low_str"));
				usesTargeting = false;
				// fixme fix +3 effect of elite dexterity, current charges is undefined behavior
			} else {
				int slot = hero.belongings.findWeapon(this);
				Charger charger = Buff.affect(hero, Charger.class);
				if (slot != -1 && charger.charges[slot] < abilityChargeUse(hero)) {
					GLog.w(Messages.get(this, "ability_no_charge"));
					usesTargeting = false;
				} else {

					if (targetingPrompt() == null){
						duelistAbility(hero, hero.pos);
						updateQuickslot();
					} else {
						usesTargeting = useTargeting();
						GameScene.selectCell(new CellSelector.Listener() {
							@Override
							public void onSelect(Integer cell) {
								if (cell != null) {
									duelistAbility(hero, cell);
									updateQuickslot();
								}
							}

							@Override
							public String prompt() {
								return targetingPrompt();
							}
						});
					}
				}
			}
		}
	}

	@Override
	public boolean doEquip(Hero hero, int slot) {
		if (super.doEquip(hero, slot)){
			ActionIndicator.updateIcon();
			return true;
		}
		return false;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			ActionIndicator.updateIcon();
			return true;
		}
		return false;
	}

	//leave null for no targeting
	public String targetingPrompt(){
		return null;
	}

	public boolean useTargeting(){
		return targetingPrompt() != null;
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return dst; //weapon abilities do not use projectile logic, no autoaim
	}

	protected void duelistAbility( Hero hero, Integer target ){
		//do nothing by default
	}

	protected void beforeAbilityUsed(Hero hero ){
		hero.belongings.abilityWeapon = this;
		Buff.affect(hero, Charger.class).gainCharge(
				-abilityChargeUse(hero),
				hero.belongings.findWeapon(this)
		);

		if (hero.heroClass == HeroClass.DUELIST
				&& hero.hasTalent(Talent.AGGRESSIVE_BARRIER)
				&& (hero.HP / (float)hero.HT) < 0.20f*(1+hero.pointsInTalent(Talent.AGGRESSIVE_BARRIER))){
			Buff.affect(hero, Barrier.class).setShield(3);
		}

		if (hero.buff(Talent.CombinedLethalityAbilityTracker.class) != null
				&& hero.buff(Talent.CombinedLethalityAbilityTracker.class).weapon != null
				&& hero.buff(Talent.CombinedLethalityAbilityTracker.class).weapon != this){
			Buff.affect(hero, Talent.CombinedLethalityTriggerTracker.class, 5f);
		}

		updateQuickslot();
	}

	protected void afterAbilityUsed( Hero hero ){
		hero.belongings.abilityWeapon = null;
		if (hero.hasTalent(Talent.COMBINED_LETHALITY)) {
			Talent.CombinedLethalityAbilityTracker tracker = hero.buff(Talent.CombinedLethalityAbilityTracker.class);
			if (tracker == null || tracker.weapon == this || tracker.weapon == null){
				Buff.affect(hero, Talent.CombinedLethalityAbilityTracker.class, hero.cooldown()).weapon = this;
			} else {
				//we triggered the talent, so remove the tracker
				tracker.detach();
			}
		}
		if (hero.hasTalent(Talent.COMBINED_ENERGY)){
			Talent.CombinedEnergyAbilityTracker tracker = hero.buff(Talent.CombinedEnergyAbilityTracker.class);
			if (tracker == null || tracker.energySpent == -1){
				Buff.prolong(hero, Talent.CombinedEnergyAbilityTracker.class, hero.cooldown()).wepAbilUsed = true;
			} else {
				tracker.wepAbilUsed = true;
				Buff.affect(hero, MonkEnergy.class).processCombinedEnergy(tracker);
			}
		}
		if (hero.buff(Talent.CounterAbilityTacker.class) != null){
			hero.buff(Talent.CounterAbilityTacker.class).detach();
		}
	}

	public void onAbilityKill( Hero hero ){
		Talent.LethalHasteCooldown.applyLethalHaste(hero, true);
	}

	public float abilityChargeUse( Hero hero ){
		float chargeUse = 1f;
		if (hero.buff(Talent.CounterAbilityTacker.class) != null){
			chargeUse = Math.max(0, chargeUse-0.5f*hero.pointsInTalent(Talent.COUNTER_ABILITY));
		}
		if (hero.hasTalent(Talent.LIGHTWEIGHT_CHARGE) && tier <= 3){
			// T1/2/3 get 25/20/15% charge use reduction at +3
			float chargeUseReduction = (0.30f-.05f*tier) * (hero.pointsInTalent(Talent.LIGHTWEIGHT_CHARGE)/3f);
			chargeUse *= 1f - chargeUseReduction;
		}
		return chargeUse;
	}

	@Override
	public int min(int lvl) {
		return  tier +  //base
				lvl;    //level scaling
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+1) +    //base
				lvl*(tier+1);   //level scaling
	}

	public int STRReq(int lvl){
		return STRReq(tier, lvl);
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));

		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
		}
		
		return damage;
	}
	
	@Override
	public String info() {

		String info = desc();

		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (STRReq() > hero.STR()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (hero.STR() > STRReq()){
				info += " " + Messages.get(Weapon.class, "excess_str", hero.STR() - STRReq());
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(hero.getBonus(this)), max(hero.getBonus(this)), STRReq(0));
			if (STRReq(0) > hero.STR()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", enchantment.name()));
			info += " " + enchantment.desc();
		}

		if (cursed && isEquipped( hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			if (enchantment != null && enchantment.curse()) {
				info += "\n\n" + Messages.get(Weapon.class, "weak_cursed");
			} else {
				info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
			}
		}

		//the mage's staff has no ability as it can only be gained by the mage
		if (hero.heroClass == HeroClass.DUELIST && !(this instanceof MagesStaff)){
			info += "\n\n" + Messages.get(this, "ability_desc");
		}

		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}

	@Override
	public String status() {
		Charger buff = hero.buff(Charger.class);
		int slot = hero.belongings.findWeapon(this);
		if (slot >= 0) {
			return (int)buff.charges[slot] + "/" + buff.chargeCap(slot);
		} else {
			return super.status();
		}
	}

	@Override
	public int value() {
		int price = 20 * tier;
		if (hasGoodEnchant()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public static class Charger extends Buff implements ActionIndicator.Action {

		public float[] charges = {3,3,3};

		@Override
		public boolean act() {
			LockedFloor lock = target.buff(LockedFloor.class);
			if (lock == null || lock.regenOn()) for (int i = 0; i < nSlots(); i++) {
				gainCharge( chargeMultiplier(i)/(40f-(chargeCap(i)-charges[i])), i ); // 40 to 30 turns per charge
				// secondary: 80 to 60 turns per charge without talent
				// up to 53.333 to 40 turns per charge at max talent level
			}

			int points = ((Hero)target).pointsInTalent(Talent.WEAPON_RECHARGING);
			if (points > 0 && target.buff(Recharging.class) != null || target.buff(ArtifactRecharge.class) != null){
				//shpd: 1 every 10 turns at +1, 6 turns at +2
				//rkpd2: 1 every 5 turns at +1, 3 turns at +2
				gainCharge(1/(7f - 2f*points));
			}

			// todo might have to check if I need to remove this check.
			if (ActionIndicator.action != this && hero.subClass == HeroSubClass.CHAMPION) {
				ActionIndicator.setAction(this);
			}

			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on) ActionIndicator.setAction(this);
		}

		@Override
		public void detach() {
			super.detach();
			ActionIndicator.clearAction(this);
		}

		public int chargeCap(){
			return Math.min(10, 3 + (hero.lvl-1)/3)
					* (hero.heroClass == HeroClass.DUELIST ? 2 : 1);
		}

		public static int nSlots() {
			return hero.subClass != HeroSubClass.CHAMPION ? 1 :
					// elite dexterity lets you equip weapons at +3
					Math.max(hero.pointsInTalent(Talent.ELITE_DEXTERITY), 2);
		}

		public int chargeCap(int n) {
			return Math.round(chargeCap() * chargeMultiplier(n));
		}

		public float chargeMultiplier(int i) {
			//50% - 75%, depending on talent
			return (float)Math.pow(0.5f + 0.0834f* hero.pointsInTalent(Talent.SECONDARY_CHARGE), i);
		}

		public final void gainCharge( float charge ) { gainCharge(charge, 0); }
		public void gainCharge( float charge, int slot ){
			charges[slot] = Math.max(Math.min(charges[slot] + charge, chargeCap(slot)), 0);
			updateQuickslot();
		}

		public static final String CHARGES          = "charges";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CHARGES, charges);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			charges = bundle.getFloatArray(CHARGES);
		}

		@Override
		public String actionName() {
			return Messages.get(MeleeWeapon.class, "swap");
		}

		@Override
		public Image actionIcon() {
			if (hero.belongings.weapon == null){
				return new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER);
 			} else {
				return new ItemSprite(hero.belongings.weapon);
			}
		}

		@Override
		public boolean usable() {
			return target == hero && hero.subClass == HeroSubClass.CHAMPION;
		}

		@Override
		public void doAction() {
			if ( !usable() ) return;

			if (hero.belongings.secondWep == null && hero.belongings.backpack.items.size() >= hero.belongings.backpack.capacity()){
				GLog.w(Messages.get(MeleeWeapon.class, "swap_full"));
				return;
			}

			KindOfWeapon temp = hero.belongings.weapon;
			if (hero.belongings.thirdWep instanceof MeleeWeapon) {
				hero.belongings.weapon = hero.belongings.thirdWep; // 2 -> 0
				hero.belongings.thirdWep = hero.belongings.secondWep; // 1 -> 2
			} else {
				hero.belongings.weapon = hero.belongings.secondWep;
			}
			hero.belongings.secondWep = temp; // 0 -> 1

			hero.sprite.operate(hero.pos);
			Sample.INSTANCE.play(Assets.Sounds.UNLOCK);

			ActionIndicator.setAction(this);
			Item.updateQuickslot();
			AttackIndicator.updateState();
		}
	}

}
