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

import java.util.ArrayList;

import static com.zrp200.rkpd2.Assets.Interfaces.TALENT_ICONS;
import static com.zrp200.rkpd2.actors.hero.HeroClass.*;
import static com.zrp200.rkpd2.messages.Messages.get;
import static com.zrp200.rkpd2.sprites.CharSprite.NEGATIVE;
import static com.zrp200.rkpd2.sprites.CharSprite.POSITIVE;
import static com.zrp200.rkpd2.sprites.CharSprite.WARNING;
import static com.zrp200.rkpd2.sprites.HeroSprite.avatar;
import static com.zrp200.rkpd2.sprites.ItemSpriteSheet.*;
import static com.zrp200.rkpd2.ui.Icons.INFO;
import static com.zrp200.rkpd2.ui.Icons.TARGET;
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

    /** makes a list in the standard PD style.
     * [lineSpace] determines the number of spaces between each list item.
     * If you want to append extra spaces, you should do it at the end of the previous item, rather than at the start of that item.*/
    private static String list(int lineSpace, String... items) {
        StringBuilder builder = new StringBuilder("_-_ ");
        for(int i = 0; i < items.length-1; i++) {
            builder.append(items[i]);
            for(int j=0; j < lineSpace; j++) builder.append('\n');
            builder.append("_-_ ");
        }
        return builder.append(items[items.length-1]).toString();
    }

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        for(ChangeInfo[] section : new RKPD2Changes().changes) changeInfos.addAll(asList(section));
    }

    // yet another attempt at formatting changes. not sure if this is any better than the last one, but one thing is certain: I HATE the way it's done in Shattered.
    // in this case I made it so you could add buttons in the ChangeInfo constructor; this is 'lustrous' style

    final ChangeInfo[]
        v010 = {
            new ChangeInfo("v0.1.0-BETA", true, TITLE_COLOR, ""),

            new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, TITLE_COLOR, "",
                new ChangeButton(Icons.get(INFO), "Developer Commentary",
                    "I regret the extremely long turnover, but RKPD2 is now updated to Shattered v0.9.3c!"
                    + "\n\nI'm currently messing with a new method with which to buff talents and heroes: _point shifting_. Point shifting means that I make +1 effects +0 effects, +2 effects +1 effects, and so on. This results in an immediate power bump when the particular talents are obtained, though it can be slightly confusing on the first time (or with tier 2 talents)."
                    + "\n\nThere's probably going to be a v0.1.1 that will add more thematic changes that I can make building off of this update."
                    + "I'm still deciding if level-shifting is more annoying than it's worth, so if you don't like the changes, please let me know so I can get a clearer picture of what I am doing."
                    + "\n\nIn general, though, I'd love to hear any feedback about the game! _You can reach me on discord under Zrp200#0484, my email (zrp200@gmail.com), or on the Pixel Dungeon server!_"
                ),
                new ChangeButton(new ItemSprite(CROWN), "T4 Talents Implemented!", ""
                    +"Now bringing you a RKPD2 with FULL talents!!!"
                    +"\n\n"+list(2,
                        "Epic armors and T4 talents have been directly implemented.",
                        "The 12 points for t3 talents remain, causing the hero to uniquely gain two talent points per level for levels 21-24.",
                        "You can still supercharge your armor if you have too low charge, don't worry, though I doubt it'll be as notable as before with the charging mechanic changes and Heroic Energy.",
                        "Rat King has two \"special\" abilities to choose from, currently, and one more will hopefully be added in future updates."
                    )
                    +"\n\n_Ability Buffs_:"
                    +'\n'+list(1,
                        "Heroic Energy, Elemental Power, Reactive Barrier, and Nature's Wrath are now more potent.",
                        "Ratforcements spawns scaled rats that are much stronger than standard rats. It can also summon scaled loyal albinos now.",
                        "Ratlomancy now gives 50% more adrenaline for 50% more fun.",
                        "Death Mark has been buffed heavily.",
                        "Fire Everything, Growing Power, Blast Radius, Long Range Warp, and Telefrag have been level-shifted",
                        "Shadow Clone and Spectral Blades have their talents level-shifted.")
                ),
                new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "New Challenge!", "Badder bosses has been implemented into RKPD2, enjoy teaching those bosses that no matter what they do, they will still lose."),
                new ChangeButton(Icons.get(TARGET), "Special Action Targeting", "Added smart targeting for Combo, Preparation, and Sniper's Mark's free shot via Multishot."
                    +"\n\n"+list(2,
                        "When there is only one target for the action, the action will skip the prompt and simply execute.",
                        "When there is more than one possible target (or no targets), the valid targets will be highlighted.")
                    +"\n\nThis should make using these abilities much more smooth when fighting a single enemy as well as making them more intuitive in general."
                )
            ),

            new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, WARNING, "",
                new ChangeButton(HeroSprite.avatar(RAT_KING, 6), "Rat King", ""
                        +"Shattered balance changes have been directly implemented to Rat King's mechanics, for better or for worse:"
                        +"\n\n"+list(2,
                            "Noble Cause nerfed, gives less shielding when the staff runs out of charge.",
                            "Tactics buffed, its strongman now uses the v0.9.3 version and its description has been updated to indicate that cleave makes combo last for 15/30/45 turns. This talent is probably the biggest winner of this update.",
                            "Ranged Terror buffed, now gives a greater damage boost to specials when using thrown weapons.",
                            "Royal Presence changed, now has an increased chance to spawn wraiths but decreased indirect damage soul mark effectiveness and ally warp range",
                            "Natural Dominance nerfed, now gives 50/100/150% Rat King's level in barkskin that fades every turn instead of previous barkskin effect.")
                        //+"\n\nThe following balance changes have been made:"
                        //+"\n_TODO_ Strongman talent effect moved from Tactics to Imperial Wrath, uses new 0.9.3 implementation."
                        //+"\n_TODO_ Hold fast talent effect moved from Imperial Wrath to Tactics."
                        //+"\n\nTactics is already one of the safest investments you can have, it really doesn't need a strongman buff right now, and strongman fits IW better thematically."
                ),
                //new ChangeButton(new ItemSprite(new SpiritBow().enchant(new Unstable())), "Unstable Spirit Bow", "_TODO_ Fix double turn usage for upgrading spirit bow."),
                misc(list(2,
                        "Implemented virtually all misc changes up to SHPD v0.9.3c, including the addition of quick-use bags, stone behavior, etc.",
                        "New levelgen from SHPD v0.9.3 should result in shorter hallways and thus notably smaller levels.",
                        "Sorcery talent moved from 3rd slot to 6th slot for consistency with other RKPD2-exclusive talents.",
                        "Updated some talent descriptions to be clearer or otherwise add commentary.",
                        "Added unique dialogue for Rat King-Wandmaker interactions",
                        "Dwarf King has a new snide comment for you in badder bosses!",
                        "Added differing descriptions for loyal rats and hostile rats, might wanna look at their descriptions when you get the chance."
                )),
                bugFixes("Bugfixes up to SHPD v0.9.3c have been implemented.")
            ),

            new ChangeInfo(Messages.get(ChangesScene.class, "buffs"), false, POSITIVE, "",
                new ChangeButton(HeroSprite.avatar(HUNTRESS, 6), "Huntress Subclasses and T3",
                    "I'm leaning harder on giving subclasses access to talents without upgrading them, and _Warden_ is an excellent place to start:"
                    +"\n"+list(1,
                        "Tipped Darts shifted, now gives 2x/3x/4x/5x durability.",
                        "v0.9.3 Barkskin implemented, but Barkskin now degrades every two turns instead of one and gets barkskin equal to 50/100/150/200% of her level at +0/1/2/3 respectively. Thus instead of nerfing it, I have buffed it ;)")
                    +"\n\nIt's also clear that _Sniper needs buffs._"
                    +"\n"+list(1,
                        "Farsight shifted, now gives 25/50/75/100% bonus sight.",
                        "Shared Upgrades shifted (now 10/20/30/40), and the increasing duration aspect is given at +0.",
                        "New Sniper perk: +33 accuracy.")
                    //+"\n"
                    //+"\n_DEFERRED_ Multishot shifted, +3 now is 4x duration+4x target"
                    // TODO see if I can make the new targeting make this a smooth addition for future
                    //+"\n_TODO_ Multishot free shot can stack with standard marking."
                    //+"\n"
                ),
                new ChangeButton(HeroSprite.avatar(WARRIOR,6), "Warrior T2 and T3",
                    "Berserker and (to a smaller extent) Gladiator have an issue where they can kinda feel sorta weak directly after you subclass... so I've decided to nip the issue in the bud (even if it means buffing already powerful subclasses...)"
                        +"\n\n_-_ Endless Rage, Berserking Stamina, and Enraged Catalyst unique effects level-shifted."
                        +"\n_-_ Cleave buffed from 0/10/20/30 (was bugged) to 15/30/45/60"
                        +"\n\n\nFor more general buffs:"
                        +"\n"+list(1,
                            //"Hold Fast buffed from 3/6/9 to 4/8/12",
                            "Runic Transference now has a +0 effect equivalent to Shattered's +1 effect, unlocked at level 6.",
                            "Improvised projectiles nerf not implemented.")
                ),
                new ChangeButton(HeroSprite.avatar(MAGE, 6), "Battlemage", "Battlemage is currently a bit 'weaker' than warlock, and thus it's getting its unique talents (minus Sorcery) up-front for a power spike after subclassing."
                    +"\n\n_-_ Empowered Strike now 25/50/75/100 at +0/1/2/3"
                    +"\n\n_-_ Mystical Charge recharging now .5/1/1.5/2 instead of 0/.75/1.5/2.25 at +0/1/2/3 respectively."
                    +"\n\n_-_ Excess Charge proc chance is now 20/40/60/80 at +0/1/2/3, up from 0/25/50/75."
                ),
                misc(list(2,
                        "Assassin's Enhanced Lethality is buffed to be in line with SHPD",
                        "Studded gloves damage now 1-6, up from 1-5.",
                        "Empowering scrolls talent no longer is time-limited, boost increased to +2/+4/+6.",
                        "Shops sell upgraded items for cheaper."
                ))
            ),
            new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, NEGATIVE, "",
                new ChangeButton(new Image(TALENT_ICONS, 16*3,0,16,16), "Iron Will",
                    "I've decided to take the opportunity to yank Iron Will's bonus upgrade mechanics. It's really unfun to use and it kinda warps the game to a large extent."
                            +"\n\n_-_ Iron Will no longer gives bonus upgrades to the seal."
                            +"\n\n_-_ Iron Will now increases the seal's maximum shield by 1/3/5 and reduces the time it takes to fully recharge all shield by 30/60/90 turns."
                            +"\n\nThis change makes iron will recharge the seal super fast, even when not upgraded. At the very start of the game, the time to fully recharge two shield is halved!"
                ),
                new ChangeButton(new Image(TALENT_ICONS, 16*10, 0, 16,16), "Strongman", "I've implemented and (obviously) buffed Shattered's Strongman (13%/21%/29% up from 8%/13%/18%), now that Evan has finally gotten his act together and realized that I was right all along."
                        +"\n\n_+3:_\n"+list(1,
                            ":14-:17 strength -> +4 -> 18/19/20/21 down from 19/20/21/22",
                            "12-13 strength -> +3 -> 15/16 down from 17/18",
                            "10-11 strength -> +2 -> 12/13 down from 15/16",
                            "It is effectively around the same starting around 15 strength (16 if you were using a greataxe), but is increasingly worse prior to that."
                        )
                        +"\n\n_+2_:\n"+list(1,
                            "Gives a worse boost (2 down from 3) before the hero naturally attains 15 strength.",
                            "Gives a better boost once the hero has natural 20 strength"
                        )
                        +"\n\n_+1_:\n"+list(1,
                            "Gives +2 strength, up from its effective previous value of +1, once the hero naturally attains 16 strength."
                        )
                        +"\n\nOverall Strongman is a bit worse at +3 and a bit better at +2 and +1. Its ability to be exploited is down due to now being reliant on having strength, but in return it also gives true strength bonuses (thus opening up synergies with rings of force and might...)"
                ),
                new ChangeButton(HeroSprite.avatar(MAGE, 1), "Mage", "These nerfs are bit more 'nerfy' than Warrior's, considering I'm not replacing them with anything."
                    + "\n\n_-_ Backup barrier now generates 5/8 shield, down from 6/9, to reflect the Shattered nerf to Backup Barrier."
                    + "\n\n_TODO_ Energizing Meal I now gives 4/6 turns of recharging, down from 5/8.")
            )
        },
        v001 = {
            new ChangeInfo("v0.0.1", true, TITLE_COLOR, "") {{
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
                addButton(new ChangeButton(new ItemSprite(KIT), "Epic Armor", ""
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
            new ChangeInfo("v0.0.1a", false, TITLE_COLOR, "",
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
                    + "\n_-_ Gamebreaking typo in update screen."
                )
            )
        },
        v000 = {
            new ChangeInfo("v0.0.0",true, TITLE_COLOR, "This is a list of all the stuff I've changed from SHPD, for those who are curious."),
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
        foot = { // extra message at bottom.
            new ChangeInfo("",true,"Enjoy!")
        };

    // this affects what is displayed.
    final ChangeInfo[][] changes = {v010, v001, v000, foot};
}
