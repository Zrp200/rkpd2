/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
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

package com.zrp200.rkpd2.ui.changelist;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.mobs.Spinner;
import com.zrp200.rkpd2.items.armor.WarriorArmor;
import com.zrp200.rkpd2.items.rings.RingOfEnergy;
import com.zrp200.rkpd2.items.wands.WandOfFrost;
import com.zrp200.rkpd2.items.wands.WandOfTransfusion;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.ChangesScene;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.HeroSprite;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.sprites.SpinnerSprite;
import com.zrp200.rkpd2.ui.Icons;
import com.zrp200.rkpd2.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class RKPD2Changes {

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        addChanges(changeInfos);
    }

    public static void addChanges( ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("ALPHA",true, "");
        changeInfos.add(changes);
        changes.hardlight(Window.TITLE_COLOR);

        changeInfos.add(changes = new ChangeInfo("Classes",false,""));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING,6), "New Class!",
                "Added a new class! Rat King is supposed to be a sort of 'omniclass', with the perks of all the shattered classes combined.\n\n" +
                        "He also has his subclass implemented, which, in line with the above, is of course all subclasses in one. I'm pretty proud of this one, give it a shot!"));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.WARRIOR,6),"Warrior","" +
                "_-_ Seal can now carry an infinite amount of upgrades." +
                "\n_-_ All t1 talents buffed." +
                "\n\n_Berserker_ basically cannot lose now (I may even nerf this later):" +
                "\n_-_ Rage gain now scales directly with current HP for an up to 2/3 increase in rate as HP decreases." +
                "\n_-_ Damage multiplier during berserk is now 2x." +
                "\n_-_ Rage can be gained while recovering and recovering happens slowly over time if no exp is gained." +
                "\n_-_ Berserk duration increased somewhat (this also affects Rat King subclass)" +
                "\n\n_Gladiator_:" +
                "\n_-_ Combo is no longer lost upon missing." +
                "\n_-_ All finishers get an extra damage roll check for increased consistency." +
                "\n_-_ Finishers can bypass infinite evasion."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.MAGE,6),"Mage",
                "_-_ Mage now has intrinsic +2 level to all wands for purposes of power and recharge\n" +
                        "_-_ Battlemage effects now apply to all equipped weapons, including thrown weapons. On-hit recharge remains exclusive to the staff.\n" +
                        "_-_ Warlock can now soul mark with weapons and all damage can now trigger soul mark. Wands get full effect as if the hero was doing the damage directly.\n" +
                        "_-_ Most talents buffed.\n" +
                        "_-_ Empowering meal has been removed (for mage at least) and replaced with Energizing Meal, t2 meal replaced with something else."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.HUNTRESS,0),"Huntress",
                "Huntress has also recieved 'tweaks'!\n\n" +
                        "_-_ Spirit Bow now scales to +12\n" +
                        "_-_ The Spirit bow can be enchanted simply by using a scroll of upgrade on it.\n" +
                        "_-_ Huntress has her innate heightened vision and missile durability back!\n" +
                        "_-_ Huntress talents have been buffed across the board by ~50%."));
        changeInfos.add(changes = new ChangeInfo("Misc",false,""));
        changes.addButton(new ChangeButton(new ItemSprite(new SpiritBow().enchant(new Explosive())),"Spirit Bow enchantments",
                "The idea here is to lower the chance of 'misfiring' when enchanting the spirit bow dramatically.\n\n" +
                        "_-_ Added a new spirit-bow exclusive enchantment.\n" +
                        "_-_ It, along with grim, will replace shocking, blocking, and lucky when using a scroll of enchantment on the bow (or a scroll of upgrade in the case of huntress)."));
        changes.addButton(new ChangeButton(new ItemSprite(new WarriorArmor()),"Class Armor","Who liked limits anyway?"));
        changes.addButton(new ChangeButton(Icons.get(Icons.LANGS),"Disabled Languages","Since I only know English and do not have an active translation project, I've set the language to English. Sorry!"));
        // plans
        changeInfos.add(changes = new ChangeInfo("TODO",false,""));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ROGUE,6),"Rogue",""));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.HUNTRESS,6),"Huntress Subclasses",
                "While I'm pretty happy with the changes I've made thus far to the huntress, warden can easily be tweaked to be more in line with the mood, and it would be a crime to not buff sniper while I'm at it..."));
        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS),"","" +
                "_-_ \"Balance\" tweaks to existing content as I see fit." +
                "\n_-_ Levelgen changes?" +
                "\n_-_ Visual stuff"));
        // closing message
        changeInfos.add(changes = new ChangeInfo("What changed???",true, "" +
                        "_-_ Added Warrior changes that I forgot to add to the last alpha." +
                        "\n_-_ Reworked Scholar's Intution and mage's on-eat talents." +
                        "\n_-_ Heroes start with all bags." +
                        "\n_-_ Explosive/Grim on Spirit Bow slightly less common." +
                        "\n_-_ Explosive proc rate adjusted, can now proc on miss." +
                        "\n_-_ Fixed a crash error with badges." +
                        "\n_-_ Adrenaline now actually works with the hero!"));
        changeInfos.add(changes = new ChangeInfo("",true,"Thanks for testing out my alpha! While most of the art isn't in the game, and won't be in the game until it's actually released, the gameplay mechanics are a different story entirely!\n\nAll hail the mighty rat king, destroyer of Yogs!"));
    }
}
