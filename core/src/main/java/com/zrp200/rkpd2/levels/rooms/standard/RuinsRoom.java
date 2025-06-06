/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.zrp200.rkpd2.levels.rooms.standard;

import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.levels.painters.Painter;
import com.zrp200.rkpd2.levels.rooms.Room;
import com.watabou.utils.Point;

public class RuinsRoom extends PatchRoom {
	
	@Override
	public float[] sizeCatProbs() {
		return new float[]{4, 2, 1};
	}

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		return true;
	}

	@Override
	protected float fill() {
		//fill scales from ~20% at 4x4, to ~50% at 18x18
		// normal   ~20% to ~30%
		// large    ~30% to ~40%
		// giant    ~40% to ~50%
		int scale = Math.min(width()*height(), 18*18);
		return 0.20f + scale/1024f;
	}

	@Override
	protected int clustering() {
		return 0;
	}

	@Override
	protected boolean ensurePath() {
		return connected.size() > 0;
	}

	@Override
	protected boolean cleanEdges() {
		return true;
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1 , Terrain.EMPTY );
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}
		
		setupPatch(level);
		fillPatch(level, Terrain.WALL);
	}
}
