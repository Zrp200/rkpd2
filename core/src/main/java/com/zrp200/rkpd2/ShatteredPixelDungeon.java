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

package com.zrp200.rkpd2;

import com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.scenes.PixelScene;
import com.zrp200.rkpd2.scenes.TitleScene;
import com.zrp200.rkpd2.scenes.WelcomeScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;

public class ShatteredPixelDungeon extends Game {

	//variable constants for specific older versions of shattered, used for data conversion
	public static final int
			V0_4_0=642,
			v0_3_0=616,
			v0_2_0=597,
			v0_1_0=557,
			v0_0_1=551,
			v0_0_0=550;

	// shattered versions
	//versions older than v0.9.3c are no longer supported, and data from them is ignored
	public static final int v0_9_3c = 557; //557 on iOS, 554 on other platforms

	public static final int v1_0_3  = 574;
	public static final int v1_1_2  = 588;
	public static final int v1_2_3  = 628;
	public static final int v1_3_0  = 642;
	public ShatteredPixelDungeon( PlatformSupport platform ) {
		super( sceneClass == null ? WelcomeScene.class : sceneClass, platform );

		//pre-v1.3.0
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.actors.buffs.Bleeding.class,
				"com.zrp200.rkpd2.levels.features.Chasm$FallBleed" );
		// REJECTED :D
//		com.watabou.utils.Bundle.addAlias(
//				com.zrp200.rkpd2.plants.Mageroyal.class,
//				"com.zrp200.rkpd2.plants.Dreamfoil" );
//		com.watabou.utils.Bundle.addAlias(
//				com.zrp200.rkpd2.plants.Mageroyal.Seed.class,
//				"com.zrp200.rkpd2.plants.Dreamfoil$Seed" );

		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.weapon.curses.Dazzling.class,
				"com.zrp200.rkpd2.items.weapon.curses.Exhausting" );
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.weapon.curses.Explosive.class,
				"com.zrp200.rkpd2.items.weapon.curses.Fragile" );

		//pre-v1.2.0
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.weapon.missiles.darts./*CleansingDart*/DreamDart.class,
				"com.zrp200.rkpd2.items.weapon.missiles.darts.SleepDart" );

		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.levels.rooms.special.CrystalVaultRoom.class,
				"com.zrp200.rkpd2.levels.rooms.special.VaultRoom" );

		// no idea if this is needed.
		com.watabou.utils.Bundle.addAlias(com.zrp200.rkpd2.actors.buffs.SoulMark.class,
				"com.zrp200.rkpd2.actors.buffs.SoulMark.DelayedMark");

		//pre-v1.1.0
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfDread.class,
				"com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfPetrification" );
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfSirensSong.class,
				"com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfAffection" );
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfChallenge.class,
				"com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfConfusion" );
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.potions.exotic.PotionOfDivineInspiration.class,
				"com.zrp200.rkpd2.items.potions.exotic.PotionOfHolyFuror" );
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.potions.exotic.PotionOfMastery.class,
				"com.zrp200.rkpd2.items.potions.exotic.PotionOfAdrenalineSurge" );
		com.watabou.utils.Bundle.addAlias(
				ScrollOfMetamorphosis.class,
				"com.zrp200.rkpd2.items.scrolls.exotic.ScrollOfPolymorph" );

		//pre-v1.0.0
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.stones.StoneOfFear.class,
				"com.zrp200.rkpd2.items.stones.StoneOfAffection" );
		com.watabou.utils.Bundle.addAlias(
				com.zrp200.rkpd2.items.stones.StoneOfDeepSleep.class,
				"com.zrp200.rkpd2.items.stones.StoneOfDeepenedSleep" );
		
	}
	
	@Override
	public void create() {
		super.create();

		updateSystemUI();
		SPDAction.loadBindings();
		
		Music.INSTANCE.enable( SPDSettings.music() );
		Music.INSTANCE.volume( SPDSettings.musicVol()*SPDSettings.musicVol()/100f );
		Sample.INSTANCE.enable( SPDSettings.soundFx() );
		Sample.INSTANCE.volume( SPDSettings.SFXVol()*SPDSettings.SFXVol()/100f );

		Sample.INSTANCE.load( Assets.Sounds.all );
		
	}

	@Override
	public void finish() {
		if (!DeviceCompat.isiOS()) {
			super.finish();
		} else {
			//can't exit on iOS (Apple guidelines), so just go to title screen
			switchScene(TitleScene.class);
		}
	}

	public static void switchNoFade(Class<? extends PixelScene> c){
		switchNoFade(c, null);
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}
	
	public static void seamlessResetScene(SceneChangeCallback callback) {
		if (scene() instanceof PixelScene){
			((PixelScene) scene()).saveWindows();
			switchNoFade((Class<? extends PixelScene>) sceneClass, callback );
		} else {
			resetScene();
		}
	}
	
	public static void seamlessResetScene(){
		seamlessResetScene(null);
	}
	
	@Override
	protected void switchScene() {
		super.switchScene();
		if (scene instanceof PixelScene){
			((PixelScene) scene).restoreWindows();
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		if (width == 0 || height == 0){
			return;
		}

		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			PixelScene.noFade = true;
			((PixelScene) scene).saveWindows();
		}

		super.resize( width, height );

		updateDisplaySize();

	}
	
	@Override
	public void destroy(){
		super.destroy();
		GameScene.endActorThread();
	}
	
	public void updateDisplaySize(){
		platform.updateDisplaySize();
	}

	public static void updateSystemUI() {
		platform.updateSystemUI();
	}
}