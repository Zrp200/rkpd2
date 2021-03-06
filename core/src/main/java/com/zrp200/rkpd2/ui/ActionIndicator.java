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

package com.zrp200.rkpd2.ui;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.SPDAction;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Combo;
import com.zrp200.rkpd2.actors.buffs.Momentum;
import com.zrp200.rkpd2.actors.buffs.Preparation;
import com.zrp200.rkpd2.actors.buffs.SnipersMark;
import com.zrp200.rkpd2.scenes.PixelScene;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;

public class ActionIndicator extends Tag {

	Image icon;

	public static Action action;
	public static ActionIndicator instance;

	public ActionIndicator() {
		super( 0xFFFF4C );

		instance = this;

		setSize( 24, 24 );
		visible = false;
	}
	
	@Override
	public GameAction keyAction() {
		return SPDAction.TAG_ACTION;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		instance = null;
	}
	
	@Override
	protected synchronized void layout() {
		super.layout();
		
		if (icon != null){
			icon.x = x + (width - icon.width()) / 2;
			icon.y = y + (height - icon.height()) / 2;
			PixelScene.align(icon);
			if (!members.contains(icon))
				add(icon);
		}
	}
	
	private boolean needsLayout = false;
	
	@Override
	public synchronized void update() {
		super.update();

		if (!Dungeon.hero.ready){
			if (icon != null) icon.alpha(0.5f);
		} else {
			if (icon != null) icon.alpha(1f);
		}

		if (!visible && action != null){
			visible = true;
			updateIcon();
			flash();
		} else {
			visible = action != null;
		}
		
		if (needsLayout){
			layout();
			needsLayout = false;
		}
	}

	@Override
	protected void onClick() {
		if (action != null && Dungeon.hero.ready)
			action.doAction();
	}

	public static void setAction(Action action){
		if(!action.usable()) return;
		ActionIndicator.action = action;
		updateIcon();
	}

	// list of action buffs that we should replace it with.
	private static final Class<?extends Buff>[] actionBuffClasses = new Class[]{Preparation.class, SnipersMark.class, Combo.class, Momentum.class};
	public static void clearAction(Action action){
		if (ActionIndicator.action != action) return;
		ActionIndicator.action = null;
		for(Class<?extends Buff> actionBuffClass : actionBuffClasses) {
			Action a = (Action)Dungeon.hero.buff(actionBuffClass);
			if(a != null && a != action && action.usable()) {
				if(actionBuffClass == Combo.class) {
					if(((Combo)a).getHighestMove() == null) continue;
				}
				setAction(a);
				return;
			}
		}
	}

	public static void updateIcon(){
		if (instance != null){
			synchronized (instance) {
				if (instance.icon != null) {
					instance.icon.killAndErase();
					instance.icon = null;
				}
				if (action != null) {
					instance.icon = action.getIcon();
					instance.needsLayout = true;
				}
			}
		}
	}

	public interface Action{

		public Image getIcon();

		public void doAction();

		default boolean usable() { return true; }

	}

}
