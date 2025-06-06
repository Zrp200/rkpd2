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

package com.zrp200.rkpd2.levels;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.Statistics;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.npcs.Blacksmith;
import com.zrp200.rkpd2.items.quest.Pickaxe;
import com.zrp200.rkpd2.levels.features.LevelTransition;
import com.zrp200.rkpd2.levels.painters.CavesPainter;
import com.zrp200.rkpd2.levels.painters.Painter;
import com.zrp200.rkpd2.levels.rooms.Room;
import com.zrp200.rkpd2.levels.traps.BurningTrap;
import com.zrp200.rkpd2.levels.traps.ConfusionTrap;
import com.zrp200.rkpd2.levels.traps.CorrosionTrap;
import com.zrp200.rkpd2.levels.traps.FrostTrap;
import com.zrp200.rkpd2.levels.traps.GatewayTrap;
import com.zrp200.rkpd2.levels.traps.GeyserTrap;
import com.zrp200.rkpd2.levels.traps.GrippingTrap;
import com.zrp200.rkpd2.levels.traps.GuardianTrap;
import com.zrp200.rkpd2.levels.traps.PitfallTrap;
import com.zrp200.rkpd2.levels.traps.PoisonDartTrap;
import com.zrp200.rkpd2.levels.traps.RockfallTrap;
import com.zrp200.rkpd2.levels.traps.StormTrap;
import com.zrp200.rkpd2.levels.traps.SummoningTrap;
import com.zrp200.rkpd2.levels.traps.WarpingTrap;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.BlacksmithSprite;
import com.zrp200.rkpd2.tiles.DungeonTileSheet;
import com.zrp200.rkpd2.tiles.DungeonTilemap;
import com.zrp200.rkpd2.utils.GLog;
import com.zrp200.rkpd2.windows.WndOptions;
import com.zrp200.rkpd2.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CavesLevel extends RegularLevel {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	public static final String[] CAVES_TRACK_LIST
			= new String[]{Assets.Music.CAVES_1, Assets.Music.CAVES_2, Assets.Music.CAVES_2,
			Assets.Music.CAVES_1, Assets.Music.CAVES_3, Assets.Music.CAVES_3};
	public static final float[] CAVES_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};

	@Override
	public void playLevelMusic() {
		if (Statistics.amuletObtained){
			Music.INSTANCE.play(Assets.Music.CAVES_TENSE, true);
		} else {
			Music.INSTANCE.playTracks(CAVES_TRACK_LIST, CAVES_TRACK_CHANCES, false);
		}
	}

	@Override
	protected ArrayList<Room> initRooms() {
		return Blacksmith.Quest.spawn(super.initRooms());
	}
	
	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 7;
		//6 to 7, average 6.333
		return 6+Random.chances(new float[]{2, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 3;
		//2 to 3, average 2.2
		return 2+Random.chances(new float[]{4, 1});
	}
	
	@Override
	protected Painter painter() {
		return new CavesPainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
				.setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.BRANCH_EXIT
				&& (!Blacksmith.Quest.given() || Blacksmith.Quest.completed() || !Blacksmith.Quest.started())) {

			Blacksmith smith = null;
			for (Char c : Actor.chars()){
				if (c instanceof Blacksmith){
					smith = (Blacksmith) c;
					break;
				}
			}

			if (smith == null || !Blacksmith.Quest.given() || Blacksmith.Quest.completed()) {
				GLog.w(Messages.get(Blacksmith.class, "entrance_blocked"));
			} else {
				final Pickaxe pick = hero.belongings.getItem(Pickaxe.class);
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						if (pick == null){
							GameScene.show( new WndTitledMessage(new BlacksmithSprite(),
									Messages.titleCase(Messages.get(Blacksmith.class, "name")),
									Messages.get(Blacksmith.class, "lost_pick"))
							);
						} else {
							GameScene.show( new WndOptions( new BlacksmithSprite(),
									Messages.titleCase(Messages.get(Blacksmith.class, "name")),
									Messages.get(Blacksmith.class, "quest_start_prompt"),
									Messages.get(Blacksmith.class, "enter_yes"),
									Messages.get(Blacksmith.class, "enter_no")){
								@Override
								protected void onSelect(int index) {
									if (index == 0){
										Blacksmith.Quest.start();
										CavesLevel.super.activateTransition(hero, transition);
									}
								}
							} );
						}

					}
				});
			}
			return false;

		} else {
			return super.activateTransition(hero, transition);
		}
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_CAVES;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_CAVES;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				BurningTrap.class, PoisonDartTrap.class, FrostTrap.class, StormTrap.class, CorrosionTrap.class,
				GrippingTrap.class, RockfallTrap.class,  GuardianTrap.class,
				ConfusionTrap.class, SummoningTrap.class, WarpingTrap.class, PitfallTrap.class, GatewayTrap.class, GeyserTrap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				4, 4, 4, 4, 4,
				2, 2, 2,
				1, 1, 1, 1, 1, 1 };
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.GRASS:
				return Messages.get(CavesLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_name");
			case Terrain.WATER:
				return Messages.get(CavesLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(CavesLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CavesLevel.class, "exit_desc");
			case Terrain.HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_desc");
			case Terrain.WALL_DECO:
				return Messages.get(CavesLevel.class, "wall_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CavesLevel.class, "bookshelf_desc");
			case Terrain.STATUE:
				return Messages.get(CavesLevel.class, "statue_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addCavesVisuals( this, visuals );
		return visuals;
	}

	public static void addCavesVisuals( Level level, Group group ) {
		addCavesVisuals(level, group, false);
	}

	public static void addCavesVisuals( Level level, Group group, boolean overHang ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Vein( i, overHang ) );
			}
		}
	}
	
	private static class Vein extends Group {
		
		private int pos;

		private boolean includeOverhang;

		private float delay;

		public Vein( int pos ) {
			this(pos, false);
		}

		public Vein( int pos, boolean includeOverhang ) {
			super();
			
			this.pos = pos;
			this.includeOverhang = includeOverhang;

			delay = Random.Float( 2 );
		}
		
		@Override
		public void update() {
			
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				
				super.update();

				if ((delay -= Game.elapsed) <= 0) {

					//pickaxe can remove the ore, should remove the sparkling too.
					if (Dungeon.level.map[pos] != Terrain.WALL_DECO){
						kill();
						return;
					}
					
					delay = Random.Float();

					PointF p = DungeonTilemap.tileToWorld( pos );
					if (includeOverhang && !DungeonTileSheet.wallStitcheable(Dungeon.level.map[pos-Dungeon.level.width()])){
						//also sparkles in the bottom 1/2 of the upper tile. Increases particle frequency by 50% accordingly.
						delay *= 0.67f;
						p.y -= DungeonTilemap.SIZE/2f;
						((Sparkle)recycle( Sparkle.class )).reset(
								p.x + Random.Float( DungeonTilemap.SIZE ),
								p.y + Random.Float( DungeonTilemap.SIZE*1.5f ) );
					} else {
						((Sparkle)recycle( Sparkle.class )).reset(
								p.x + Random.Float( DungeonTilemap.SIZE ),
								p.y + Random.Float( DungeonTilemap.SIZE ) );
					}
				}
			}
		}
	}
	
	public static final class Sparkle extends PixelParticle {
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan = 0.5f;
		}
		
		@Override
		public void update() {
			super.update();
			
			float p = left / lifespan;
			size( (am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2 );
		}
	}
}