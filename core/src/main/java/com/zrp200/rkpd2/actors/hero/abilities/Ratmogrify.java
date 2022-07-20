/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.zrp200.rkpd2.actors.hero.abilities;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Adrenaline;
import com.zrp200.rkpd2.actors.buffs.AllyBuff;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.mobs.Albino;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.actors.mobs.Rat;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.armor.ClassArmor;
import com.zrp200.rkpd2.items.scrolls.ScrollOfTeleportation;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.RatSprite;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.ui.TargetHealthIndicator;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Ratmogrify extends ArmorAbility {

	{
		baseChargeUse = 50f;
	}

	//this is sort of hacky, but we need it to know when to use alternate name/icon for heroic energy
	public static boolean useRatroicEnergy = false;

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (target == null){
			return;
		}

		Char ch = Actor.findChar(target);

		if (ch == null) {
			GLog.w(Messages.get(this, "no_target"));
			return;
		} else if (ch == hero){
			if (!hero.hasTalent(Talent.RATFORCEMENTS)){
				GLog.w(Messages.get(this, "self_target"));
				return;
			} else {
				ArrayList<Integer> spawnPoints = new ArrayList<>();

				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = hero.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
						spawnPoints.add( p );
					}
				}

				int ratsToSpawn = hero.pointsInTalent(Talent.RATFORCEMENTS);

				while (ratsToSpawn > 0 && spawnPoints.size() > 0) {
					int index = Random.index( spawnPoints );

					Rat rat = Random.Int(25) == 0 ? new SummonedAlbino() : new SummonedRat();
					rat.alignment = Char.Alignment.ALLY;
					rat.state = rat.HUNTING;
					GameScene.add( rat );
					ScrollOfTeleportation.appear( rat, spawnPoints.get( index ) );

					spawnPoints.remove( index );
					ratsToSpawn--;
				}

			}
		} else if (ch.alignment != Char.Alignment.ENEMY || !(ch instanceof Mob) || ch instanceof Rat){
			GLog.w(Messages.get(this, "cant_transform"));
			return;
		} else if (ch instanceof TransmogRat){
			if (((TransmogRat) ch).allied || !hero.hasTalent(Talent.RATLOMACY)){
				GLog.w(Messages.get(this, "cant_transform"));
				return;
			} else {
				((TransmogRat) ch).makeAlly();
				ch.sprite.emitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				if (hero.pointsInTalent(Talent.RATLOMACY) > 1){
					Buff.affect(ch, Adrenaline.class, /*2*/4*(hero.pointsInTalent(Talent.RATLOMACY)-1));
				}
			}
		} else if (Char.hasProp(ch, Char.Property.MINIBOSS) || Char.hasProp(ch, Char.Property.BOSS)){
			GLog.w(Messages.get(this, "too_strong"));
			return;
		} else {
			TransmogRat rat = new TransmogRat();
			rat.setup((Mob)ch);
			rat.pos = ch.pos;

			Actor.remove( ch );
			ch.sprite.killAndErase();
			Dungeon.level.mobs.remove(ch);

			GameScene.add(rat);

			TargetHealthIndicator.instance.target(null);
			CellEmitter.get(rat.pos).burst(Speck.factory(Speck.WOOL), 4);
			Sample.INSTANCE.play(Assets.Sounds.PUFF);

			Dungeon.level.occupyCell(rat);
		}

		armor.useCharge(hero,this);
		Invisibility.dispel();
		hero.spendAndNext(Actor.TICK);

	}

	@Override
	public int icon() {
		return HeroIcon.RATMOGRIFY;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{ Talent.RATSISTANCE, Talent.RATLOMACY, Talent.RATFORCEMENTS, Talent.HEROIC_ENERGY};
	}

	@Override
	public boolean isTracked() {
		// yes I know this is incredibly general, but I know this will come up at some point.
		return Actor.containsClass(Rat.class) || Actor.containsClass(TransmogRat.class);
	}

	public static class TransmogRat extends Mob {

		{
			spriteClass = RatSprite.class;
		}

		private Mob original;
		private boolean allied;

		public void setup(Mob original) {
			this.original = original;

			HP = original.HP;
			HT = original.HT;

			defenseSkill = original.defenseSkill;

			EXP = original.EXP;
			maxLvl = original.maxLvl;

			if (original.state == original.SLEEPING) {
				state = SLEEPING;
			} else if (original.state == original.HUNTING) {
				state = HUNTING;
			} else {
				state = WANDERING;
			}

			if(!isAlive()) {
				// this is actually possible.
				die(null);
			}

		}

		public Mob getOriginal(){
			return original;
		}

		private float timeLeft = 6f;

		@Override
		protected boolean act() {
			if (timeLeft <= 0){
				original.HP = HP;
				original.pos = pos;
				original.clearTime();
				GameScene.add(original);
				if(original.HP == 0) original.die(this); // avoid shittery.

				EXP = 0;
				destroy();
				sprite.killAndErase();
				CellEmitter.get(original.pos).burst(Speck.factory(Speck.WOOL), 4);
				Sample.INSTANCE.play(Assets.Sounds.PUFF);
				return true;
			} else {
				return super.act();
			}
		}

		@Override
		protected void spend(float time) {
			if (!allied) timeLeft -= time;
			super.spend(time);
		}

		public void makeAlly() {
			allied = true;
			alignment = Alignment.ALLY;
			timeLeft = Float.POSITIVE_INFINITY;
		}

		public int attackSkill(Char target) {
			return original.attackSkill(target);
		}

		public int drRoll() {
			return original.drRoll();
		}

		private static final float RESIST_FACTOR=.85f; // .9 in shpd

		@Override
		public int damageRoll() {
			int damage = original.damageRoll();
			if (!allied && Dungeon.hero.hasTalent(Talent.RATSISTANCE)){
				damage *= Math.pow(RESIST_FACTOR, Dungeon.hero.pointsInTalent(Talent.RATSISTANCE));
			}
			return damage;
		}

		@Override
		public float attackDelay() {
			return original.attackDelay();
		}

		@Override
		public void rollToDropLoot() {
			original.pos = pos;
			original.rollToDropLoot();
		}

		@Override
		public String name() {
			return Messages.get(this, "name", original.name());
		}

		{
			immunities.add(AllyBuff.class);
		}

		private static final String ORIGINAL = "original";
		private static final String ALLIED = "allied";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(ORIGINAL, original);
			bundle.put(ALLIED, allied);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			original = (Mob) bundle.get(ORIGINAL);
			defenseSkill = original.defenseSkill;
			EXP = original.EXP;

			allied = bundle.getBoolean(ALLIED);
			if (allied) alignment = Alignment.ALLY;
		}
	}

	// summons.
	private static float getModifier() { return Math.max(1, Dungeon.scalingDepth()/5f)*.8f; }

	public interface Ratforcements { }

	public static class SummonedRat extends Rat implements Ratforcements {
		{
			HP = HT = (int)Math.ceil( HT * getModifier() );

			damageRange[0] *= getModifier();
			damageRange[1] *= getModifier();

			armorRange[0] *= getModifier();
			armorRange[1] *= getModifier();
		}

		@Override public int defenseSkill(Char enemy) {
			return Random.round( super.defenseSkill(enemy) * getModifier() );
		}

		@Override public int attackSkill(Char target) {
			return Random.round( super.attackSkill(target) * getModifier() );
		}
	}
	public static class SummonedAlbino extends Albino implements Ratforcements {
		{
			HP = HT = (int)Math.ceil( HT * getModifier() );

			damageRange[0] *= getModifier();
			damageRange[1] *= getModifier();

			armorRange[0] *= getModifier();
			armorRange[1] *= getModifier();
		}

		@Override public int defenseSkill(Char enemy) {
			return Random.round( super.defenseSkill(enemy) * getModifier() );
		}

		@Override public int attackSkill(Char target) {
			return Random.round( super.attackSkill(target) * getModifier() );
		}
	}
}
