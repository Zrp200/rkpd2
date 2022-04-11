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

package com.zrp200.rkpd2.windows;

import com.zrp200.rkpd2.items.Heap;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.scenes.PixelScene;
import com.zrp200.rkpd2.ui.ItemSlot;
import com.zrp200.rkpd2.ui.RenderedTextBlock;
import com.zrp200.rkpd2.ui.Window;

public class WndInfoItem extends WndTitledMessage {

	//only one WndInfoItem can appear at a time
	private static WndInfoItem INSTANCE;

	{
		if(INSTANCE != null) INSTANCE.hide();
		INSTANCE = this;
	}

	public WndInfoItem( Heap heap ) {
		super(getTitlebar(heap), heap.type == Heap.Type.HEAP ? heap.peek().info() : heap.info());
	}

	public WndInfoItem( Item item ) {
		super(getTitlebar(item), item.info());
	}

	@Override
	public void hide() {
		super.hide();
		if (INSTANCE == this){
			INSTANCE = null;
		}
	}

	private static IconTitle getTitlebar(Heap heap ) {
		return heap.type == Heap.Type.HEAP
				? getTitlebar( heap.peek() )
				: new IconTitle( heap, TITLE_COLOR );
	}
	private static IconTitle getTitlebar( Item item ) {
		
		int color = TITLE_COLOR;
		if (item.levelKnown && item.level() > 0) {
			color = ItemSlot.UPGRADED;
		} else if (item.levelKnown && item.level() < 0) {
			color = ItemSlot.DEGRADED;
		}

		return new IconTitle( item, color );
	}

	// this is unneeded.
	/*private void layoutFields(IconTitle title, RenderedTextBlock info){
		int width = WIDTH_MIN;

		info.maxWidth(width);

		//window can go out of the screen on landscape, so widen it as appropriate
		while (PixelScene.landscape()
				&& info.height() > 100
				&& width < WIDTH_MAX){
			width += 20;
			info.maxWidth(width);
		}

		title.setRect( 0, 0, width, 0 );
		add( title );

		info.setPos(title.left(), title.bottom() + GAP);
		add( info );

		resize( width, (int)(info.bottom() + 2) );
	}*/
}
