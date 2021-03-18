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

package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Badges;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.items.BrokenSeal;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.wands.WandOfBlastWave;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.AttackIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;
import com.zrp200.rkpd2.windows.WndCombo;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Combo extends Buff implements ActionIndicator.Action {
	
	private int count = 0;
	private float comboTime = 0f;
	private float initialComboTime = baseComboTime();

	private static float baseComboTime() {
		return 5f+(Dungeon.hero != null ? Dungeon.hero.pointsInTalent(Talent.SKILL) : 0);
	}

	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}
	
	@Override
	public void tintIcon(Image icon) {
		ComboMove move = getHighestMove();
		if (move != null){
			icon.hardlight(move.tintColor & 0x00FFFFFF);
		} else {
			icon.resetColor();
		}
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (initialComboTime - comboTime)/ initialComboTime);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	public void hit( Char enemy ) {

		if(Dungeon.hero.pointsInTalent(Talent.SKILL) == 3 && Random.Int(3) == 0) count++;
		comboTime = baseComboTime();

		//TODO this won't count a kill on an enemy that gets corruped by corrupting I think?
		if (!enemy.isAlive() || enemy.buff(Corruption.class) != null){
			Hero hero = (Hero)target;
			int multiplier = hero.hasTalent(Talent.CLEAVE) ? 10 : 15;
			comboTime = Math.max(comboTime, multiplier*hero.pointsInTalent(Talent.CLEAVE,Talent.RK_GLADIATOR));
		}
		incCombo();
	}
	void incCombo() {
		count++;
		initialComboTime = comboTime;

		if ((getHighestMove() != null)) {

			ActionIndicator.setAction( this );
			Badges.validateMasteryCombo( count );

			GLog.p( Messages.get(this, "combo", count) );
			
		}

		BuffIndicator.refreshHero(); //refresh the buff visually on-hit

	}

	public void miss() {
		if(((Hero)target).pointsInTalent(Talent.SKILL) >= 2 && Random.Int(3) == 0) {
			comboTime = baseComboTime();
			incCombo();
		}
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override
	public boolean act() {
		comboTime-=TICK;
		spend(TICK);
		if (comboTime <= 0) {
			detach();
		}
		return true;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc",((Hero)target).subClass.title());
	}

	private static final String COUNT = "count";
	private static final String TIME  = "combotime";
	private static final String INITIAL_TIME  = "initialComboTime";

	private static final String CLOBBER_USED = "clobber_used";
	private static final String PARRY_USED   = "parry_used";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COUNT, count);
		bundle.put(TIME, comboTime);
		bundle.put(INITIAL_TIME, initialComboTime);

		bundle.put(CLOBBER_USED, clobberUsed);
		bundle.put(PARRY_USED, parryUsed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		count = bundle.getInt( COUNT );
		comboTime = bundle.getFloat( TIME );

		//pre-0.9.2
		if (bundle.contains(INITIAL_TIME))  initialComboTime = bundle.getFloat( INITIAL_TIME );
		else                                initialComboTime = 5;

		clobberUsed = bundle.getBoolean(CLOBBER_USED);
		parryUsed = bundle.getBoolean(PARRY_USED);

		if (getHighestMove() != null) ActionIndicator.setAction(this);
	}

	@Override
	public Image getIcon() {
		Image icon;
		if (((Hero)target).belongings.weapon != null){
			icon = new ItemSprite(((Hero)target).belongings.weapon.image, null);
		} else {
			icon = new ItemSprite(new Item(){ {image = ItemSpriteSheet.WEAPON_HOLDER; }});
		}

		icon.tint(getHighestMove().tintColor);
		return icon;
	}

	@Override
	public void doAction() {
		GameScene.show(new WndCombo(this));
	}

	@Override
	public boolean usable() {
		return getHighestMove() != null;
	}

	public enum ComboMove {
		CLOBBER(2, 0xFF00FF00),
		SLAM   (4, 0xFFCCFF00),
		PARRY  (6, 0xFFFFFF00),
		CRUSH  (8, 0xFFFFCC00),
		FURY   (10, 0xFFFF0000);

		public int comboReq, tintColor;

		ComboMove(int comboReq, int tintColor){
			this.comboReq = comboReq;
			this.tintColor = tintColor;
		}

		public String desc(){
			return Messages.get(this, name()+"_desc");
		}

	}

	private boolean clobberUsed = false;
	private boolean parryUsed = false;

	public ComboMove getHighestMove(){
		ComboMove best = null;
		for (ComboMove move : ComboMove.values()){
			if (count >= move.comboReq){
				best = move;
			}
		}
		return best;
	}

	public int getComboCount(){
		return count;
	}

	public boolean canUseMove(ComboMove move){
		if (move == ComboMove.CLOBBER && clobberUsed)   return false;
		if (move == ComboMove.PARRY && parryUsed)       return false;
		return move.comboReq <= count;
	}

	public void useMove(ComboMove move){
		if (move == ComboMove.PARRY){
			parryUsed = true;
			comboTime = 5f;
			Buff.affect(target, ParryTracker.class, Actor.TICK);
			((Hero)target).spendAndNext(Actor.TICK);
			Dungeon.hero.busy();
		} else {
			moveBeingUsed = move;
			GameScene.selectCell(listener);
		}
	}

	public static class ParryTracker extends FlavourBuff{
		{ actPriority = HERO_PRIO+1;}

		public boolean parried;

		@Override
		public void detach() {
			if (!parried && target.buff(Combo.class) != null) target.buff(Combo.class).detach();
			super.detach();
		}
	}

	public static class RiposteTracker extends Buff{
		{ actPriority = VFX_PRIO;}

		public Char enemy;

		@Override
		public boolean act() {
			if (target.buff(Combo.class) != null) {
				moveBeingUsed = ComboMove.PARRY;
				target.sprite.attack(enemy.pos, new Callback() {
					@Override
					public void call() {
						target.buff(Combo.class).doAttack(enemy);
						next();
					}
				});
				detach();
				return false;
			} else {
				detach();
				return true;
			}
		}
	}

	private static ComboMove moveBeingUsed;

	private void doAttack(final Char enemy){

		AttackIndicator.target(enemy);
		Buff.detach(target, Preparation.class); // not the point, otherwise this would be all that's done.

		boolean wasAlly = enemy.alignment == target.alignment;
		Hero hero = (Hero)target;

		if (enemy.defenseSkill(target) >= Char.INFINITE_EVASION){
			enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );
			Sample.INSTANCE.play(Assets.Sounds.MISS);

			} else if (enemy.isInvulnerable(target.getClass())){
			enemy.sprite.showStatus( CharSprite.POSITIVE, Messages.get(Char.class, "invulnerable") );
			Sample.INSTANCE.play(Assets.Sounds.MISS);

			} else {

			int dmg = target.damageRoll();
			if(hero.hasTalent(Talent.SKILL)) dmg = Math.max(target.damageRoll(), dmg); // free reroll. This will be rather...noticable on fury.

			//variance in damage dealt
			switch (moveBeingUsed) {
				case CLOBBER:
					dmg = 0;
					break;
				case SLAM:
					// reroll armor for gladiator
					int drRoll = target.drRoll();
					if(hero.hasTalent(Talent.SKILL)) drRoll = Math.max(drRoll, target.drRoll());
					dmg += Math.round(drRoll * count/5f);
					break;
				case CRUSH:
					dmg = Math.round(dmg * 0.25f*count);
					break;
				case FURY:
					dmg = Math.round(dmg * 0.6f);
					break;
			}

			dmg = enemy.defenseProc(target, dmg);
			dmg -= enemy.drRoll();

			if (enemy.buff(Vulnerable.class) != null) {
				dmg *= 1.33f;
			}

			dmg = target.attackProc(enemy, dmg);
			enemy.damage(dmg, target);

			//special effects
			switch (moveBeingUsed) {
				case CLOBBER:
					hit( enemy );
					if (enemy.isAlive()) {
						//trace a ballistica to our target (which will also extend past them
						Ballistica trajectory = new Ballistica(target.pos, enemy.pos, Ballistica.STOP_TARGET);
						//trim it to just be the part that goes past them
						trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
						//knock them back along that ballistica, ensuring they don't fall into a pit
						int dist = 2;
						if (hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 1 && count >= 4 || count >= 7 && hero.pointsInTalent(Talent.RK_GLADIATOR) >= 1){
							dist ++;
							Buff.prolong(enemy, Vertigo.class, 3);
						} else if (!enemy.flying) {
							while (dist > trajectory.dist ||
									(dist > 0 && Dungeon.level.pit[trajectory.path.get(dist)])) {
								dist--;
							}
						}
						WandOfBlastWave.throwChar(enemy, trajectory, dist, true, false);
					}
					break;
				case PARRY:
					hit( enemy );
					break;
				case CRUSH:
					WandOfBlastWave.BlastWave.blast(enemy.pos);
					PathFinder.buildDistanceMap(target.pos, Dungeon.level.passable, 3);
					for (Char ch : Actor.chars()){
						if (ch != enemy && ch.alignment == Char.Alignment.ENEMY
								&& PathFinder.distance[ch.pos] < Integer.MAX_VALUE){
							int aoeHit = Math.round(target.damageRoll() * 0.25f*count);
							aoeHit /= 2;
							aoeHit -= ch.drRoll();
							if (ch.buff(Vulnerable.class) != null) aoeHit *= 1.33f;
							ch.damage(aoeHit, target);
							ch.sprite.bloodBurstA(target.sprite.center(), dmg);
							ch.sprite.flash();

							if (!ch.isAlive()) {
								if (hero.hasTalent(Talent.LETHAL_DEFENSE,Talent.RK_GLADIATOR) && hero.buff(BrokenSeal.WarriorShield.class) != null){
									BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
									shield.supercharge(Math.round(shield.maxShield() * hero.pointsInTalent(Talent.LETHAL_DEFENSE,Talent.RK_GLADIATOR)
											/(hero.hasTalent(Talent.LETHAL_DEFENSE)?2f:3f)));
								}
							}
						}
					}
					break;
				default:
					//nothing
					break;
			}

				if (target.buff(FireImbue.class) != null)   target.buff(FireImbue.class).proc(enemy);
				if (target.buff(FrostImbue.class) != null)  target.buff(FrostImbue.class).proc(enemy);

			target.hitSound(Random.Float(0.87f, 1.15f));
			if (moveBeingUsed != ComboMove.FURY) Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			enemy.sprite.bloodBurstA(target.sprite.center(), dmg);
			enemy.sprite.flash();

			if (!enemy.isAlive()) {
				GLog.i(Messages.capitalize(Messages.get(Char.class, "defeat", enemy.name())));
			}

		}

		Invisibility.dispel();

		//Post-attack behaviour
		switch(moveBeingUsed){
			case CLOBBER:
				clobberUsed = true;
				if (getHighestMove() == null) ActionIndicator.clearAction(Combo.this);
				hero.spendAndNext(hero.attackDelay());
				break;

			case PARRY:
				//do nothing
				break;

			case FURY:
				count--;
				//fury attacks as many times as you have combo count
				if (count > 0 && enemy.isAlive() && hero.canAttack(enemy) &&
						(wasAlly || enemy.alignment != target.alignment)){
					target.sprite.attack(enemy.pos, new Callback() {
						@Override
						public void call() {
							doAttack(enemy);
						}
					});
				} else {
					detach();
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					ActionIndicator.clearAction(Combo.this);
					hero.spendAndNext(hero.attackDelay());
				}
				break;

			default:
				detach();
				ActionIndicator.clearAction(Combo.this);
				hero.spendAndNext(hero.attackDelay());
				break;
		}

		if (!enemy.isAlive() || (!wasAlly && enemy.alignment == target.alignment)) {
			if (hero.hasTalent(Talent.LETHAL_DEFENSE,Talent.RK_GLADIATOR) && hero.buff(BrokenSeal.WarriorShield.class) != null){
				BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
				shield.supercharge(Math.round(shield.maxShield() *
						(hero.pointsInTalent(Talent.LETHAL_DEFENSE)/2f + hero.pointsInTalent(Talent.RK_GLADIATOR)/3f)));
			}
		}

	}

	private CellSelector.Listener listener = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Char enemy = Actor.findChar( cell );
			if (enemy == null
					|| enemy == target
					|| !Dungeon.level.heroFOV[cell]
					|| target.isCharmedBy( enemy )) {
				GLog.w(Messages.get(Combo.class, "bad_target"));

			} else if (!((Hero)target).canAttack(enemy)){
				if (((Hero) target).pointsInTalent(Talent.ENHANCED_COMBO,Talent.RK_GLADIATOR) < 3
					|| Dungeon.level.distance(target.pos, enemy.pos) > 1 + target.buff(Combo.class).count/(((Hero)target).hasTalent(Talent.ENHANCED_COMBO)?2:3)){
					GLog.w(Messages.get(Combo.class, "bad_target"));
				} else {
					Ballistica c = new Ballistica(target.pos, enemy.pos, Ballistica.PROJECTILE);
					if (c.collisionPos == enemy.pos){
						Dungeon.hero.busy();
						target.sprite.jump(target.pos, c.path.get(c.dist-1), new Callback() {
							@Override
							public void call() {
								target.move(c.path.get(c.dist-1));
								Dungeon.level.occupyCell(target);
								Dungeon.observe();
								GameScene.updateFog();
								target.sprite.attack(cell, new Callback() {
									@Override
									public void call() {
										doAttack(enemy);
									}
								});
							}
						});
					} else {
						GLog.w(Messages.get(Combo.class, "bad_target"));
					}
				}

			} else {
				Dungeon.hero.busy();
				target.sprite.attack(cell, new Callback() {
					@Override
					public void call() {
						doAttack(enemy);
					}
				});
			}
		}

		@Override
		public String prompt() {
			return Messages.get(Combo.class, "prompt");
		}
	};
}
