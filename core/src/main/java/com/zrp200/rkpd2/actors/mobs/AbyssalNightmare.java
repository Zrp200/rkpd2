/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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

package com.zrp200.rkpd2.actors.mobs;

import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.blobs.*;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Pushing;
import com.zrp200.rkpd2.effects.particles.SmokeParticle;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.potions.PotionOfHealing;
import com.zrp200.rkpd2.items.rings.RingOfWealth;
import com.zrp200.rkpd2.items.wands.*;
import com.zrp200.rkpd2.items.weapon.enchantments.*;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.levels.traps.DisintegrationTrap;
import com.zrp200.rkpd2.levels.traps.GrimTrap;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.AbyssalSprite;
import com.zrp200.rkpd2.utils.BArray;

import java.util.ArrayList;

public class AbyssalNightmare extends Mob {

	{
		spriteClass = AbyssalSprite.class;

		HP = HT = 250;
		defenseSkill = 30;

		EXP = 40;
		maxLvl = 30;

		flying = true;
		baseSpeed = 0.5f;

		loot = new PotionOfHealing();
		lootChance = 0.1667f; //by default, see rollToDropLoot()

		properties.add(Property.INORGANIC);
		properties.add(Property.UNDEAD);
		properties.add(Property.DEMONIC);
		properties.add(Property.BOSS);
		properties.add(Property.LARGE);
	}

	private static final float SPLIT_DELAY	= 1f;
	
	int generation	= 0;
	
	private static final String GENERATION	= "generation";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GENERATION, generation );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt( GENERATION );
		if (generation > 0) EXP = 0;
	}

	@Override
	protected float attackDelay() {
		return super.attackDelay()*1.6f;
	}

	@Override
	protected boolean act() {
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		HP = Math.min(HP+6, HT);

		boolean justAlerted = alerted;
		alerted = false;

		if (justAlerted){
			sprite.showAlert();
		} else {
			sprite.hideAlert();
			sprite.hideLost();
		}

		if (paralysed > 0) {
			enemySeen = false;
			spend( TICK );
			return true;
		}

		enemy = chooseEnemy();

		boolean enemyInFOV = enemy != null && enemy.isAlive() && enemy.invisible <= 0;

		return state.act( enemyInFOV, justAlerted );
	}

	@Override
	public boolean canSee(int pos) {
		return true;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 60 );
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 16);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 60;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0){
			ArrayList<Integer> candidates = new ArrayList<>();
			boolean[] solid = Dungeon.level.solid;

			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (!solid[n] && Actor.findChar( n ) == null) {
					candidates.add( n );
				}
			}

			if (candidates.size() > 0) {

				AbyssalNightmare clone = split();
				clone.HP = HP;
				clone.pos = Random.element( candidates );
				clone.state = clone.HUNTING;

				Dungeon.level.occupyCell(clone);

				GameScene.add( clone, SPLIT_DELAY );
				Actor.addDelayed( new Pushing( clone, pos, clone.pos ), -1 );
			}
		}
		return super.attackProc(enemy, damage);
	}

	private AbyssalNightmare split() {
		AbyssalNightmare clone = new AbyssalNightmare();
		clone.EXP = EXP/2;
		if (buff(Corruption.class ) != null) {
			Buff.affect( clone, Corruption.class);
		}
		return clone;
	}

	@Override
	protected Item createLoot(){
		int rolls = 30;
		((RingOfWealth)(new RingOfWealth().upgrade(10))).buff().attachTo(this);
		ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(this, rolls);
		if (bonus != null && !bonus.isEmpty()) {
			for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
			RingOfWealth.showFlareForBonusDrop(sprite);
		}
		return null;
	}

	@Override
	protected boolean getCloser(int target) {
		if (super.getCloser(target)){
			return true;
		} else {

			if (target == pos || Dungeon.level.adjacent(pos, target)) {
				return false;
			}




			int bestpos = pos;
			for (int i : PathFinder.NEIGHBOURS8){
				PathFinder.buildDistanceMap(pos+i, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
				if (PathFinder.distance[pos+i] == Integer.MAX_VALUE){
					continue;
				}
				if (Actor.findChar(pos+i) == null &&
						Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
					bestpos = pos+i;
				}
			}
			if (bestpos != pos){

				for (int i : PathFinder.CIRCLE8){
					if ((Dungeon.level.map[pos+i] == Terrain.WALL || Dungeon.level.map[pos+i] == Terrain.WALL_DECO ||
							Dungeon.level.map[pos+i] == Terrain.DOOR || Dungeon.level.map[pos+i] == Terrain.SECRET_DOOR)){
						Level.set(pos+i, Terrain.EMPTY);
						if (Dungeon.hero.fieldOfView[pos+i]){
							CellEmitter.bottom(pos+i).burst(SmokeParticle.FACTORY, 12);
						}
						GameScene.updateMap(pos+i);
					}
				}
				Dungeon.level.cleanWalls();
				Dungeon.observe();

				bestpos = pos;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null && Dungeon.level.openSpace[pos+i] &&
							Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
						bestpos = pos+i;
					}
				}

				if (bestpos != pos) {
					move(bestpos);
				}

				return true;
			}

			return false;
		}
	}

	{
		immunities.add( Blizzard.class );
		immunities.add( ConfusionGas.class );
		immunities.add( CorrosiveGas.class );
		immunities.add( Electricity.class );
		immunities.add( Fire.class );
		immunities.add( Freezing.class );
		immunities.add( Inferno.class );
		immunities.add( ParalyticGas.class );
		immunities.add( Regrowth.class );
		immunities.add( SmokeScreen.class );
		immunities.add( StenchGas.class );
		immunities.add( StormCloud.class );
		immunities.add( ToxicGas.class );
		immunities.add( Web.class );
		immunities.add( FrostFire.class);


		immunities.add( Burning.class );
		immunities.add( Charm.class );
		immunities.add( Chill.class );
		immunities.add( Frost.class );
		immunities.add( Ooze.class );
		immunities.add( Paralysis.class );
		immunities.add( Poison.class );
		immunities.add( Corrosion.class );
		immunities.add( Weakness.class );
		immunities.add( FrostBurn.class);
		immunities.add( Shrink.class);
		immunities.add( TimedShrink.class);
		immunities.add( MagicalSleep.class);
		immunities.add( Vertigo.class);
		immunities.add( Terror.class);
		immunities.add( Vulnerable.class);
		immunities.add( Slow.class);
		immunities.add( Blindness.class);
		immunities.add( Cripple.class);
		immunities.add( Doom.class);
		immunities.add( Drowsy.class);
		immunities.add( Hex.class);
		immunities.add( Sleep.class);

		immunities.add( DisintegrationTrap.class );
		immunities.add( GrimTrap.class );

		immunities.add( WandOfBlastWave.class );
		immunities.add( WandOfDisintegration.class );
		immunities.add( WandOfFireblast.class );
		immunities.add( WandOfFrost.class );
		immunities.add( WandOfLightning.class );
		immunities.add( WandOfLivingEarth.class );
		immunities.add( WandOfMagicMissile.class );
		immunities.add( WandOfPrismaticLight.class );
		immunities.add( WandOfTransfusion.class );
		immunities.add( WandOfWarding.Ward.class );

		immunities.add( Shaman.EarthenBolt.class );
		immunities.add( Warlock.DarkBolt.class );
		immunities.add( Eye.DeathGaze.class );
		immunities.add( Yog.BurningFist.DarkBolt.class );
		immunities.add( FinalFroggit.Bolt.class);
		immunities.add( SpectreRat.DarkBolt.class);

		immunities.add(NewTengu.FireAbility.FireBlob.class);

		immunities.add(Grim.class);
		immunities.add(Kinetic.class);
		immunities.add(Blazing.class);
		immunities.add(Shocking.class);
		immunities.add(Vampiric.class);
	}
}
