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

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.ui.QuickSlotButton;

public class SnipersMark extends FlavourBuff implements ActionIndicator.Action {

	private final IntArray objects = new IntArray(),
							levels = new IntArray(); // I fucking hate how this is done, but so be it.
	public int level = 0;

	// helper methods to make sure these are synced up.
	private boolean removeIndex(int i) { //
		try {
			objects.removeIndex(i);
			if( objects.isEmpty() ) level = levels.pop(); else levels.removeIndex(i); // remember levels should be the same size.
			return true;
		}
		catch(IndexOutOfBoundsException e) { return false; }
	}
	private boolean removeObject(int id) {
		return removeIndex(objects.indexOf(id));
	}

	private static final String OBJECT    = "object";
	private static final String OBJECTS	  = "objects";
	private static final String LEVEL     = "level",
								LEVELS    = LEVEL+"s";

	public static final float DURATION = 4f;

	{
		type = buffType.POSITIVE;
	}

	// TODO should I allow the free shot to be used at the same time as the standard shot?
	private static int maxObjects() {
		return Math.max(1,1+Dungeon.hero.pointsInTalent(Talent.MULTISHOT)-1);
	}
	// this is basically a garbage collect. This may have weird behavior when you change floors, idk.
	/*private boolean pruneObjects() {
		boolean result = false;
		for(int i=0; i < objects.size; i++) {
			if(Actor.findById(objects.get(i)) == null) {
				removeIndex(i--);
				result = true;
			}
		}
		return result;
	}*/

	public float duration() { return duration(true); }
	public float duration(boolean allowRanger) {
		if(allowRanger && levels.isEmpty()) return (DURATION+level)*((Hero)target).pointsInTalent(Talent.MULTISHOT);
		int time=0; for(int level : levels.toArray()) time += DURATION+level;
		return time;
	}

	protected void reset() { reset(true); }
	protected void reset(boolean allowRanger) {
		spend( duration(allowRanger) - cooldown() );
		if(cooldown() == 0) detach();
	}

	public void set(int object, int level){
		int index = objects.indexOf(object);
		if(index > -1) levels.set(index,level);
		else {
			if(objects.size == maxObjects() /*&& (!pruneObjects() || objects.size == maxObjects())*/) {
				removeIndex(0);
			}
			objects.add(object); levels.add(level);
		}
		ActionIndicator.setAction(this); // for rat king interaction.
		reset();
	}
	public void remove(int object) { remove(object,false); }
	public void remove(int object, boolean special) {
		if (removeObject(object) && objects.isEmpty() && !special) {
			reset();
		}
	}
	
	@Override
	public boolean attachTo(Char target) {
		ActionIndicator.setAction(this);
		return super.attachTo(target);
	}
	
	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( OBJECTS, objects.toArray() );
		bundle.put( LEVELS, levels.toArray() );
		bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if(bundle.contains(OBJECT)) objects.add(bundle.getInt(OBJECT)); else objects.addAll(bundle.getIntArray(OBJECTS));
		level = bundle.getInt( LEVEL );
		if(bundle.contains(LEVELS)) levels.addAll(bundle.getIntArray(LEVELS)); else while(levels.size < objects.size) levels.add(level);
	}

	@Override
	public int icon() {
		return BuffIndicator.MARK;
	}

	@Override
	public float iconFadePercent() {
		float duration = duration();
		return duration() == 0 ? 0 : Math.max(0, (duration - visualcooldown()) / duration);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		HeroSubClass sub = Dungeon.hero.subClass;
		String[] args = new String[4];
		args[0] = sub.title();
		args[1] = sub == HeroSubClass.SNIPER ? "she" : "he";
		args[2] = Messages.capitalize(args[1]);
		args[3] = sub == HeroSubClass.SNIPER ? "her" : "his";
		String msg = Messages.get(this, "desc", (Object[])args);
		if(objects.size > 1) msg += "\n\nThere are " + objects.size + " characters currently being targeted.";
		return msg;
	}
	
	@Override
	public Image getIcon() {
		return new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null);
	}
	
	@Override
	public void doAction() {
		
		Hero hero = Dungeon.hero;
		if (hero == null) return;
		
		SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
		if (bow == null) return;

		if (bow.knockArrow() == null) return; // this is just a test.

		if (objects.isEmpty()) {
			if(hero.hasTalent(Talent.MULTISHOT)) GameScene.selectCell(new CellSelector.Listener() {
				@Override
				public void onSelect(Integer cell) {
					if(cell == null || cell == -1) return;
					Char ch = Actor.findChar(cell);
					if(ch != null && ch != hero && canDoSniperSpecial(bow,ch)) {
						// there's no need to update the shot count if there's just 1 guy.
						hero.busy();
						doSniperSpecial(hero,bow,ch,level);
						detach();
					}
				}

				@Override
				public String prompt() {
					return Messages.get(SpiritBow.class, "prompt");
				}
			});
		} else {
			// populate list of targets.
			ObjectIntMap<Char> targets = new ObjectIntMap();
			for(int i=0; i < objects.size; i++) {
				Char ch = (Char) Actor.findById(objects.get(i));
				if(ch != null && canDoSniperSpecial(bow,ch)) {
					targets.put(ch, levels.removeIndex(i));
					objects.removeIndex(i--);
				}
			}
			bow.shotCount = targets.size;
			if(targets.size > 0) {
				hero.busy();
				for(ObjectIntMap.Entry<Char> entry : targets.entries()) doSniperSpecial(hero,bow,entry.key,entry.value);
				reset(false);
			}
			//else if(!objects.isEmpty() && pruneObjects() && objects.isEmpty()) doAction();
		}
	}

	// this should be called before doing the sniper special
	private boolean canDoSniperSpecial(SpiritBow bow, Char ch) {
		SpiritBow.SpiritArrow arrow = bow.knockArrow();
		return arrow != null && QuickSlotButton.autoAim(ch, arrow) != -1;
	}
	// actual sniper special
	protected void doSniperSpecial(Hero hero, SpiritBow bow, Char ch, int level) {
		SpiritBow.SpiritArrow arrow = bow.knockArrow(); // need a unique arrow for every character.
		arrow.sniperSpecial = true; // :D

		int cell = QuickSlotButton.autoAim(ch, arrow);

		arrow.sniperSpecialBonusDamage = level*Dungeon.hero.pointsInTalent(Talent.SHARED_UPGRADES,Talent.RK_SNIPER)/15f;

		Buff.detach(hero, Preparation.class); // nope!

		arrow.cast(hero, cell);
	}
}
