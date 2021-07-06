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

import com.badlogic.gdx.utils.IntIntMap;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroAction;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.mobs.npcs.NPC;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Effects;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.BArray;
import com.zrp200.rkpd2.utils.GLog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zrp200.rkpd2.actors.hero.HeroSubClass.KING;

public class Preparation extends Buff implements ActionIndicator.Action {
	
	{
		//always acts after other buffs, so invisibility effects can process first
		actPriority = BUFF_PRIO - 1;
	}
	
	public enum AttackLevel{
		LVL_1( 1, 0.15f, 1),
		LVL_2( 3, 0.30f, 1),
		LVL_3( 5, 0.45f, 2),
		LVL_4( 9, 0.60f, 3);

		final int turnsReq;
		final float baseDmgBonus;
		final int damageRolls;
		
		AttackLevel( int turns, float base, int rolls){
			turnsReq = turns;
			baseDmgBonus = base;
			damageRolls = rolls;
		}

		//1st index is prep level, 2nd is talent level
		private static final float[][] KOThresholds = new float[][]{
				{.03f, .04f, .05f, .06f},
				{.10f, .13f, .17f, .20f},
				{.20f, .27f, .33f, .40f},
				{.50f, .67f, .83f, 1.0f}
		};

		public float KOThreshold(){
			return KOThresholds[ordinal()][Dungeon.hero.pointsInTalent(Talent.ENHANCED_LETHALITY,Talent.RK_ASSASSIN)];
		}

		//1st index is prep level, 2nd is talent level, third is type.
		private static final int[][][] blinkRanges = new int[][][]{
				{{1, 2, 3, 4}, {1, 2, 3, 4}},
				{{1, 3, 4, 6}, {2, 3, 5, 6}},
				{{2, 4, 6, 8}, {3, 5, 7,10}},
				{{2, 5, 7,10}, {4, 7,10,14}}
		};

		public int blinkDistance(){
			return blinkRanges[Dungeon.hero.pointsInTalent(Talent.ASSASSINS_REACH,Talent.RK_ASSASSIN)][Dungeon.hero.hasTalent(Talent.ASSASSINS_REACH)?1:0][ordinal()];
		}
		
		public boolean canKO(Char defender){
			if (defender.properties().contains(Char.Property.MINIBOSS)
					|| defender.properties().contains(Char.Property.BOSS)){
				return (defender.HP/(float)defender.HT) < (KOThreshold()/5f);
			} else {
				return (defender.HP/(float)defender.HT) < KOThreshold();
			}
		}
		
		public int damageRoll( Char attacker ){
			int dmg = attacker.damageRoll();
			for( int i = 1; i < damageRolls; i++){
				int newDmg = attacker.damageRoll();
				if (newDmg > dmg) dmg = newDmg;
			}
			return Math.round(dmg * (1f + baseDmgBonus));
		}
		
		public static AttackLevel getLvl(int turnsInvis){
			List<AttackLevel> values = Arrays.asList(values());
			Collections.reverse(values);
			for ( AttackLevel lvl : values ){
				if (turnsInvis >= lvl.turnsReq){
					return lvl;
				}
			}
			return LVL_1;
		}
	}
	
	private int turnsInvis = 0;
	
	@Override
	public boolean act() {
		if (target.invisible > 0){
			turnsInvis++;
			if (AttackLevel.getLvl(turnsInvis).blinkDistance() > 0 && target == Dungeon.hero){
				ActionIndicator.setAction(this);
			}
			if (Dungeon.hero.subClass == KING && Dungeon.hero.pointsInTalent(Talent.TURNABOUT) < 4) spend(TICK);
			spend(TICK);
		} else {
			detach();
		}
		return true;
	}
	
	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	public int attackLevel(){
		return AttackLevel.getLvl(turnsInvis).ordinal()+1;
	}

	public int damageRoll( Char attacker ){
		return AttackLevel.getLvl(turnsInvis).damageRoll(attacker);
	}

	public boolean canKO( Char defender ){
		return !defender.isInvulnerable(target.getClass()) && AttackLevel.getLvl(turnsInvis).canKO(defender);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.PREPARATION;
	}
	
	@Override
	public void tintIcon(Image icon) {
		switch (AttackLevel.getLvl(turnsInvis)){
			case LVL_1:
				icon.hardlight(0f, 1f, 0f);
				break;
			case LVL_2:
				icon.hardlight(1f, 1f, 0f);
				break;
			case LVL_3:
				icon.hardlight(1f, 0.6f, 0f);
				break;
			case LVL_4:
				icon.hardlight(1f, 0f, 0f);
				break;
		}
	}

	@Override
	public float iconFadePercent() {
		AttackLevel level = AttackLevel.getLvl(turnsInvis);
		if (level == AttackLevel.LVL_4){
			return 0;
		} else {
			float turnsForCur = level.turnsReq;
			float turnsForNext = AttackLevel.values()[level.ordinal()+1].turnsReq;
			turnsForNext -= turnsForCur;
			float turnsToNext = turnsInvis - turnsForCur;
			return Math.min(1, (turnsForNext - turnsToNext)/(turnsForNext));
		}
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		String cls = ((Hero)target).subClass.title();
		String desc = Messages.get(this, "desc",cls);
		
		AttackLevel lvl = AttackLevel.getLvl(turnsInvis);

		desc += "\n\n" + Messages.get(this, "desc_dmg",
				(int)(lvl.baseDmgBonus*100),
				(int)(lvl.KOThreshold()*100),
				(int)(lvl.KOThreshold()*20));
		
		if (lvl.damageRolls > 1){
			desc += " " + Messages.get(this, "desc_dmg_likely");
		}
		
		if (lvl.blinkDistance() > 0){
			desc += "\n\n" + Messages.get(this, "desc_blink", lvl.blinkDistance());
		}
		
		desc += "\n\n" + Messages.get(this, "desc_invis_time", turnsInvis);
		
		if (lvl.ordinal() != AttackLevel.values().length-1){
			AttackLevel next = AttackLevel.values()[lvl.ordinal()+1];
			desc += "\n" + Messages.get(this, "desc_invis_next", next.turnsReq);
		}
		
		return desc;
	}
	
	private static final String TURNS = "turnsInvis";
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		turnsInvis = bundle.getInt(TURNS);
		ActionIndicator.setAction(this);
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TURNS, turnsInvis);
	}
	
	@Override
	public Image getIcon() {
		Image actionIco = Effects.get(Effects.Type.WOUND);
		tintIcon(actionIco);
		return actionIco;
	}
	
	@Override
	public void doAction() {
		GameScene.selectCell(new Attack());
	}

	private class Attack extends CellSelector.TargetedListener {

		IntIntMap blinkPos = new IntIntMap(); // enemy pos to blink pos

		private boolean canAttack(Char enemy) {
			return !(enemy == null || Dungeon.hero.isCharmedBy(enemy) || enemy instanceof NPC || !Dungeon.level.heroFOV[enemy.pos]);
		}

		@Override protected void findTargets() {
			AttackLevel lvl = AttackLevel.getLvl(turnsInvis);
			PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.not(Dungeon.level.solid, null), lvl.blinkDistance());
			super.findTargets();
		} @Override protected boolean isValidTarget(Char enemy) {
			if ( !canAttack(enemy) ){
				//GLog.w(Messages.get(Preparation.class, "no_target"));
				return false;
			}
			// check if the hero can reach them outright.
			if( Dungeon.hero.canAttack(enemy) ) {
				return true;
			}
			int dest = -1;
			for (int i : PathFinder.NEIGHBOURS8){
				int cell = enemy.pos+i;
				//cannot blink into a cell that's occupied or impassable, only over them
				if (Actor.findChar(cell) != null)     continue;
				if (!Dungeon.level.passable[cell])    continue;

				if (dest == -1 || PathFinder.distance[dest] > PathFinder.distance[cell]){
					dest = cell;
					//if two cells have the same pathfinder distance, prioritize the one with the closest true distance to the hero
				} else if (PathFinder.distance[dest] == PathFinder.distance[cell]){
					if (Dungeon.level.trueDistance(Dungeon.hero.pos, dest) > Dungeon.level.trueDistance(Dungeon.hero.pos, cell)){
						dest = cell;
					}
				}
			}
			if (dest == -1 || PathFinder.distance[dest] == Integer.MAX_VALUE || Dungeon.hero.rooted){
				//GLog.w(Messages.get(Preparation.class, "out_of_reach"));
				return false;
			}
			blinkPos.put(enemy.pos, dest);
			return true;
		}

		@Override
		protected void onInvalid(int cell) {
			// this just..guesses. it just checks the conditions until it gets a reasonable result.
			GLog.w(Messages.get(Preparation.class,
					canAttack(findChar(cell)) ? "out_of_reach" : "no_target"));
		}

		@Override
		protected void action(Char enemy) {
			int dest = blinkPos.get(enemy.pos,-1);
			if(dest != -1) {
				Dungeon.hero.pos = dest;
				Dungeon.level.occupyCell(Dungeon.hero);
				//prevents the hero from being interrupted by seeing new enemies
				Dungeon.observe();
				GameScene.updateFog();
				Dungeon.hero.checkVisibleMobs();
				
				Dungeon.hero.sprite.place( Dungeon.hero.pos );
				Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, enemy.pos);
				CellEmitter.get( Dungeon.hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				Sample.INSTANCE.play( Assets.Sounds.PUFF );
			}
			Dungeon.hero.curAction = new HeroAction.Attack( enemy );
			Dungeon.hero.next();
		}

		@Override
		public String prompt() {
			return Messages.get(Preparation.class, "prompt", AttackLevel.getLvl(turnsInvis).blinkDistance());
		}
	}
}
