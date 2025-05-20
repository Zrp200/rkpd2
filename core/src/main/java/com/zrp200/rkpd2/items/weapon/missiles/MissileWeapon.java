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

package com.zrp200.rkpd2.items.weapon.missiles;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Adrenaline;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.MagicImmune;
import com.zrp200.rkpd2.actors.buffs.Momentum;
import com.zrp200.rkpd2.actors.buffs.PinCushion;
import com.zrp200.rkpd2.actors.buffs.RevealedArea;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.spells.HolyWeapon;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.artifacts.CloakOfShadows;
import com.zrp200.rkpd2.items.bags.Bag;
import com.zrp200.rkpd2.items.bags.MagicalHolster;
import com.zrp200.rkpd2.items.rings.RingOfSharpshooting;
import com.zrp200.rkpd2.items.wands.WandOfDisintegration;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.enchantments.Projecting;
import com.zrp200.rkpd2.items.weapon.melee.MagesStaff;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.items.weapon.melee.RunicBlade;
import com.zrp200.rkpd2.items.weapon.missiles.darts.Dart;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.utils.SafeCast;

import java.util.ArrayList;

abstract public class MissileWeapon extends Weapon {

	{
		stackable = true;
		levelKnown = true;

		bones = true;

		defaultAction = AC_THROW;
		usesTargeting = true;
	}

	//whether or not this instance of the item exists purely to trigger its effect. i.e. no dropping
	public boolean spawnedForEffect = false;

	protected boolean sticky = true;
	
	public static final float MAX_DURABILITY = 100;
	protected float durability = MAX_DURABILITY;
	protected float baseUses = 10;
	
	public boolean holster;
	
	//used to reduce durability from the source weapon stack, rather than the one being thrown.
	protected MissileWeapon parent;

	@Override
	public int min() {
		if (Dungeon.hero != null){
			return Math.max(0, min(buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)));
		} else {
			return Math.max(0 , min( buffedLvl() ));
		}
	}
	
	@Override
	public int min(int lvl) {
		return  2 * tier +                      //base
				(tier == 1 ? lvl : 2*lvl);      //level scaling
	}
	
	@Override
	public int max() {
		if (Dungeon.hero != null){
			return Math.max(0, max( buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) ));
		} else {
			return Math.max(0 , max( buffedLvl() ));
		}
	}
	
	@Override
	public int max(int lvl) {
		return  5 * tier +                      //base
				(tier == 1 ? 2*lvl : tier*lvl); //level scaling
	}
	
	public int STRReq(int lvl){
		return STRReq(tier, lvl) - 1; //1 less str than normal for their tier
	}

	//use the parent item if this has been thrown from a parent
	public int buffedLvl(){
		if (parent != null) {
			return parent.buffedLvl();
		} else {
			return super.buffedLvl();
		}
	}

	@Override
	//FIXME some logic here assumes the items are in the player's inventory. Might need to adjust
	public Item upgrade() {
		if (!bundleRestoring) {
			durability = MAX_DURABILITY;
			if (quantity > 1) {
				MissileWeapon upgraded = (MissileWeapon) split(1);
				upgraded.parent = null;
				
				upgraded = (MissileWeapon) upgraded.upgrade();
				
				//try to put the upgraded into inventory, if it didn't already merge
				if (upgraded.quantity() == 1 && !upgraded.collect()) {
					Dungeon.level.drop(upgraded, Dungeon.hero.pos);
				}
				updateQuickslot();
				return upgraded;
			} else {
				super.upgrade();
				
				Item similar = Dungeon.hero.belongings.getSimilar(this);
				if (similar != null){
					detach(Dungeon.hero.belongings.backpack);
					Item result = similar.merge(this);
					updateQuickslot();
					return result;
				}
				updateQuickslot();
				return this;
			}
			
		} else {
			return super.upgrade();
		}
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (!hero.hasTalent(Talent.ELITE_DEXTERITY)) actions.remove( AC_EQUIP );
		return actions;
	}
	
	@Override
	public boolean collect(Bag container) {
		if (container instanceof MagicalHolster) holster = true;
		return super.collect(container);
	}
public boolean isSimilar( Item item ) {
		return level() == item.level() && getClass() == item.getClass();
	}

	@Override
	public int throwPos(Hero user, int dst) {

		boolean projecting = hasEnchant(Projecting.class, user);
		float projectingFactor = 0;
		if (!(this instanceof SpiritBow.SpiritArrow) && user.hasTalent(Talent.SHARED_ENCHANTMENT)) {
			// this implementation allows projecting to stack with projecting properly
			SpiritBow bow = user.belongings.getItem(SpiritBow.class);
			if (bow != null && bow.hasEnchant(Projecting.class, user)) {
				projectingFactor = user.pointsInTalent(Talent.SHARED_ENCHANTMENT)/3f;
			}
		}
		else if (!projecting && Random.Int(3) < user.pointsInTalent(Talent.RK_SNIPER)) {
			if (this instanceof Dart && ((Dart) this).crossbowHasEnchant(Dungeon.hero)){
				//do nothing
			} else {
				SpiritBow bow = Dungeon.hero.belongings.getItem(SpiritBow.class);
				if (bow != null && bow.hasEnchant(Projecting.class, user)) {
					projecting = true;
				}
			}
		}
		if(!projecting && Random.Int(3) < Dungeon.hero.pointsInTalent(Talent.SORCERY)) { // just like shared enchant... yay.
			MagesStaff staff = Dungeon.hero.belongings.getItem(MagesStaff.class);
			projecting = staff != null && staff.wand() instanceof WandOfDisintegration;
		}

		RunicBlade.RunicSlashTracker tracker = Dungeon.hero.buff(RunicBlade.RunicSlashTracker.class);
		boolean ignoreTracker = tracker != null;
		if (MeleeWeapon.activeAbility instanceof MeleeWeapon.MeleeAbility && ((MeleeWeapon.MeleeAbility)MeleeWeapon.activeAbility).weapon().hasEnchant(Projecting.class, Dungeon.hero)) {
			projecting = true;
			ignoreTracker = false;
		}
		if (ignoreTracker) tracker.detach();
		int throwPos;
		if (projecting) projectingFactor++;
		if (projectingFactor > 0
				&& (Dungeon.level.passable[dst] || Dungeon.level.avoid[dst] || Actor.findChar(dst) != null)
				&& Dungeon.level.distance(user.pos, dst) <= Math.round(4 * projectingFactor * Enchantment.genericProcChanceMultiplier(user))){
			throwPos = dst;
		} else {
			throwPos = super.throwPos(user, dst);
		}
		if (ignoreTracker) tracker.attachTo(Dungeon.hero);
		return throwPos;
	}

	@Override
	public float accuracyFactor(Char owner, Char target) {
		float accFactor = super.accuracyFactor(owner, target);
		if (owner instanceof Hero && owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()){
			accFactor *= 1f + 0.2f*((Hero) owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM,Talent.RK_FREERUNNER);
		}

		accFactor *= adjacentAccFactor(owner, target);

		return accFactor;
	}

	protected float rangedAccFactor(Char owner) {
		float factor = .5f;
		if(owner instanceof Hero) {
			// +50% / +70% / +90% / +110%
			factor += 0.2f*((Hero)owner).pointsInTalent(Talent.POINT_BLANK);
		};
		return 1 + factor;
	}

	protected float adjacentAccFactor(Char owner, Char target){
		if (Dungeon.level.adjacent( owner.pos, target.pos )) {
			float factor = 0.5f;
			if (owner instanceof Hero){
				// -50% / -30% / -10% / +10%
				int points = ((Hero)owner).pointsInTalent(Talent.POINT_BLANK,Talent.RK_SNIPER);
				factor += 0.2f*points;
			}
			return factor;
		} else {
			return rangedAccFactor(owner);
		}
	}

	@Override
	public void doThrow(Hero hero) {
		parent = null; //reset parent before throwing, just incase
		super.doThrow(hero);
	}

	@Override
	protected void onThrow( int cell ) {
		Char enemy = Actor.findChar( cell );
		if (enemy == null || enemy == curUser) {
			parent = null;

			//metamorphed seer shot logic
			if (curUser.hasTalent(Talent.SEER_SHOT)
					&& curUser.heroClass != HeroClass.HUNTRESS
					&& curUser.buff(Talent.SeerShotCooldown.class) == null){
				if (Actor.findChar(cell) == null) {
					// manually made consistent with rkpd2 logic
					new RevealedArea(cell).attachTo(curUser);
				}
			}

			if (!spawnedForEffect) super.onThrow( cell );
		} else curUser.shoot(enemy, this);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {

		RunicBlade.RunicSlashTracker tracker = Dungeon.hero.buff(RunicBlade.RunicSlashTracker.class);
		if (tracker != null) tracker.detach(); // don't use it for built in effects
		if (attacker instanceof Hero) {
			// separate shared enchantment logic purely in the interest of sniper buffs
			Hero hero = (Hero) attacker;
			if (hero.hasTalent(Talent.SHARED_ENCHANTMENT)) {
				// proc bow enchantment at 0.5x / 1x / 1.5x power
				SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
				if (bow.hasEnchant(Weapon.Enchantment.class, hero)) {
					procChanceMultiplier = 0.5f * hero.pointsInTalent(Talent.SHARED_ENCHANTMENT);
					try {
						damage = bow.enchantment.proc(this, attacker, defender, damage);
					} finally {
						procChanceMultiplier = 0;
					}
				}
			} else if (Random.Int(3) < hero.pointsInTalent(Talent.RK_SNIPER)) {
				if (this instanceof Dart && ((Dart) this).crossbowHasEnchant(hero)) {
					//do nothing
				} else {
					SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
					if (bow != null && bow.enchantment != null && hero.buff(MagicImmune.class) == null) {
						damage = bow.enchantment.proc(this, attacker, defender, damage);
					}
				}
			}
		}
		if (tracker != null) tracker.attachTo(Dungeon.hero); // reapply it

		MeleeWeapon.MeleeAbility abilityOverride = SafeCast.cast(MeleeWeapon.activeAbility, MeleeWeapon.MeleeAbility.class);
		if (abilityOverride != null) {
			MeleeWeapon wep = abilityOverride.weapon();
			// so balanced
			damage = wep.proc(attacker, defender, damage);
		}

		return super.proc(attacker, defender, damage);
	}

	@Override
	public Item random() {
		if (!stackable) return this;
		
		//2: 66.67% (2/3)
		//3: 26.67% (4/15)
		//4: 6.67%  (1/15)
		quantity = 2;
		if (Random.Int(3) == 0) {
			quantity++;
			if (Random.Int(5) == 0) {
				quantity++;
			}
		}
		return this;
	}

	public String status() {
		//show quantity even when it is 1
		String status = Integer.toString( quantity );

		if(Dungeon.hero.belongings.thirdWep() == this) {
			// show charges since it matters for the talent interactions
			MeleeWeapon.Charger charger = Buff.affect(Dungeon.hero, MeleeWeapon.Charger.class);
			status += " (" + charger.charges + "/" + charger.chargeCap() + ")";
		}
		return status;
	}
	
	@Override
	public float castDelay(Char user, int dst) {
		if(Talent.LethalMomentumTracker.apply(user)) return 0;
		float speedFactor = delayFactor( user );
		if(user instanceof Hero && ((Hero)user).hasTalent(Talent.ONE_MAN_ARMY)) {
			Hero hero = (Hero)user;
			int targets = 0;
			Char enemy = Actor.findChar(dst);
			for(Char c : Dungeon.level.mobs) if(c.alignment == Char.Alignment.ENEMY && (c == enemy || hero.canAttack(c) || c.canAttack(hero) || throwPos(hero,c.pos) == c.pos)) targets++;
			speedFactor /= 1 + .11f * hero.pointsInTalent(Talent.ONE_MAN_ARMY) * Math.max(0,targets-1);
		}
		return speedFactor / (user.buff(Adrenaline.class) != null?1.5f:1);
	}
	public void onRangedAttack(Char enemy, int cell, boolean hit) {
		if(hit) rangedHit(enemy, cell);
		else rangedMiss(cell);
		MeleeWeapon.markAbilityUsed();
	}

	protected void rangedHit( Char enemy, int cell ){
		decrementDurability();
		if (durability > 0 && !spawnedForEffect){
			//attempt to stick the missile weapon to the enemy, just drop it if we can't.
			if (sticky && enemy != null && enemy.isActive() && enemy.alignment != Char.Alignment.ALLY){
				PinCushion p = Buff.affect(enemy, PinCushion.class);
				if (p.target == enemy){
					p.stick(this);
					return;
				}
			}
			Dungeon.level.drop( this, cell ).sprite.drop();
		}
	}
	
	protected void rangedMiss( int cell ) {
		parent = null;
		if (!spawnedForEffect) super.onThrow(cell);
	}

	public float durabilityLeft(){
		return durability;
	}

	public void repair( float amount ){
		durability += amount;
		durability = Math.min(durability, MAX_DURABILITY);
	}

	public float durabilityPerUse(){
		//classes that override durabilityPerUse can turn rounding off, to do their own rounding after more logic
		return durabilityPerUse(true);
	}

	protected final float durabilityPerUse( boolean rounded){
		int level = level();
		if(Dungeon.hero.heroClass == HeroClass.ROGUE && Dungeon.hero.buff(CloakOfShadows.cloakStealth.class) != null) level++;
		float usages = baseUses * (float)(Math.pow(3, level));

		final float[] u = {usages};
		if (Dungeon.hero != null) Dungeon.hero.byTalent(
				(talent, points) -> {
					float boost = 0.25f * (1 + points); 					// +50% / +75%
					if(talent == Talent.DURABLE_PROJECTILES) boost *= 2; 	// +100% / +150%
					u[0] *= 1 + boost;
				}, Talent.DURABLE_PROJECTILES, Talent.PURSUIT);
		usages = u[0];
		if (holster) {
			usages *= MagicalHolster.HOLSTER_DURABILITY_FACTOR;
		}

		if (Dungeon.hero != null) usages *= RingOfSharpshooting.durabilityMultiplier( Dungeon.hero );

		//at 100 uses, items just last forever.
		if (usages >= 100f) return 0;

		if (rounded){usages = Math.round(usages);

		//add a tiny amount to account for rounding error for calculations like 1/3
		return (MAX_DURABILITY/usages) + 0.001f;} else {
			//rounding can be disabled for classes that override durability per use
			return MAX_DURABILITY/usages;
		}
	}
	
	protected void decrementDurability(){
		//if this weapon was thrown from a source stack, degrade that stack.
		//unless a weapon is about to break, then break the one being thrown
		if (parent != null){
			if (parent.durability <= parent.durabilityPerUse()){
				durability = 0;
				parent.durability = MAX_DURABILITY;
				if (parent.durabilityPerUse() < 100f) {
					GLog.n(Messages.get(this, "has_broken"));
				}
			} else {
				parent.durability -= parent.durabilityPerUse();
				if (parent.durability > 0 && parent.durability <= parent.durabilityPerUse()){
					GLog.w(Messages.get(this, "about_to_break"));
				}
			}
			parent = null;
		} else {
			durability -= durabilityPerUse();
			if (durability > 0 && durability <= durabilityPerUse()){
				GLog.w(Messages.get(this, "about_to_break"));
			} else if (durabilityPerUse() < 100f && durability <= 0){
				GLog.n(Messages.get(this, "has_broken"));
			}
		}
	}
	
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));
		
		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Hero.heroDamageIntRange( 0, exStr );
			}
			if (owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()) {
				damage = Math.round(damage * (1f + 0.15f * ((Hero) owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM,Talent.RK_FREERUNNER)));
			}
		}
		
		return damage;
	}
	
	@Override
	public void reset() {
		super.reset();
		durability = MAX_DURABILITY;
	}
	
	@Override
	public Item merge(Item other) {
		super.merge(other);
		if (isSimilar(other)) {
			durability += ((MissileWeapon)other).durability;
			durability -= MAX_DURABILITY;
			while (durability <= 0){
				quantity -= 1;
				durability += MAX_DURABILITY;
			}
		}
		return this;
	}
	
	@Override
	public Item split(int amount) {
		bundleRestoring = true;
		Item split = super.split(amount);
		bundleRestoring = false;
		
		//unless the thrown weapon will break, split off a max durability item and
		//have it reduce the durability of the main stack. Cleaner to the player this way
		if (split != null){
			MissileWeapon m = (MissileWeapon)split;
			m.durability = MAX_DURABILITY;
			m.parent = this;
		}
		
		return split;
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		parent = null;
		return super.doPickUp(hero, pos);
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public String info() {

		String info = super.info();
		
		info += "\n\n" + Messages.get( MissileWeapon.class, "stats",
				tier,
				Math.round(augment.damageFactor(min())),
				Math.round(augment.damageFactor(max())),
				STRReq());

		if (Dungeon.hero != null) {
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (Dungeon.hero.STR() > STRReq()) {
				info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
			}
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}
		if (isHoly()) {
			info += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", Messages.get(HolyWeapon.class, "ench_name", Messages.get(Enchantment.class, "enchant"))));
			info += " " + Messages.get(HolyWeapon.class, "ench_desc");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}

		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");
		
		info += "\n\n" + Messages.get(this, "durability");
		
		if (durabilityPerUse() > 0){
			info += " " + Messages.get(this, "uses_left",
					(int)Math.ceil(durability/durabilityPerUse()),
					(int)Math.ceil(MAX_DURABILITY/durabilityPerUse()));
		} else {
			info += " " + Messages.get(this, "unlimited_uses");
		}
		
		
		return info;
	}

	protected boolean isHoly() {
		return Dungeon.hero != null &&
				Dungeon.hero.buff(HolyWeapon.HolyWepBuff.Empowered.class) != null && (
				Dungeon.hero.belongings.contains(this)
						|| Dungeon.hero.belongings.contains(parent)
		);
	}

	@Override
	public ItemSprite.Glowing glowing() {
		ItemSprite.Glowing glowing = super.glowing();
		return glowing != null ? glowing : isHoly() ? HOLY : null;
	}
	
	@Override
	public int value() {
		return 6 * tier * quantity * (level() + 1);
	}

	private static final String SPAWNED = "spawned";
	private static final String DURABILITY = "durability";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPAWNED, spawnedForEffect);
		bundle.put(DURABILITY, durability);
	}
	
	private static boolean bundleRestoring = false;
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		bundleRestoring = true;
		super.restoreFromBundle(bundle);
		bundleRestoring = false;
		spawnedForEffect = bundle.getBoolean(SPAWNED);
		durability = bundle.getFloat(DURABILITY);
	}

	public static class PlaceHolder extends MissileWeapon {

		{
			image = ItemSpriteSheet.MISSILE_HOLDER;
		}

		@Override
		public boolean isSimilar(Item item) {
			return item instanceof MissileWeapon;
		}

		@Override
		public String info() {
			return "";
		}
	}
}
