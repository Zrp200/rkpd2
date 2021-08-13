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

import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.zrp200.rkpd2.scenes.PixelScene;
import com.zrp200.rkpd2.ui.RenderedTextBlock;
import com.zrp200.rkpd2.ui.ScrollPane;
import com.zrp200.rkpd2.ui.Window;

public class WndTitledMessage extends Window {

	protected static int maxHeight() { return (int)(PixelScene.uiCamera.height*0.9); }

	protected static final int WIDTH_MIN    = 120;
	protected static final int WIDTH_MAX    = 220;
	protected static final int GAP	= 2;

	public WndTitledMessage( Image icon, String title, String message ) {

		this(icon, title, message, WIDTH_MAX);

	}

	public WndTitledMessage( Image icon, String title, String message, int maxWidth ) {
		
		this( new IconTitle( icon, title ), message, maxWidth );

	}

	ScrollPane sp;

	public WndTitledMessage( Component titlebar, String message) { this(titlebar, message, WIDTH_MAX); }
	public WndTitledMessage( Component titlebar, String message, int maxWidth ) {

		super();

		int width = WIDTH_MIN;

		titlebar.setRect( 0, 0, width, 0 );
		add(titlebar);

		RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
		text.text( message, width );
		text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );

		while (PixelScene.landscape()
				&& text.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
				&& width < maxWidth){
			width += 20;
			titlebar.setRect(0, 0, width, 0);
			text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
			text.maxWidth(width);

			titlebar.setWidth(width);
			text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
		}
		Component comp = new Component();
		comp.add(text);
		text.setPos(0,GAP);
		comp.setSize(text.width(),text.height()+GAP*2);
		resize( width, (int)Math.min((int)comp.bottom()+2+titlebar.height()+GAP, maxHeight()) );

		add( sp = new ScrollPane(comp) );
		sp.setRect(titlebar.left(), titlebar.bottom() + GAP, comp.width(), Math.min((int)comp.bottom()+2, maxHeight()-titlebar.bottom()-GAP));
	}


	// adds to the bottom of a titled message, below the message itself.
	// this only works ONCE currently.
	public final void addToBottom(Component c) { addToBottom(c, 5); }
	public void addToBottom(Component c, float gap) {
		c.setRect(0, height+gap, width, c.height());
		setHeight( Math.min( (int) c.bottom(), maxHeight() ) );
		add(c);

		c.setPos(0, height - c.height());
		sp.setRect(0, sp.top(), width, height - (c.height()+2*gap)*1.5f);

		setHeight( (int) c.bottom() );
	}
}
