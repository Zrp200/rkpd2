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
import com.zrp200.rkpd2.items.TengusMask;
import com.zrp200.rkpd2.items.armor.WarriorArmor;
import com.zrp200.rkpd2.items.bags.VelvetPouch;
import com.zrp200.rkpd2.items.wands.WandOfFirebolt;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.scenes.ChangesScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.KingSprite;
import com.zrp200.rkpd2.ui.Icons;

import java.util.ArrayList;

import static com.zrp200.rkpd2.actors.hero.HeroClass.*;
import static com.zrp200.rkpd2.messages.Messages.get;
import static com.zrp200.rkpd2.sprites.HeroSprite.avatar;
import static com.zrp200.rkpd2.ui.Icons.get;
import static com.zrp200.rkpd2.ui.Window.TITLE_COLOR;
import static java.util.Arrays.asList;

// TODO should I have a separate section for shattered changes?
public class RKPD2Changes {

    // utility
    private static ChangeButton bugFixes(String message) {
        return new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), get(ChangesScene.class, "bugfixes"), message);
    }
    private static ChangeButton misc(String message) {
        return new ChangeButton(get(Icons.PREFS), get(ChangesScene.class,"misc"), message);
    }

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        for(ChangeInfo[] section : getChanges()) changeInfos.addAll(asList(section));
    }
    private static ChangeInfo[][] getChanges() {
        return new ChangeInfo[][] {
            { // v0.0.1
                new ChangeInfo("v0.0.1", true, "") {{
                    hardlight(TITLE_COLOR);
                    addButton(new ChangeButton(get(Icons.INFO), "Developer Commentary", "This update is mostly just bugfixes and balance adjustments. More substantial changes should come when SHPD v0.9.3 is released.\n\nDo note that while things are intended to be broken, I'm aiming for a state where things are 'evenly' overpowered such that you can play any class or do any build and be like 'that's really damn good' for everything, rather than resetting (or just choosing talents!) for that same broken build every time."));
                    addButton(new ChangeButton(avatar(WARRIOR, 6), "Warrior", "This is intended to make Warrior (and Berserker) a little more balanced powerwise compared to other stuff in the game."
                            + "\n\nGeneral:" 
                            + "\n_-_ Implemented buffed runic transference." 
                            + "\n_-_ Nerfed Iron Stomach to be in line with SHPD." 
                            + "\n_-_ Removed Iron Will charge speed increase and bonus shielding." 
                            + "\n_-_ Strongman buffed to 1/3/5 from 1/2/3. It's good now I swear!" 
                            + "\n\nBerserker:"
                            + "\n_-_ Fixed a bug that made it MUCH easier to get rage while recovering than intended."
                            + "\n_-_ Berserking Stamina recovery nerfed from 1.5/1/.5 to 1.67/1.33/1. Shielding unchanged." 
                            + "\n_-_ Enraged Catalyst was bugged to be at shpd levels (17/33/50), but now that's intended." 
                            + "\n_-_ One Man Army now only gives boost if you're in combat, rather than simply relying on line of sight."
                            + "\n_-_ Berserker no longer gets rage from non-melee sources. (this was an unintentional addition)" 
                            + "\n_-_ Current HP weighting now refers to actual current HP, instead of current HP after damage is dealt."));
                    addButton(new ChangeButton(avatar(ROGUE, 6), "Rogue", "Of all the classes, Rogue has the most exploits and uneven gameplay. These changes are intended to make him seem less repetitive/unbalanced.\n" 
                            + "\n_-_ Removed Mystical Meal-Horn of Plenty exploit, it was WAY more potent than I'd thought it would be." 
                            + "\n_-_ Sucker punch now only works on the first surprise attack, numbers unchanged." 
                            + "\n_-_ Marathon runner potency halved (is now 17%/33%/50%)." 
                            + "\n_-_ Mystical Upgrade is now more potent, gives instant 8/12 recharge to non-cloak artifacts, up from 5/8." 
                            + "\n_-_ Enhanced Rings now stacks."));
                    addButton(new ChangeButton(avatar(RAT_KING, 6), "Rat King", "" 
                            + "_-_ Mercenary Commander renamed to Flash, no longer gives the same blink benefit as Assassin's Reach (was bugged lol), instead gives the standard SHPD blink scaling it was supposed to have." 
                            + "\n\n_-_ Changed Rat King's hero description in Hero Select and the descriptions of many of his t3 talents." 
                            + "\n\n_-_ Added a note about how to toggle special actions for Rat King's subclass, and added a hotkey for toggling special action for desktop users." 
                            + "\n\n_-_ Added unique dialog for Ambitious Imp."));
                    addButton(new ChangeButton(new ItemSprite(new TengusMask()), "Epic Armor", ""
                            + "_-_ Overcharging is now standardized to +100% per overcharge, down from 200% for everyone but Rat King."
                            + "\n\n_-_ Rogue Garb now has no limit on the distance it can jump."
                            + "\n\n_-_ Huntress Spectral Blades now has no distance limit."
                            + "\n\nRat King's Wrath:"
                            + "\n_-_ jump takes an extra turn."
                            + "\n_-_ Changed effects to make them more distinct (and fixed a couple omitted visual effects)"
                            + "\n_-_ Changed the priority of the invisibility to actually cover the last turn of delay."));
                    addButton(misc("_-_ Huntress spirit bow now scales to +10, down from +12." 
                            + "\n\n_-_ Empowering Scrolls talent's effect now lasts for 40 turns, up from 30." 
                            + "\n\n_-_ Shopkeepers now offer reasonable deals for both buying and selling..." 
                            + "\n\n_-_ ID VFX is now used for on-equip identification."
                            + "\n\n_-_ Adjusted VFX for Hearty Meal and Test Subject."
                            + "\n\n_-_ Added a shatter vfx effect when 'skipping' DK."
                            + "\n\n_-_ Added a sfx for fully recovering, since apparently it's needed."
                            + "\n\n_-_ Troll Blacksmith now transitions directly into the reforge window after initially talking to him."));
                    addButton(bugFixes("Fixed many bugs involving soul marking that were directly impacting the power of soul mark:"
                            + "\n_-_ Warlock's Touch can now actually provide its benefit immediately."
                            + "\n_-_ Fixed melee attacks proccing soul mark twice."
                            + "\n_-_ Fixed melee attacks coming from the hero being considered indirect damage and thus requiring Soul Siphon or Presence to work with soul mark at all."
                            + "\n_-_ Rare crashes when zapping the shopkeeper with a wand that can soul-mark."
                            + "\n"
                            + "\nOther fixes:"
                            + "\n_-_ Huntress and Rat King getting more berries at +2 from their respective talents than intended."
                            + "\n_-_ Being able to apply sniper's mark to multiple foes when it shouldn't be possible."
                            + "\n_-_ Typos"));
                }},
                new ChangeInfo("v0.0.1a", false, "") {
                    final ChangeButton[] buttons; /* formatting is a bitch */ { buttons = new ChangeButton[] {
                            new ChangeButton(avatar(HUNTRESS, 6), "Multishot and Warden", ""
                                    + "\nMultishot:"
                                    + "\n_-_ now has a unique icon."
                                    + "\n_-_ levels for targets are now evaluated independently for the purposes of shared upgrades."
                                    + "\n_-_ the total duration of sniper's mark is now the sum of the durations of each marked target."
                                    + "\n_-_ free shot is now kept for 1/2/3x duration at +1/2/3."
                                    + "\n_-_ Improved target handling between floors and when some targets cannot be shot."
                                    + "\n"
                                    + "\nWarden's power seems to currently be concentrated in Nature's Better Aid:"
                                    + "\n_-_ NBA seed boost now +17/33/50% down from +33/66/100%"
                                    + "\n_-_ NBA dew boost now +8/17/25% down from +33/66/100%"
                                    + "\n_-_ Shielding Dew now also gives upfront shielding equal to 25%/50%/75% of the heal when not at full HP."
                            ),
                            new ChangeButton(get(Icons.TALENT), "General Talents", ""
                                    + "_-_ Scholar's Intuition +2 now has a 50% chance to identify consumables when picking them up, down from 2/3"
                                    + "\n_-_ Scholar's Intuition +1 now identifies wand levels on the first zap.,"
                                    + "\n"
                                    + "\n_-_ Restored Nature now roots for 4/6 turns, up from 3/5"
                                    + "\n"
                                    + "\n_-_ Seer Shot cooldown reduced from 20 to 5. (It was bugged to be 20 instead of 10)"
                                    + "\n_-_ Natural Dominance's Seer Shot cooldown increased to 20 from 10"
                                    + "\n"
                                    + "\n_-_ Fixed an oversight that caused Soul Siphon to cause wands to trigger soul mark at 200/300/400% effectiveness instead of 20/30/40%"
                            ),
                            misc(""
                                    + "_-_ The level at which enemies stop giving exp has been increased again by 1 (now +2 compared to shattered)"
                                    + "\n_-_ The level at which enemies stop dropping stuff has been decreased by 1 to match this new value. (now same as shattered)"
                                    + "\n\n_-_ explosive arrows can now proc on miss regardless of where they are shot."
                                    + "\n\n_-_ Adrenaline and Sniper's Mark visual fades now match their initial durations"
                                    + "\n\n_-_ Dwarf King now gloats if he manages to kill you."
                            ),
                            bugFixes("_-_ natural dominance incorrectly giving ally swap instead of king's presence"
                                    + "\n_-_ Rogue not actually recieving innate 2x ring id speed."
                                    + "\n_-_ display bugs with rings, especially might and cursed rings."
                                    + "\n_-_ Multishot not checking if a given character is already marked when marking an enemy with sniper's mark"
                                    + "\n_-_ 'Hang' bug when attempting to target an out-of-range character with free shot from multi-shot"
                                    + "\n_-_ Shattered bug where dk shows his alert during the whole second phase."
                                    + "\n_-_ Gamebreaking typo in update screen.")
                    };}
                    { 
                        hardlight(TITLE_COLOR);
                        for (ChangeButton button : buttons) addButton(button);
                    }
                }
            }, 
            { // v0.0.0
                new ChangeInfo("v0.0.0",true, "This is a list of all the stuff I've changed from SHPD, for those who are curious.") {{ 
                    hardlight(TITLE_COLOR); 
                }},
                new ChangeInfo("Classes",false,"") {{
                    addButton(new ChangeButton(avatar(RAT_KING,6), "New Class!", 
                            "Added a new class! Rat King is supposed to be a sort of 'omniclass', with the perks of all the shattered classes combined.\n\n"
                                    + "He also has his subclass implemented, which, in line with the above, is of course all subclasses in one. I'm pretty proud of this one, give it a shot!"));
                    addButton(new ChangeButton(avatar(WARRIOR,6),"Warrior",""
                            + "_-_ Iron Will now increases the amount of upgrades the seal can store."
                            + "\n_-_ All t1 talents buffed."
                            + "\n\n_Berserker_:"
                            + "\n_-_ Rage gain now scales with HP as talents are upgraded"
                            + "\n_-_ Added a new talent that makes him attack faster with more enemies around"
                            + "\n\n_Gladiator_:"
                            + "\n_-_ All finishers get an extra damage roll check for increased consistency via Skill talent"));
                    addButton(new ChangeButton(avatar(MAGE,6),"Mage", "_-_ Mage now has intrinsic +2 level to all wands for purposes of power and recharge\n"
                            + "_-_ Battlemage now has a new talent that lets him spread his staff effects to all his attacks"
                            + "_-_ Battlemage gets +2 effect on his staff.\n"
                            + "_-_ Warlock can now soul mark with weapons and all damage can now trigger soul mark through new Warlock's Touch talent\n"
                            + "_-_ Most talents buffed.\n"
                            + "_-_ Empowering meal has been removed (for mage at least) and replaced with Energizing Meal, t2 meal replaced with something else."));
                    addButton(new ChangeButton(avatar(ROGUE,6),"Rogue", "_-_ Now gets an invisible +1 to weapons when cloak is active"
                            + "\n_-_ Subclasses get more invisible upgrades to various item types."
                            + "\n_-_ Subclasses have their t3s buffed."
                            + "\n_-_ Cloak recharges faster"
                            + "\n_-_ Talents buffed"
                            + "\n_-_ Protective Shadows replaced by mending shadows."));
                    addButton(new ChangeButton(avatar(HUNTRESS,6),"Huntress", "Huntress has also recieved 'tweaks'!\n\n"
                            + "_-_ Spirit Bow now scales more.\n"
                            + "_-_ The Spirit bow can be enchanted simply by using a scroll of upgrade on it.\n"
                            + "_-_ Huntress has her innate heightened vision and missile durability back!\n"
                            + "_-_ Huntress talents have been buffed across the board by ~50%.\n"
                            + "_-_ Replaced invigorating meal with adrenalizing meal. ;)\n"
                            + "_-_ Added new talents to both subclasses"));
                    addButton(new ChangeButton(new ItemSprite(new VelvetPouch()), "Bags",
                        "All heroes start with all bags. This should reduce the stress of inventory management throughout the game and also make gold more plentiful."));
            }},
                new ChangeInfo("Misc", false, "") {{
                    addButton(new ChangeButton(new WandOfFirebolt(), "Added wand of firebolt."
                            + "\n\n_-_ Does 0-9 damage with +1/+6 scaling instead of its vanilla/early shattered 1-8 with exponential scaling."
                            + "\n_-_ Is otherwise unchanged."
                            + "\n\nThis should make it more consistently overpowered instead of requiring 10+ upgrades to reach actually astounding performance. I can probably call this my greatest mistake of all time. Oh well..."));
                    addButton(new ChangeButton(get(Icons.DEPTH),"Level gen changes", 
                            "_-_ Amount of standard rooms reduced by 30%-50% in honor of precedent set by Rat King Dungeon." 
                                    + "\n_-_ Gold yield from Rat King's room increased by ~10x, general gold yield increased by 50%" 
                                    + "\n_-_ Hero takes 1/3 damage from Grim traps." 
                                    + "\n_-_ Rat King room has a sign in it if playing as rat king as a homage to RKPD" 
                                    + "\n_-_ Enemy level caps increased by 1"));
                    addButton(new ChangeButton(new KingSprite(),"Boss Changes","Certain bosses have recieved adjustments:" 
                            + "\n_-_ Dwarf King fight start altered." 
                            + "\n_-_ Dwarf King's phases can now be skipped with enough burst damage." 
                            + "\n_-_ Dwarf King and DM-300 have altered quotes for Rat King" 
                            + "\n_-_ DM-300 fight starts directly upon moving initially, instead of when you see a pylon. Better know what you are doing!"));
                    addButton(new ChangeButton(new ItemSprite(new SpiritBow().enchant(new Explosive())),"Spirit Bow enchantments",
                            "The idea here is to lower the chance of 'misfiring' when enchanting the spirit bow dramatically.\n\n"
                                + "_-_ Added a new spirit-bow exclusive enchantment.\n" 
                                + "_-_ It, along with grim, will replace blocking and lucky when using a scroll of enchantment on the bow (or a scroll of upgrade in the case of huntress).\n"));
                    addButton(new ChangeButton(new ItemSprite(new WarriorArmor()),"Class Armor","Who liked limits anyway? You can trade current HP for a full charge."));
                    addButton(new ChangeButton(get(Icons.LANGS),"Text Changes","I've changed some of the text in the game, including:"
                            + "\n_-_ Class descriptions both ingame and in the select screen"
                            + "\n_-_ Some enemy descriptions"
                            + "\n_-_ Some boss quotes"
                            + "\n_-_ This changelog"
                            + "\n\nHowever, since I only know English and do not have an active translation project, I've set the language to English. Sorry!"));
                }}
            }, 
            { // extra message at bottom.
                new ChangeInfo("",true,"Enjoy!")
            }
        };
    }
}
