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

package com.zrp200.rkpd2.items.artifacts;


import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Barrier;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.LockedFloor;
import com.zrp200.rkpd2.actors.buffs.Preparation;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.bags.Bag;
import com.zrp200.rkpd2.items.rings.RingOfEnergy;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class CloakOfShadows extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_CLOAK;

		exp = 0;
		levelCap = 10;

		charge = Math.min(level()+3, 10);
		partialCharge = 0;
		chargeCap = Math.min(level()+3, 10);

		defaultAction = AC_STEALTH;

		unique = true;
		bones = false;
	}

	public static final float ROGUE_BOOST = 4/3f;

	public static final String AC_STEALTH = "STEALTH";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if ((isEquipped( hero ) || hero.hasTalent(Talent.LIGHT_CLOAK,Talent.RK_FREERUNNER))
				&& !cursed && (charge > 0 || activeBuff != null)) {
			actions.add(AC_STEALTH);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (action.equals( AC_STEALTH )) {

			if (activeBuff == null){
				if (!isEquipped(hero) && !hero.hasTalent(Talent.LIGHT_CLOAK,Talent.RK_FREERUNNER)) GLog.i( Messages.get(Artifact.class, "need_to_equip") );
				else if (cursed)       GLog.i( Messages.get(this, "cursed") );
				else if (charge <= 0)  GLog.i( Messages.get(this, "no_charge") );
				else {
					hero.spend( 1f );
					hero.busy();
					Sample.INSTANCE.play(Assets.Sounds.MELD);
					activeBuff = activeBuff();
					activeBuff.attachTo(hero);
					if (hero.sprite.parent != null) {
						hero.sprite.parent.add(new AlphaTweener(hero.sprite, 0.4f, 0.4f));
					} else {
						hero.sprite.alpha(0.4f);
					}
					Talent.onArtifactUsed(Dungeon.hero);
					hero.sprite.operate(hero.pos);
				}
			} else {
				activeBuff.detach();
				activeBuff = null;
				hero.spend( 1f );
				hero.sprite.operate( hero.pos );
			}

		}
	}

	@Override
	public void activate(Char ch){
		super.activate(ch);
		if (activeBuff != null){
			activeBuff.attachTo(ch);
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			if (hero.hasTalent(Talent.LIGHT_CLOAK,Talent.RK_FREERUNNER)){
				activate(hero);
			}

			return true;
		} else
			return false;
	}

	@Override
	public boolean collect( Bag container ) {
		if (super.collect(container)){
			if (container.owner instanceof Hero
					&& passiveBuff == null
					&& ((Hero) container.owner).hasTalent(Talent.LIGHT_CLOAK,Talent.RK_FREERUNNER)){
				activate((Hero) container.owner);
			}
			return true;
		} else{
			return false;
		}
	}

	@Override
	protected void onDetach() {
		if (passiveBuff != null){
			passiveBuff.detach();
			passiveBuff = null;
		}
		if (activeBuff != null){
			activeBuff.detach();
			activeBuff = null;
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new cloakRecharge();
	}

	@Override
	protected ArtifactBuff activeBuff( ) {
		return new cloakStealth();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (charge < chargeCap) {
			if (!isEquipped(target)) amount *= target.pointsInTalent(Talent.LIGHT_CLOAK,Talent.RK_FREERUNNER)/
					(target.hasTalent(Talent.LIGHT_CLOAK)?4f:10f); // moved previous "equip for free" mechanic to light cloak.
			if(target.heroClass == HeroClass.ROGUE) amount *= ROGUE_BOOST;
			partialCharge += 0.25f*amount;
			if (partialCharge >= 1){
				partialCharge--;
				charge++;
				updateQuickslot();
			}
		}
	}

	public void overCharge(int amount){
		charge = Math.min(charge+amount, chargeCap+amount);
		updateQuickslot();
	}
	
	@Override
	public Item upgrade() {
		chargeCap = Math.min(chargeCap + 1, 10);
		return super.upgrade();
	}

	private static final String STEALTHED = "stealthed";
	private static final String BUFF = "buff";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		if (activeBuff != null) bundle.put(BUFF, activeBuff);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(BUFF)){
			activeBuff = new cloakStealth();
			activeBuff.restoreFromBundle(bundle.getBundle(BUFF));
		}
	}

	@Override
	public int value() {
		return 0;
	}

	public class cloakRecharge extends ArtifactBuff{
		@Override
		public boolean act() {
			if (charge < chargeCap) {
				LockedFloor lock = target.buff(LockedFloor.class);
				if (activeBuff == null && (lock == null || lock.regenOn())) {
					float missing = (chargeCap - charge);
					if (level() > 7) missing += 5*(level() - 7)/3f;
					float turnsToCharge = (45 - missing);
					if(((Hero)target).heroClass == HeroClass.ROGUE) turnsToCharge /= ROGUE_BOOST;
					turnsToCharge /= RingOfEnergy.artifactChargeMultiplier(target);
					float chargeToGain = (1f / turnsToCharge);
					if (!isEquipped(Dungeon.hero)){
						chargeToGain *= (Dungeon.hero.hasTalent(Talent.LIGHT_CLOAK) ? .25f : 0.1f) *
								Dungeon.hero.pointsInTalent(Talent.LIGHT_CLOAK,Talent.RK_FREERUNNER);
					}
					partialCharge += chargeToGain;
				}

				if (partialCharge >= 1) {
					charge++;
					partialCharge -= 1;
					if (charge == chargeCap){
						partialCharge = 0;
					}

				}
			} else
				partialCharge = 0;

			if (cooldown > 0)
				cooldown --;

			updateQuickslot();

			spend( TICK );

			return true;
		}

	}

	public class cloakStealth extends ArtifactBuff{
		
		{
			type = buffType.POSITIVE;
		}
		
		int turnsToCost = 0;

		@Override
		public int icon() {
			return BuffIndicator.INVISIBLE;
		}

		@Override
		public float iconFadePercent() {
			return (4f - turnsToCost) / 4f;
		}

		@Override
		public boolean attachTo( Char target ) {
			if (super.attachTo(target)) {
				target.invisible++;
				if (target instanceof Hero
						&& (((Hero) target).subClass == HeroSubClass.ASSASSIN
						|| ((Hero)target).subClass == HeroSubClass.KING)) {
					Buff.affect(target, Preparation.class);
				}
				return true;
			} else {
				return false;
			}
		}

		float inc = 1;

		@Override
		public boolean act(){
			turnsToCost--;
			Hero target = (Hero)this.target;
			if(target.hasTalent(Talent.MENDING_SHADOWS)) {
				// heal every 2/1 turns
				if (++inc >= 3-target.pointsInTalent(Talent.MENDING_SHADOWS)){
					inc = 0;
					if(target.HP != target.HT) {
						target.HP = Math.min(target.HT, target.HP + 1);
						target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
					}
				}
			}
			//barrier every 2/1 turns, to a max of 3/5
			if (target.hasTalent(Talent.NOBLE_CAUSE)){
				Barrier barrier = Buff.affect(target, Barrier.class);
				int points = target.pointsInTalent(Talent.NOBLE_CAUSE);
				if (barrier.shielding() < 1 + 2*points) {
					inc += 0.5f*points;
				}
				if (inc >= 1 ){
					inc = 0;
					barrier.incShield(1);
				}
			}
			
			if (turnsToCost <= 0){
				charge--;
				if (charge < 0) {
					charge = 0;
					detach();
					GLog.w(Messages.get(this, "no_charge"));
					target.interrupt();
				} else {
					//target hero level is 1 + 2*cloak level
					int lvlDiffFromTarget = target.lvl - (1+level()*2);
					//plus an extra one for each level after 6
					if (level() >= 7){
						lvlDiffFromTarget -= level()-6;
					}
					if (lvlDiffFromTarget >= 0){
						exp += Math.round(10f * Math.pow(1.1f, lvlDiffFromTarget));
					} else {
						exp += Math.round(10f * Math.pow(0.75f, -lvlDiffFromTarget));
					}

					int expPerLevel = 50;
					if (exp >= (level() + 1) * expPerLevel && level() < levelCap) {
						upgrade();
						exp -= level() * expPerLevel;
						GLog.p(Messages.get(this, "levelup"));
						
					}
					turnsToCost = 4;
				}
				updateQuickslot();
			}

			spend( TICK );

			return true;
		}

		public void dispel(){
			updateQuickslot();
			detach();
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add( CharSprite.State.INVISIBLE );
			else if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		@Override
		public void detach() {
			activeBuff = null;

			if (target.invisible > 0)   target.invisible--;

			updateQuickslot();
			super.detach();
		}
		
		private static final String TURNSTOCOST = "turnsToCost";
		private static final String BARRIER_INC = "barrier_inc";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			
			bundle.put( TURNSTOCOST , turnsToCost);
			bundle.put( BARRIER_INC, inc);
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			
			turnsToCost = bundle.getInt( TURNSTOCOST );
			inc = bundle.getFloat( BARRIER_INC );
		}
	}
}
