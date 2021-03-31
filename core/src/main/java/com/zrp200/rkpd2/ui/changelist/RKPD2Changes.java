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

import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.items.armor.WarriorArmor;
import com.zrp200.rkpd2.items.bags.VelvetPouch;
import com.zrp200.rkpd2.items.wands.WandOfFirebolt;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.sprites.HeroSprite;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.KingSprite;
import com.zrp200.rkpd2.ui.Icons;
import com.zrp200.rkpd2.ui.Window;

import java.util.ArrayList;

public class RKPD2Changes {

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        addChanges(changeInfos);
    }

    public static void addChanges( ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.0.0",true, "This is a list of all the stuff I've changed from SHPD, for those who are curious.");
        changeInfos.add(changes);
        changes.hardlight(Window.TITLE_COLOR);

        changeInfos.add(changes = new ChangeInfo("Classes",false,""));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING,6), "New Class!",
                "Added a new class! Rat King is supposed to be a sort of 'omniclass', with the perks of all the shattered classes combined.\n\n" +
                        "He also has his subclass implemented, which, in line with the above, is of course all subclasses in one. I'm pretty proud of this one, give it a shot!"));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.WARRIOR,6),"Warrior","" +
                "_-_ Iron Will now increases the amount of upgrades the seal can store. Shield cap increased by 1 to compensate." +
                "\n_-_ All t1 talents buffed." +
                "\n\n_Berserker_:" +
                "\n_-_ Rage gain now scales with HP as talents are upgraded" +
                "\n_-_ Added a new talent that makes him attack faster with more enemies around" +
                "\n\n_Gladiator_:" +
                "\n_-_ All finishers get an extra damage roll check for increased consistency via Skill talent"));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.MAGE,6),"Mage",
                "_-_ Mage now has intrinsic +2 level to all wands for purposes of power and recharge\n" +
                        "_-_ Battlemage now has a new talent that lets him spread his staff effects to all his attacks" +
                        "_-_ Battlemage gets +2 effect on his staff.\n" +
                        "_-_ Warlock can now soul mark with weapons and all damage can now trigger soul mark through new Warlock's Touch talent\n" +
                        "_-_ Most talents buffed.\n" +
                        "_-_ Empowering meal has been removed (for mage at least) and replaced with Energizing Meal, t2 meal replaced with something else."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ROGUE,6),"Rogue",
        "_-_ Now gets an invisible +1 to weapons when cloak is active" +
                "\n_-_ Subclasses get more invisible upgrades to various item types." +
                "\n_-_ Subclasses have their t3s buffed." +
                "\n_-_ Cloak recharges 1.5x as fast" +
                "\n_-_ Talents buffed" +
                "\n_-_ Protective Shadows replaced by mending shadows."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.HUNTRESS,6),"Huntress",
            "Huntress has also recieved 'tweaks'!\n\n" +
                    "_-_ Spirit Bow now scales to +12\n" +
                    "_-_ The Spirit bow can be enchanted simply by using a scroll of upgrade on it.\n" +
                    "_-_ Huntress has her innate heightened vision and missile durability back!\n" +
                    "_-_ Huntress talents have been buffed across the board by ~50%.\n" +
                    "_-_ Replaced invigorating meal with adrenalizing meal. ;)\n" +
                    "_-_ Added new talents to both subclasses"));
        changes.addButton(new ChangeButton(new ItemSprite(new VelvetPouch()), "Bags",
                "All heroes start with all bags. This should reduce the stress of inventory management throughout the game and also make gold more plentiful."));
        changeInfos.add(changes = new ChangeInfo("Misc",false,""));
        changes.addButton(new ChangeButton(new WandOfFirebolt(), "Added wand of firebolt." +
                "\n\n_-_ Does 0-9 damage with +1/+6 scaling instead of its vanilla/early shattered 1-8 with exponential scaling." +
                "\n_-_ Is otherwise unchanged." +
                "\n\nThis should make it more consistently overpowered instead of requiring 10+ upgrades to reach actually astounding performance. I can probably call this my greatest mistake of all time. Oh well..."));
        changes.addButton(new ChangeButton(Icons.get(Icons.DEPTH),"Level gen changes",
            "_-_ Amount of standard rooms reduced by 30%-50% in honor of precedent set by Rat King Dungeon." +
            "\n_-_ Gold yield from Rat King's room increased by ~10x, general gold yield increased by 50%" +
            "\n_-_ Hero takes 1/3 damage from Grim traps." +
            "\n_-_ Rat King room has a sign in it if playing as rat king as a homage to RKPD" +
            "\n_-_ Enemy level caps increased by 1"));
        changes.addButton(new ChangeButton(new KingSprite(),"Boss Changes","Certain bosses have recieved adjustments:" +
                "\n_-_ Dwarf King fight start altered." +
                "\n_-_ Dwarf King's phases can now be skipped with enough burst damage." +
                "\n_-_ Dwarf King and DM-300 have altered quotes for Rat King" +
                "\n_-_ DM-300 fight starts directly upon moving initially, instead of when you see a pylon. Better know what you are doing!"));
        changes.addButton(new ChangeButton(new ItemSprite(new SpiritBow().enchant(new Explosive())),"Spirit Bow enchantments",
                "The idea here is to lower the chance of 'misfiring' when enchanting the spirit bow dramatically.\n\n" +
                        "_-_ Added a new spirit-bow exclusive enchantment.\n" +
                        "_-_ It, along with grim, will replace blocking and lucky when using a scroll of enchantment on the bow (or a scroll of upgrade in the case of huntress).\n"));
        changes.addButton(new ChangeButton(new ItemSprite(new WarriorArmor()),"Class Armor","Who liked limits anyway? You can trade current HP for a full charge."));
        changes.addButton(new ChangeButton(Icons.get(Icons.LANGS),"Text Changes","I've changed some of the text in the game, including:" +
                "\n_-_ Class descriptions both ingame and in the select screen" +
                "\n_-_ Some enemy descriptions" +
                "\n_-_ Some boss quotes" +
                "\n_-_ This changelog" +
                "\n\nHowever, since I only know English and do not have an active translation project, I've set the language to English. Sorry!"));
        changeInfos.add(changes = new ChangeInfo("",true,"Enjoy!"));
    }
}
