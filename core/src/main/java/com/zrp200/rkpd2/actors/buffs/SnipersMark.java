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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
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

	private final IntArray objects = new IntArray();
	public int level = 0;

	private static final String OBJECT    = "object";
	private static final String LEVEL    = "level";

	public static final float DURATION = 4f;

	{
		type = buffType.POSITIVE;
	}

	private static int maxObjects() {
		return Math.max(1,1+Dungeon.hero.pointsInTalent(Talent.RANGER)-1);
	}
	private void pruneObjects() {
		for(int i=0; i < objects.size; i++) {
			if(Actor.findById(objects.get(i)) == null) objects.removeIndex(i--);
		}
	}

	public void set(int object, int level){
		this.level = Math.max(this.level,level);
		if(objects.size == maxObjects()) {
			pruneObjects();
			if(objects.size == maxObjects()) objects.removeIndex(0);
		}
		objects.add(object);
	}
	public void remove(int object) { remove(object,false); }
	public void remove(int object, boolean special) {
		if( !objects.removeValue(object) || !objects.isEmpty() || special) return;
		if(((Hero)target).hasTalent(Talent.RANGER)) postpone(DURATION + level);
		else detach();
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
		bundle.put( OBJECT + "S", objects.items );
		bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if(bundle.contains(OBJECT)) objects.add(bundle.getInt(OBJECT));
		else objects.addAll(bundle.getIntArray(OBJECT+"S"));
		level = bundle.getInt( LEVEL );
	}

	@Override
	public int icon() {
		return BuffIndicator.MARK;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
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
		return Messages.get(this, "desc", (Object[])args);
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
			if(hero.hasTalent(Talent.RANGER)) GameScene.selectCell(new CellSelector.Listener() {
				@Override
				public void onSelect(Integer cell) {
					if(cell == null || cell == -1) return;
					Char ch = Actor.findChar(cell);
					if(ch != null && ch != hero) {
						// there's no need to update the shot count if there's just 1 guy.
						hero.busy();
						doSniperSpecial(hero,bow,ch);
					}
				}

				@Override
				public String prompt() {
					return Messages.get(Combo.class, "prompt");
				}
			});
		} else {
			// populate list of targets.
			Array<Char> targets = new Array<>();
			for(int i=0; i < objects.size; i++) {
				Char ch = (Char) Actor.findById(objects.get(i));
				if(ch != null && canDoSniperSpecial(bow,ch)) {
					objects.removeIndex(i--);
					targets.add(ch);
				}
			}
			bow.shotCount = targets.size;
			if(targets.size > 0) hero.busy();
			for(Char ch : targets) doSniperSpecial(hero,bow,ch);
		}
	}

	private boolean canDoSniperSpecial(SpiritBow bow, Char ch) {
		SpiritBow.SpiritArrow arrow = bow.knockArrow();
		if(arrow == null) return false;
		return arrow != null && QuickSlotButton.autoAim(ch, arrow) != -1;
	}
	protected void doSniperSpecial(Hero hero, SpiritBow bow, Char ch) {
		if(!canDoSniperSpecial(bow,ch)) return;
		SpiritBow.SpiritArrow arrow = bow.knockArrow(); // need a unique arrow for every character.
		arrow.sniperSpecial = true; // :D

		int cell = QuickSlotButton.autoAim(ch, arrow);

		arrow.sniperSpecialBonusDamage = level*Dungeon.hero.pointsInTalent(Talent.SHARED_UPGRADES,Talent.RK_SNIPER)/15f;

		Buff.detach(target, Preparation.class); // nope!

		arrow.cast(hero, cell);
		detach();
	}
}
