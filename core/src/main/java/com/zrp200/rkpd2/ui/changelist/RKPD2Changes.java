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
import com.zrp200.rkpd2.items.armor.WarriorArmor;
import com.zrp200.rkpd2.items.bags.VelvetPouch;
import com.zrp200.rkpd2.items.wands.WandOfFirebolt;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.ChangesScene;
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
        ChangeInfo changes;

        changeInfos.add(changes = new ChangeInfo("Abyssal", true, ""));
        changes.hardlight(Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary", "This mod brings Abyss endless chapter from Summoning PD. That's all.\n\n-TrashboxBobylev"));


        changeInfos.add(changes = new ChangeInfo("v0.0.1", true, ""));
        changes.hardlight(Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary", "This update is mostly just bugfixes and balance adjustments. More substantial changes should come when SHPD v0.9.3 is released.\n\nDo note that while things are intended to be broken, I'm aiming for a state where things are 'evenly' overpowered such that you can play any class or do any build and be like 'that's really damn good' for everything, rather than resetting (or just choosing talents!) for that same broken build every time."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.WARRIOR,6),"Warrior", "This is intended to make Warrior (and Berserker) a little more balanced powerwise compared to other stuff in the game." +
                "\n\nGeneral:\n_-_ Implemented buffed runic transference." +
                "\n_-_ Nerfed Iron Stomach to be in line with SHPD." +
                "\n_-_ Removed Iron Will charge speed increase and bonus shielding." +
                "\n_-_ Strongman buffed to 1/3/5 from 1/2/3. It's good now I swear!" +
                "\n\nBerserker:" +
                "\n_-_ Fixed a bug that made it MUCH easier to get rage while recovering than intended." +
                "\n_-_ Berserking Stamina recovery nerfed from 1.5/1/.5 to 1.67/1.33/1. Shielding unchanged." +
                "\n_-_ Enraged Catalyst was bugged to be at shpd levels (17/33/50), but now that's intended." +
                "\n_-_ One Man Army now only gives boost if you're in combat, rather than simply relying on line of sight." +
                "\n_-_ Berserker no longer gets rage from non-melee sources. (this was an unintentional addition)" +
                "\n_-_ Current HP weighting now refers to actual current HP, instead of current HP after damage is dealt."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ROGUE,6), "Rogue", "Of all the classes, Rogue has the most exploits and uneven gameplay. These changes are intended to make him seem less repetitive/unbalanced.\n" +
                "\n_-_ Removed Mystical Meal-Horn of Plenty exploit, it was WAY more potent than I'd thought it would be."+
                "\n_-_ Sucker punch now only works on the first surprise attack, numbers unchanged." +
                "\n_-_ Marathon runner potency halved (is now 17%/33%/50%)." +
                "\n_-_ Mystical Upgrade is now more potent, gives instant 8/12 recharge to non-cloak artifacts, up from 5/8." +
                "\n_-_ Enhanced Rings now stacks."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING,6),"Rat King",""+
                "_-_ Mercenary Commander renamed to Flash, no longer gives the same blink benefit as Assassin's Reach (was bugged lol), instead gives the standard SHPD blink scaling it was supposed to have." +
                "\n\n_-_ Changed Rat King's hero description in Hero Select and the descriptions of many of his t3 talents." +
                "\n\n_-_ Added a note about how to toggle special actions for Rat King's subclass, and added a hotkey for toggling special action for desktop users." +
                "\n\n_-_ Added unique dialog for Ambitious Imp."));
        changes.addButton(new ChangeButton(new ItemSprite(new ArmorKit()), "Epic Armor", "" +
                "_-_ Overcharging is now standardized to +100% per overcharge, down from 200% for everyone but Rat King." +
                "\n\n_-_ Rogue Garb now has no limit on the distance it can jump." +
                "\n\n_-_ Huntress Spectral Blades now has no distance limit." +
                "\n\nRat King's Wrath:" +
                "\n_-_ jump takes an extra turn." +
                "\n_-_ Changed effects to make them more distinct (and fixed a couple omitted visual effects)" +
                "\n_-_ Changed the priority of the invisibility to actually cover the last turn of delay."));
        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS),"Misc",
                "_-_ Huntress spirit bow now scales to +10, down from +12." +
                "\n\n_-_ Empowering Scrolls talent's effect now lasts for 40 turns, up from 30." +
                "\n\n_-_ Shopkeepers now offer reasonable deals for both buying and selling..." +
                "\n\n_-_ ID VFX is now used for on-equip identification." +
                "\n\n_-_ Adjusted VFX for Hearty Meal and Test Subject." +
                "\n\n_-_ Added a shatter vfx effect when 'skipping' DK." +
                "\n\n_-_ Added a sfx for fully recovering, since apparently it's needed." +
                "\n\n_-_ Troll Blacksmith now transitions directly into the reforge window after initially talking to him."));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed many bugs involving soul marking that were directly impacting the power of soul mark:" +
                "\n_-_ Warlock's Touch can now actually provide its benefit immediately." +
                "\n_-_ Fixed melee attacks proccing soul mark twice." +
                "\n_-_ Fixed melee attacks coming from the hero being considered indirect damage and thus requiring Soul Siphon or Presence to work with soul mark at all." +
                "\n_-_ Rare crashes when zapping the shopkeeper with a wand that can soul-mark." +
                "\n\nOther fixes:" +
                "\n_-_ Huntress and Rat King getting more berries at +2 from their respective talents than intended." +
                "\n_-_ Being able to apply sniper's mark to multiple foes when it shouldn't be possible." +
                "\n_-_ Typos"));

        changes = new ChangeInfo("v0.0.0",true, "This is a list of all the stuff I've changed from SHPD, for those who are curious.");
        changeInfos.add(changes);
        changes.hardlight(Window.TITLE_COLOR);

        changeInfos.add(changes = new ChangeInfo("Classes",false,""));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING,6), "New Class!",
                "Added a new class! Rat King is supposed to be a sort of 'omniclass', with the perks of all the shattered classes combined.\n\n" +
                        "He also has his subclass implemented, which, in line with the above, is of course all subclasses in one. I'm pretty proud of this one, give it a shot!"));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.WARRIOR,6),"Warrior","" +
                "_-_ Iron Will now increases the amount of upgrades the seal can store." +
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
                "\n_-_ Cloak recharges faster" +
                "\n_-_ Talents buffed" +
                "\n_-_ Protective Shadows replaced by mending shadows."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.HUNTRESS,6),"Huntress",
            "Huntress has also recieved 'tweaks'!\n\n" +
                    "_-_ Spirit Bow now scales more.\n" +
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
