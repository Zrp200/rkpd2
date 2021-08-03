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

import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.SelectableCell;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.ui.QuickSlotButton;
import com.zrp200.rkpd2.utils.GLog;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static com.zrp200.rkpd2.Dungeon.hero;
import static java.util.Collections.unmodifiableSet;

public class SnipersMark extends FlavourBuff implements ActionIndicator.Action {

	private int object;
	public int level = 0;

	public static void remove(Char ch) {
		SnipersMark mark = new State().get( ch.id() );
		if(mark == null) return;
		mark.detach();
		FreeTarget.apply(mark.level);
	}

	// retrieves all the info needed to work with all the buffs at the same time.
	// needs to be updated every time relevant info is changed.
	// also this is probably wasteful.
	private static class State {
		final Set<SnipersMark> marks = unmodifiableSet( hero.buffs(SnipersMark.class, true) );
		final Set<FreeTarget> frees = unmodifiableSet(  hero.buffs(FreeTarget.class) );
		final boolean freeActive = !frees.isEmpty();

		final int size = marks.size() + frees.size();
		final int adjustedSize = freeActive ? size - 1 : size;

		void removeOldest() {
			FlavourBuff min = null;
			for(FlavourBuff buff : frees) {
				if(min == null || buff.cooldown() < min.cooldown()) min = buff;
			}
			for(FlavourBuff buff : marks) {
				if(min == null || buff.cooldown() < min.cooldown()) min = buff;
			}
			//noinspection ConstantConditions
			min.detach();
		}

		SnipersMark get(int object) {
			for(SnipersMark mark : marks) {
				if(mark.object == object) return mark;
			}
			return null;
		}
	}

	// TODO should I just sync all buffs together?!

	public static void add(Char ch, int level) {
		addTime(level);
		if( !ch.isAlive() ) FreeTarget.apply(level);

		State state = new State();

		SnipersMark existing = state.get( ch.id() );
		if(existing != null) {
			existing.level = Math.max(existing.level, level);
			ActionIndicator.setAction(existing);
			return;
		}

		if (state.adjustedSize >= maxObjects()) {
			state.removeOldest();
		}

		SnipersMark mark = Buff.append( hero, SnipersMark.class, duration(level) );
		mark.object = ch.id();
		mark.level = level;
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
		return Math.max(1,1+hero.pointsInTalent(Talent.MULTISHOT)-1);
	}

	public float duration() { return duration(level); }
	// todo implement extended time again.
	public static float duration(int level) {
		return (DURATION + level) * Math.max(hero.pointsInTalent(Talent.MULTISHOT), 1);
	}
	// FIXME this is really fucked up.
	protected static void addTime(int level) {
		float time = DURATION+level;
		for(SnipersMark buff : hero.buffs(SnipersMark.class)) buff.postpone( Math.min(
				duration(buff.level),
				buff.cooldown() + time
		));
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
		bundle.put( OBJECT, object );
		bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );

		level = bundle.getInt(LEVEL);

		if(this instanceof FreeTarget) return;

		if( bundle.contains(OBJECT) ) {
			object = bundle.getInt(OBJECT);
		}
		else if(bundle.contains(OBJECTS)) {
			// split between several buffs.
			int[] objects = bundle.getIntArray(OBJECTS);
			if(objects.length == 0) {
				// restore as freetarget
				Buff.affect(hero, FreeTarget.class, cooldown()).level = level;
				detach();
				return;
			}

			int[] levels;
			if( bundle.contains(LEVELS) ) {
				levels = bundle.getIntArray(LEVELS);
				level = levels[0];
			}
			else Arrays.fill(levels = new int[objects.length], level);

			object = objects[0];

			for(int i=1; i < objects.length; i++) {
				SnipersMark mark = Buff.append(hero, SnipersMark.class, cooldown());
				mark.object = objects[i];
				mark.level = levels[i];
			}
		}
		else {
			// idk where the object went?!
			// this probably never runs but just in case.
			Buff.affect(hero, FreeTarget.class, cooldown()).level = level;
		}
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
		return Messages.get(this, "desc", (Object[])args);
	}
	
	@Override
	public Image getIcon() {
		return new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null);
	}

	private static ActionHandler actionHandler;
	private static class ActionHandler {
		static void call() {
			if(actionHandler != null) {
				actionHandler.doAction();
			}
			actionHandler = new ActionHandler();
			if(actionHandler.bow != null && actionHandler.bow.knockArrow() != null) actionHandler.next();
			else actionHandler = null;
		}

		SnipersMark running = null;

		final HashMap<SnipersMark, Char> actionMap = new HashMap();
		final SpiritBow bow = hero.belongings.getItem(SpiritBow.class);

		/* this will be needed if I try to let multishot pierce marked targets. {
			for(SnipersMark mark : hero.buffs(SnipersMark.class, true)) {
				Char ch = (Char)Actor.findById(mark.object);
				if(isValidTarget(ch)) {
					actionMap.put(mark, ch);
				}
			}
		}*/

		HashSet<SelectableCell> selected = new HashSet();
		void select(SnipersMark m, Char ch) {
			SelectableCell c = new SelectableCell(ch.sprite);
			c.hardlight(1,0,0);
			selected.add(c);

			actionMap.put(m,ch);
		}
		void destroy() {
			actionHandler = null;
			for(SelectableCell c : selected) c.killAndErase();
			GameScene.ready();
		}

		final LinkedList<SnipersMark> queue = new LinkedList( hero.buffs(FreeTarget.class) );
		{
			Collections.sort(queue, (a, b) -> Float.compare( a.cooldown(), b.cooldown() ) );

			// now we add the standard buffs for processing, first.
			for( SnipersMark mark : hero.buffs(SnipersMark.class, true) ) queue.push(mark);
		}

		void doAction() {
			destroy();
			if ( actionMap.isEmpty() ) return;
			bow.shotCount = actionMap.size();
			hero.busy();
			for (Map.Entry<SnipersMark, Char> mapping : actionMap.entrySet()) {
				SnipersMark mark = mapping.getKey(); Char ch = mapping.getValue();
				mark.doSniperSpecial( hero, bow, ch );
			}
		}

		void next() {
			running = queue.poll();
			if(running == null) doAction();
			else {
				GameScene.clearCellSelector(true);
				running.doAction();
			}
		}

		boolean isTargeting(Char ch) {
			return actionMap.containsValue(ch);
		}
		boolean isValidTarget(Char ch) {
			return !isTargeting(ch) && canDoSniperSpecial(bow, ch);
		}
	}

	@Override
	public void doAction() {
		if (hero == null) return;
		if(actionHandler == null) {
			ActionHandler.call();
		}
		// this isn't perfect, but if it does end up happening that the caller wasn't called first, not much bad will happen
		// more like some weird confusion will happen, which is still a bug but not THAT bad.
		else if(actionHandler.running != this) { // in other words it's been started again.
			actionHandler.doAction();
		} else {
			queueAction();
		}
	}

	protected void queueAction() {
		Char ch = (Char)Actor.findById(object);
		if(actionHandler.isValidTarget(ch)) {
			actionHandler.select(this,ch);
		} //else actionHandler.actionMap.remove(this);
		actionHandler.next();
	}

	@Override public boolean isSelectable() {
		return ActionIndicator.Action.super.isSelectable()
				// kinda weird to be able to select identical actions.
				&& !(ActionIndicator.action instanceof SnipersMark);
	}

	public static class FreeTarget extends SnipersMark {

		private CellSelector.TargetedListener listener;

		@Override
		public boolean usable() {
			// it will never override actual snipers marks, because they play nicer with ActionHandler
			return !(ActionIndicator.action instanceof SnipersMark);
		}

		public static void apply(int level) {
			if(!hero.hasTalent(Talent.MULTISHOT)) return;

			State state = new State();
			if(state.adjustedSize > maxObjects() + 1) state.removeOldest();

			FreeTarget freeTarget = Buff.append( hero, FreeTarget.class, duration(level) );
			freeTarget.level = level;
		}

		// only difference is we don't care about object at all. it just exists.

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(.5f,.5f,.5f); // shrug
		}

		@Override
		protected void queueAction() {
			GameScene.selectCell(new CellSelector.TargetedListener() {
				{
					conflictTolerance = actionHandler.queue.size();
					promptIfNoTargets = false;
					readyOnSelect = false; // manually disable the mechanic that prevents stacking.
				}

				@Override protected boolean isValidTarget(Char ch) { return actionHandler.isValidTarget(ch); }

				@Override protected void action(Char ch) {
					actionHandler.select(FreeTarget.this, ch);
					actionHandler.next();
				}

				@Override protected void onCancel() {
					if(actionHandler != null) actionHandler.destroy();
				}

				@Override protected void onInvalid(int cell) {
					Char ch = Actor.findChar(cell);
					if(ch != null) {
						String message = actionHandler.isTargeting(ch)
								? "That character is already being targeted!"
								: "That character cannot be targeted.";
						GLog.w(message);
						GameScene.clearCellSelector(true);
						queueAction();
					} else {
						actionHandler.destroy();
					}
				}

				@Override protected boolean canIgnore(Char ch) {
					// todo do I want to just set this to blanket true?
					return super.canIgnore(ch) || actionHandler.isTargeting(ch);
				}

				@Override public String prompt() {
					return Messages.get(SpiritBow.class, "prompt");
				}
			});
		}

	}

	// this should be called before doing the sniper special
	private static boolean canDoSniperSpecial(SpiritBow bow, Char ch) {
		SpiritBow.SpiritArrow arrow = bow.knockArrow();
		return ch != null && arrow != null && QuickSlotButton.autoAim(ch, arrow) != -1;
	}
	// actual sniper special
	protected void doSniperSpecial(Hero hero, SpiritBow bow, Char ch) {
		SpiritBow.SpiritArrow arrow = bow.knockArrow(); // need a unique arrow for every character.
		arrow.sniperSpecial = true; // :D

		int cell = QuickSlotButton.autoAim(ch, arrow);

		int points = hero.pointsInTalent(Talent.SHARED_UPGRADES, Talent.RK_SNIPER);
		if(hero.canHaveTalent(Talent.SHARED_UPGRADES)) points++; // free +1.
		arrow.sniperSpecialBonusDamage = level*points/10f;

		Buff.detach(hero, Preparation.class); // nope!

		arrow.cast(hero, cell);

		detach();
	}
}