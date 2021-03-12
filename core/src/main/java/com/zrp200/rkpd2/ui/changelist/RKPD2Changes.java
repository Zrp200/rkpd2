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

import com.watabou.noosa.Image;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.items.ArmorKit;
import com.zrp200.rkpd2.items.armor.RogueArmor;
import com.zrp200.rkpd2.items.armor.WarriorArmor;
import com.zrp200.rkpd2.items.bags.VelvetPouch;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.ChangesScene;
import com.zrp200.rkpd2.sprites.HeroSprite;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.KingSprite;
import com.zrp200.rkpd2.sprites.TenguSprite;
import com.zrp200.rkpd2.ui.Icons;
import com.zrp200.rkpd2.ui.Window;

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
                "_-_ Iron Will now increases the amount of upgrades the seal can store. Shield cap increased by 1 to compensate." +
                "\n_-_ All t1 talents buffed." +
                "\n\n_Berserker_:" +
                "\n_-_ Rage gain now scales with HP as talents are upgraded" +
                "\n_-_ Added a new talent that makes him attack faster with more enemies around" +
                "\n\n_Gladiator_:" +
                "\n_-_ All finishers get an extra damage roll check for increased consistency." +
                "\n_-_ Finishers can bypass infinite evasion."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.MAGE,6),"Mage",
                "_-_ Mage now has intrinsic +2 level to all wands for purposes of power and recharge\n" +
                        "_-_ Battlemage now has a new talent that lets him spread his staff effects to all his attacks" +
                        "_-_ Battlemage gets +2 effect on his staff.\n" +
                        "_-_ Warlock can now soul mark with weapons and all damage can now trigger soul mark through new Warlock's Touch talent\n" +
                        "_-_ Most talents buffed.\n" +
                        "_-_ Empowering meal has been removed (for mage at least) and replaced with Energizing Meal, t2 meal replaced with something else."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ROGUE,6),"Rogue",
        "_-_ Now gets an invisible +1 to weapons and rings" +
                "\n_-_ Subclasses get more invisible upgrades to various item types." +
                "\n_-_ Cloak levels up twice as fast." +
                "\n_-_ Most tier 1 Talents buffed." +
                "\n_-_ Protective Shadows replaced by mending shadows, original sprite needs to be transferred over, however."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.HUNTRESS,6),"Huntress",
            "Huntress has also recieved 'tweaks'!\n\n" +
                    "_-_ Spirit Bow now scales to +12\n" +
                    "_-_ The Spirit bow can be enchanted simply by using a scroll of upgrade on it.\n" +
                    "_-_ Huntress has her innate heightened vision and missile durability back!\n" +
                    "_-_ Huntress talents have been buffed across the board by ~50%.\n" +
                    "_-_ Added new talents to both subclasses"));
        changes.addButton(new ChangeButton(new ItemSprite(new VelvetPouch()), "Bags",
                "All heroes start with all bags. This should reduce the stress of inventory management throughout the game and also make gold more plentiful."));
        changeInfos.add(changes = new ChangeInfo("Misc",false,""));
        changes.addButton(new ChangeButton(Icons.get(Icons.DEPTH),"Level gen changes",
            "_-_ Amount of standard rooms reduced by 30%-50% in honor of precedent set by Rat King Dungeon." +
            "\n_-_ Gold yield from Rat King's room increased by 20x" +
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
                        "_-_ It, along with grim, will replace blocking, and lucky when using a scroll of enchantment on the bow (or a scroll of upgrade in the case of huntress).\n" +
                        "_-_ Shocking is now rarer."));
        changes.addButton(new ChangeButton(new ItemSprite(new WarriorArmor()),"Class Armor","Who liked limits anyway? You can trade current HP for a full charge."));
        changes.addButton(new ChangeButton(Icons.get(Icons.LANGS),"Text Changes","I've changed some of the text in the game, including:" +
                "\n_-_ Class descriptions both ingame and in the select screen" +
                "\n_-_ Some enemy descriptions" +
                "\n_-_ Some boss quotes" +
                "\n_-_ This changelog" +
                "\n\nHowever, since I only know English and do not have an active translation project, I've set the language to English. Sorry!"));
        // plans
        changeInfos.add(changes = new ChangeInfo("TODO",false,""));
        changes.addButton(new ChangeButton(Icons.get(Icons.TALENT),"Talents",
                "Freerunner/Assassin/Gladiator still don't have a 6th talent, and huntress only has 1 level in hers. Also still need to buff other talents."));
        changes.addButton(new ChangeButton(new TenguSprite(), "Boss Phases",
            "I think it would be interesting if players could \"accelerate\" boss fights if they damage them enough. It's a complaint I've had with Shattered, and a mod like this is the perfect place to do something about it." +
            "\n\nThis should also contribute to making the mod run quicker during the boss fights, making it overall easier to speedrun and race." +
            "\n\nI've already done this for Dwarf King, but Tengu and DM-300 could easily be made to do this to some degree too. The question is how much I want to let the player fuck with them, as they aren't nearly as intrinsically resistant to damage as DK is."));
        changes.addButton(new ChangeButton(Icons.get(Icons.DISPLAY),"Visual changes",
                "_-_ Altered Warrior seal art for Warrior only" +
                "\n_-_ Sniper memes" +
                "\n_-_ Use of RKPD assets for amulet/amulet scene." +
                "\n\nThese may or may not happen:" +
                "\n_-_ Altered Mending Shadows icon as a throwback to original." +
                "\n_-_ Custom talent icons" +
                "\n_-_ Use of RKPD assets for hero sprites"));
        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS),"","" +
                "_-_ \"Balance\" tweaks to existing content as I see fit." +
                "\n_-_ Quest changes to make them run faster" +
                "\n_-_ Text adjustments to lore"));
        // closing message
        changeInfos.add(changes = new ChangeInfo("What changed???",true,"" +
                "_-_ Implemented v0.9.2 + t3 talents\n" +
                "_-_ Redid most subclasses.\n" +
                "_-_ Nerfed explosive again.\n" +
                "_-_ Spirit bow scroll of enchant can now roll shocking again, grim freq reduced\n" +
                "_-_ Level caps for enemies incrased by 1."+
                "_-_ Changed sign design for sewers to match its actual usage (and increased app size)"));
        changes.addButton(new ChangeButton(Icons.get(Icons.TALENT),"T3 talents implemented!",
                        "_-_ Reworked most subclasses in order to get t3 talents to work:" +
                        "\n_-_ Removed rogue's ability to use cloak unequipped, but buffed t3 talent heavily." +
                        "\n_-_ Berserker reverted to shattered functinonality, but talents restore most of the benefits from before." +
                        "\n_-_ Warlock reverted as well, but new Warlock's Touch talent restores functionality, as does soul siphon." +
                        "\n_-_ Battlemage now needs Sorcery talent to get previous benefits, but now it applies to most of his t3 talents and to wands." +
                        "\n_-_ Added unique t3 talents for Warden, Berserker, and Sniper." +
                        "\n\nMade unique changes to talents as well!" +
                        "\n_-_ t3 talent points increased to 12" +
                        "\n_-_ t3 talent points are given until level 24"));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING,6),"Rat King overhaul!",
                "In response to 0.9.2, I have adjusted rat king significantly!" +
                        "\n_-_ Added _8_ t3 talents to rat king." +
                        "\n_-_ Implemented balance changes to rat king's talents." +
                        "\n_-_ Added a vfx for royal meal to signify haste." +
                        "\n_-_ Implemented subclass changes for rat king." +
                        "\n_-_ Subclass actions can now be manually set via their buff descriptions."));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),"" +
                "_-_ RKPD2 getting bricked after you subclass rat king" +
                "_-_ Crashes in alchemy with scholar's intuition" +
                "_-_ DM-300 not spawning on f15 because of evan's insistence on using signs for the gate."));
        changeInfos.add(changes = new ChangeInfo("",true,"Thanks for testing out my alpha! While most of the art isn't in the game, and won't be in the game until it's actually released, the gameplay mechanics are a different story entirely!\n\nAll hail the mighty rat king, destroyer of Yogs!\n\nNext alpha should have v0.9.2 implemented, which means it may look almost entirely different."));
    }
}
