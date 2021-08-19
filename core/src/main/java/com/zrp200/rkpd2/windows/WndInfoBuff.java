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

package com.zrp200.rkpd2.windows;

import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.BuffIcon;
import com.zrp200.rkpd2.ui.RedButton;

public class WndInfoBuff extends WndTitledMessage {

	public WndInfoBuff(Buff buff){
		super(new BuffIcon(buff, true), buff.toString(), buff.desc(), WIDTH_MIN);

		if(buff instanceof ActionIndicator.Action && ((ActionIndicator.Action)buff).isSelectable()) {
			addToBottom(new RedButton("Set Active") {
				@Override protected void onClick() {
					hide();
					ActionIndicator.setAction( (ActionIndicator.Action) buff );
				}
				{
					setHeight(16);
				}
			}, GAP);
		}
	}
}
