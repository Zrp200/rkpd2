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
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.items.KingsCrown;
import com.zrp200.rkpd2.items.armor.WarriorArmor;
import com.zrp200.rkpd2.items.bags.VelvetPouch;
import com.zrp200.rkpd2.items.wands.WandOfFirebolt;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.ChangesScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.sprites.KingSprite;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.ui.Icons;

import java.util.ArrayList;

import static com.zrp200.rkpd2.Assets.Interfaces.TALENT_ICONS;

import static com.zrp200.rkpd2.actors.hero.HeroClass.*;

import static com.zrp200.rkpd2.messages.Messages.get;

import static com.zrp200.rkpd2.sprites.CharSprite.NEGATIVE;
import static com.zrp200.rkpd2.sprites.CharSprite.POSITIVE;
import static com.zrp200.rkpd2.sprites.CharSprite.WARNING;

import static com.zrp200.rkpd2.sprites.HeroSprite.avatar;

import static com.zrp200.rkpd2.sprites.ItemSpriteSheet.CROWN;
import static com.zrp200.rkpd2.sprites.ItemSpriteSheet.KIT;

import static com.zrp200.rkpd2.ui.Icons.DEPTH;
import static com.zrp200.rkpd2.ui.Icons.INFO;
import static com.zrp200.rkpd2.ui.Icons.TALENT;
import static com.zrp200.rkpd2.ui.Icons.TARGET;
import static com.zrp200.rkpd2.ui.Icons.get;

import static com.zrp200.rkpd2.ui.Window.*;

import static java.util.Arrays.asList;

// TODO should I have a separate section for shattered changes?
public class RKPD2Changes {

    private RKPD2Changes() {} // singleton
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        for(ChangeInfo[] section : new RKPD2Changes().changes) changeInfos.addAll(asList(section));
    }

    // utility
    private static ChangeButton bugFixes(String message) {
        return new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), get(ChangesScene.class, "bugfixes"), message);
    }
    private static ChangeButton misc(String message) {
        return new ChangeButton(get(Icons.PREFS), get(ChangesScene.class,"misc"), message);
    }

    // section types
    private static ChangeInfo NewContent(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "new"),
                false, TITLE_COLOR,
                "",
                buttons);
    }
    private static ChangeInfo Buffs(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "buffs"),
                false, POSITIVE,
                "",
                buttons);
    }
    private static ChangeInfo Changes(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "changes"),
                false, WARNING, "",
                buttons);
    }
    private static ChangeInfo Nerfs(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "nerfs"),
                false, NEGATIVE,
                "",
                buttons);
    }

    // more utils

    /** makes a list in the standard PD style.
     * [lineSpace] determines the number of spaces between each list item.
     * If you want to append extra spaces, you should do it at the end of the previous item, rather than at the start of that item.*/
    private static String list(int lineSpace, String... items) {
        StringBuilder builder = new StringBuilder();
        for (int j=0; j < lineSpace; j++) builder.append('\n');
        for (String item : items) {
            builder.append("_-_ ").append( item );
            for (int j = 0; j < lineSpace; j++) builder.append('\n');
        }
        return builder.toString();
    }

    // yet another attempt at formatting changes. not sure if this is any better than the last one, but one thing is certain: I HATE the way it's done in Shattered.
    // in this case I made it so you could add buttons in the ChangeInfo constructor; this is 'lustrous' style

    final ChangeInfo[][] changes = {
        {
            new ChangeInfo("v0.1.1 INDEV", true, TITLE_COLOR, ""),
            new ChangeInfo("From SHPD v1.0.0", false, SHPX_COLOR, "",
                    // alchemy stuff once it's added in.
                    new ChangeButton(new ItemSprite(CROWN), "Armor Ability", ""),
                    misc(""),
                    bugFixes("")),
            NewContent(
                new ChangeButton(new ItemSprite(ItemSpriteSheet.ARMOR_RAT_KING), "Rat King's Wrath Redesign!", "I've finally gotten around to updating Rat King's Wrath to reflect v0.9.3 reworks to armor abilities!"
                        + "\n\nWhile the previous Wrath was a combination of all armor abilities, the prospect of combining 13 different abilities into one isn't possible under the Wrath design, so I have instead decided to adapt the ones that have similar functionality to each part of the previous Wrath: _Smoke Bomb, Shockwave, Elemental Blast, and Spectral Blades._"
                        + "\nNote, however, that Wrath is not a perfect mirror of these abilities, though all their mechanics are there in some form."
                        + "\n" + list(1,
                        "Energy cost increased to 60 from 35.",
                        "Added four new talents to Wrath. Each new talent corresponds as closely as possible to the talents of the respective armor ability. Wrath does not have Heroic Energy.",
                        "Smoke Bomb no longer grants invisibility, mechanic instead moved to corresponding talent. Range is reduced to 6, from 8.",
                        "Molten Earth effect replaced with Elemental Blast.",
                        "Wrath's leap no longer stuns adjacent foes, instead sends out a 360 degree AOE Shockwave that covers a 3x3 area.",
                        "Spectral Blades retains the ability to hit all targets in sight (removing the need to target it), but deals less damage and has reduced chances to proc on-hit effects when attacking many targets.")),
                new ChangeButton(avatar(HUNTRESS, 6), "Huntress", list(2,
                        "Added a _secret subclass_ to Huntress, accessible by a secret interaction while choosing a subclass.",
                        "_Restored Nature_ root duration reverted to 2/3, down from 4/6, but it now also causes health potions and related alchemy products to be used instantly.")
                        + "_Multi-Shot:_" + list(1,
                        "Now uses multiple buffs to show that more than one target is marked.",
                        "Allows stacking of free-targeted marks instead of overriding them when a new target is marked.",
                        "Has changed free-targeting logic (thanks to smart-targeting) to make these new interactions smoother; enemies that are already targeted will be highlighted while manually targeting.")
                        + "\nMulti-shot should now be more complex, but in exchange it should (somewhat ironically) be easier to use and understand. It's also much more flexible with its free-targeted sniper special functionality.")),
            Changes(
                new ChangeButton(new ItemSprite(ItemSpriteSheet.STONE_ENCHANT), "Enchanting Logic",
                        list(1, "The chance for rare weapon enchantments to appear has been increased by ~50%.")
                                + "_\n\nSpirit Bow only:_" + list(2,
                                "Stones of Enchantment can no longer roll Lucky or Blocking.",
                                "Explosive Enchantment can now be rolled by Stones of Enchantment.",
                                "Explosive is now exactly as common as a standard uncommon enchantment in Shattered Pixel Dungeon. "
                                    + "Other uncommon enchants are now slightly more common to compensate.",
                                "Grim no longer has specifically boosted chances to appear.")),
                misc(list(2,
                        // gameplay related
                        "Reduced _Ratforcements_ stats by ~20%",
                        "TODO _Energizing Meal I_ now adds new recharging buffs instead of stacking on existing ones.",
                        "Talents that identify curses now declare whether an item is cursed when activated\n.",
                        // ui
                        "Most windows now scroll if they would not fit on the screen.",
                        "Changed commentary on Rat King's tier 3 talents.",
                        "Changed capitalization logic, hyphenated titles now have both words capitalized.")),
                bugFixes(list(2,
                        "Lethal Momentum not working with Death Mark.",
                        "Rat King's light cloak now is 13/27/40 instead of 10/20/30",
                        "Fixed a mistake when updating Ranger to Multi-Shot talent.",
                        "Rat King's Wrath sometimes freezing Rat King's sprite after use."))),
            Nerfs(
                new ChangeButton(avatar(ROGUE,6), "Rogue Balance", ""
                        + "_Mending Shadows_ turned out to have exploits, so it's (very unfortunately) being largely scrapped:"
                        + list(1,
                        "Now provides shielding every 2/1 turns, up to 3/5 max shielding (Shattered Protective Shadows).",
                        "Healing rate reduced to every 4/2 turns, and it no longer works while starving.")
                        + "\n\nIn addition, I'm making these changes based on player feedback:" + list(2,
                        "_Cached Rations_ now gives 3/5 rations, down from 4/6.",
                        "_Light Cloak_ effectiveness now 20%/40%/60%, down from 25/50/75.")),
                new ChangeButton(HeroSubClass.WARLOCK.icon(), "Warlock's Touch", ""
                        + "Warlock's Touch is currently either extremely broken or extremely useless, so I've decided to try to rein in the brokenness while preserving its core concept and expanding it to apply in more cases."
                        + list(2,
                        "Proc chance on physical attacks is now a fixed 15/25/35% chance at +1/+2/+3 respectively.",
                        "Proc duration of mark is now a fixed 6 turns, instead of being 10 + weapon level",
                        "Allies can now inflict soul mark via Warlock's Touch.",
                        "Chance for proccing soul mark with the attack that inflicts it is 20/30/40%, down from 25/50/75%, but now applies to wands if Soul Siphon is upgraded.")),
                new ChangeButton(avatar(RAT_KING, 6), "Rat King", list(2,
                        "The recent Strongman buff has turned Tactics into a monster, while Imperial Wrath is still rather niche in comparison. Thus, Imperial Wrath now has Strongman instead of Tactics.",
                        "Royal Intuition's +1 effect is now additive with the SHPD Survialist's Intuition rather than multiplicative. It is now 2.75x/3.75x id speed, down from 3.5x/5.25x.",
                        "Rat King is also affected by the v1.0.0 staff nerf."))),
        },
            // v0.1.0
        {
            new ChangeInfo("v0.1.0", true, TITLE_COLOR, ""),

            new ChangeInfo("v0.1.0b", false, TITLE_COLOR, "",
                    bugFixes(list(2,
                            "Sorcery not giving BM effects on non-staff melee attacks",
                            "Berserking Stamina not level-shifted in terms of berserk recovery speed.",
                            "Confusing typo in Berserking Stamina description",
                            "Spectral blades being able to attack NPCs (actually a shattered bug)",
                            "Fan of Blades dealing more damage to additional targets than intended.",
                            "Projecting Blades giving more wall penetration for the initial target than additional targets.")
                    )
            ),
            new ChangeInfo("v0.1.0a", false, TITLE_COLOR, "",
                    new ChangeButton(new ItemSprite(new KingsCrown()), "Ability adjustments",list(2,
                            "Death mark now gives +33% damage, up from +25%.",
                            "Projecting blades penetration is now 1/3/5/7/9, down from 2/4/6/8/10, and its accuracy boost is 0/33/67/100/133 instead of 25/50/75/100/125.",
                            "Fan of blades now affects 1/2-3/4/5-6/7 targets in a 30/45/90/135/180 degree AOE cone, from 1/2/3/4/5 targets in a 30/60/90/120/150 degree cone AOE.",
                            "Growing power no longer has a +0 effect, overall point effectiveness increased to compensate.",
                            "Nerfed Nature's Wrath to be 10%/20%/30%/40%, down from 13%/25%/38%/50%")),
                    new ChangeButton(avatar(WARRIOR, 6), "Berserker", "I don't think level-shifting talents is what berserker needs. I'll explore different avenues for changes here later, but in the meantime I've reverted most of the levelshifting here and lightly buffed the affected talents instead."
                            +list(2,
                            "Endless rage now gives +20%/40%/60% maximum rage instead of +15/30/45/60 rage. This is still up from v0.0.1, because endless rage is such a crappy talent that's only somewhat salvaged by the fact I made it cause you to gain more rage at low HP.",
                            "Enraged Catalyst now gives +0/+20/40/60% enchant proc rate at full rage, instead of +17/+33/+50/+66",
                            "Berserking stamina level shifting left intact (actually it was implemented).")),
                    new ChangeButton(Icons.get(TARGET), "Special Action Targeting", "Refactored Special Action Targeting. The only notable change is that only visible enemies are highlighted now; unopened mimics for example should no longer be autotargeted and must be manually tapped."),
                    bugFixes(list(2,
                            "Rogue's cloak boost to weapons being incorrectly displayed in some cases",
                            "Missing text for certain Rat King's Wrath interactions",
                            "String formatting failure in Enhanced Scrolls",
                            "Various typos in talent and subclass descriptions."))),
            NewContent(
                new ChangeButton(Icons.get(INFO), "Developer Commentary",
                    "I regret the relatively long wait for a v0.9.3 implementation, but RKPD2 is now updated to Shattered v0.9.3c! Enjoy smaller levels, some quality of life improvements, and even more buffs to your favorite heroes."
                    + "\n\nI'm currently messing with a new method with which to buff talents and heroes: _point shifting_. Point shifting means that I make +1 effects +0 effects, +2 effects +1 effects, and so on. This results in an immediate power bump when the particular talents are obtained, though it can be slightly confusing on the first time (or with tier 2 talents)."
                    + "\n\nThere's probably going to be a v0.1.1 that will add more thematic changes that I can make building off of this update."
                ),
                new ChangeButton(new ItemSprite(CROWN), "T4 Talents Implemented!", ""
                    +"Now bringing you a RKPD2 with FULL talents!!!"
                    +list(2,
                        "Epic armors and T4 talents have been directly implemented.",
                        "The 12 points for t3 talents remain, causing the hero to uniquely gain two talent points per level for levels 21-24.",
                        "You can still supercharge your armor if you have too low charge, don't worry, though I doubt it'll be as notable as before with the charging mechanic changes and Heroic Energy.",
                        "Rat King has two \"special\" abilities to choose from, currently, and one more will hopefully be added in future updates."
                    )
                    + "_Ability Buffs_:" + list(1,
                        "Heroic Energy, Elemental Power, Reactive Barrier, and Nature's Wrath are now more potent.",
                        "Ratforcements spawns scaled rats that are much stronger than standard rats. It can also summon scaled loyal albinos now.",
                        "Ratlomancy now gives 50% more adrenaline for 50% more fun.",
                        "Death Mark has been buffed heavily.",
                        "Growing Power, Blast Radius, Long Range Warp, and Telefrag have been level-shifted",
                        "Shadow Clone and Spectral Blades have their talents level-shifted.")
                ),
                new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "New Challenge!", "Badder bosses has been implemented into RKPD2, enjoy teaching those bosses that no matter what they do, they will still lose."),
                new ChangeButton(Icons.get(TARGET), "Special Action Targeting", "Added smart targeting for Combo, Preparation, and Sniper's Mark's free shot via Multishot."
                    + list(2,
                        "When there is only one target for the action, the action will skip the prompt and simply execute.",
                        "When there is more than one possible target (or no targets), the valid targets will be highlighted.")
                    + "This should make using these abilities much more smooth when fighting a single enemy as well as making them more intuitive in general."
                )
            ),

            Changes(
                new ChangeButton(avatar(RAT_KING, 6), "Rat King", ""
                        +"Shattered balance changes have been directly implemented to Rat King's mechanics, for better or for worse:"
                        +list(2,
                            "Noble Cause nerfed, gives less shielding when the staff runs out of charge.",
                            "Imperial Wrath buffed, now gives more shielding on berserk (yay?)",
                            "Tactics buffed, its strongman now uses the v0.9.3 version and its description has been updated to indicate that cleave makes combo last for 15/30/45 turns. This talent is probably the biggest winner of this update.",
                            "Ranged Terror buffed, now gives a greater damage boost to specials when using thrown weapons.",
                            "Royal Presence changed, now has an increased chance to spawn wraiths but decreased indirect damage soul mark effectiveness and ally warp range",
                            "Natural Dominance nerfed, now gives 50/100/150% Rat King's level in barkskin that fades every turn instead of previous barkskin effect.")
                        //+"\n\nThe following balance changes have been made:"
                        //+"\n_TODO_ Strongman talent effect moved from Tactics to Imperial Wrath, uses new 0.9.3 implementation."
                        //+"\n_TODO_ Hold fast talent effect moved from Imperial Wrath to Tactics."
                        //+"\n\nTactics is already one of the safest investments you can have, it really doesn't need a strongman buff right now, and strongman fits IW better thematically."
                ),
                new ChangeButton(Icons.get(DEPTH), "Levelgen", "Implementing SHPD v0.9.3's levelgen changes has resulted in an even smaller RKPD2!"
                        +list(2,
                            "Levels should be slightly smaller than before in terms of room amounts",
                            "There also should be less tunnels, which should significantly cut down further on level sizes. Up until now RKPD2 was plagued by very long connection room generation. No longer!",
                            "Entrance and Exits can no longer connect to each other.",
                            "Pit room size minimum increased.",
                            "Hordes are somewhat less likely to spawn on floor 1 despite the smaller size.")
                ),
                misc(list(2,
                        "Implemented virtually all misc changes up to SHPD v0.9.3c, including the addition of quick-use bags, stone behavior, etc.",
                        "Sorcery talent moved from 3rd slot to 6th slot for consistency with other RKPD2-exclusive talents.",
                        "Updated some talent descriptions to be clearer or otherwise add commentary.",
                        "Added unique dialogue for Rat King-Wandmaker interactions",
                        "Dwarf King has a new snide comment for you in badder bosses!",
                        "Dwarf King now bellows on certain quotes",
                        "Added differing descriptions for loyal rats and hostile rats, might wanna look at their descriptions when you get the chance.")
                ),
                bugFixes(list(2,
                        "Bugfixes up to SHPD v0.9.3c have been implemented.",
                        "King's Vision now correctly updates Rat King's field of view on the turn it is upgraded.",
                        "Fixed some talent description typos."))
            ),

            Buffs(
                new ChangeButton(avatar(HUNTRESS, 6), "Huntress Subclasses and T3",
                    "I'm leaning harder on giving subclasses access to talents without upgrading them, and _Warden_ is an excellent place to start:"
                    +list(1,
                        "Tipped Darts shifted, now gives 2x/3x/4x/5x durability.",
                        "v0.9.3 Barkskin implemented, but Barkskin now degrades every two turns instead of one and gets barkskin equal to 50/100/150/200% of her level at +0/1/2/3 respectively. Thus instead of nerfing it, I have buffed it ;)")
                    +"\nIt's also clear that _Sniper needs buffs._"
                    +list(1,
                        "Farsight shifted, now gives 25/50/75/100% bonus sight.",
                        "Shared Upgrades shifted (now 10/20/30/40), and the increasing duration aspect is given at +0.",
                        "New Sniper perk: +33 accuracy.")
                    //+"\n"
                    //+"\n_DEFERRED_ Multishot shifted, +3 now is 4x duration+4x target"
                    // TODO see if I can make the new targeting make this a smooth addition for future
                    //+"\n_TODO_ Multishot free shot can stack with standard marking."
                    //+"\n"
                ),
                new ChangeButton(avatar(WARRIOR,6), "Warrior", list(2,
                        "Cleave buffed from 0/10/20/30 (was bugged) to 15/30/45/60.",
                        "Endless Rage buffed from +15%/30%/45% to +20%/40%/60%",
                        "Enraged Catalyst buffed from 17%/33%/50% to 20%/40%/60%.",
                        "Berserking Stamina level-shifted.",
                        "Improvised projectiles is not nerfed.")),
                new ChangeButton(avatar(MAGE, 6), "Battlemage",
                        "Battlemage is currently a bit 'weaker' than warlock, and thus it's getting a power spike after subclassing."
                        + list(2,
                            "Mystical Charge recharging now .5/1/1.5/2 instead of 0/.75/1.5/2.25 at +0/1/2/3 respectively.",
                            "Excess Charge proc chance is now 20/40/60/80 at +0/1/2/3, up from 0/25/50/75.")
                ),
                misc(list(2,
                        "Assassin's Enhanced Lethality is buffed to be in line with SHPD",
                        "Studded gloves damage now 1-6, up from 1-5.",
                        "Empowering scrolls talent no longer is time-limited, boost increased to +2/+4/+6.",
                        "Shops sell upgraded items for cheaper (again)."
                ))
            ),
            Nerfs(
                new ChangeButton(new Image(TALENT_ICONS, 16*3,0,16,16), "Iron Will",
                    "I've decided to take the opportunity to yank Iron Will's bonus upgrade mechanics. It's really unfun to use and it kinda warps the game to a large extent."
                            +list(2,
                                "Iron Will no longer gives bonus upgrades to the seal.",
                                "Iron Will now increases the seal's maximum shield by 1/3/5 and reduces the time it takes to fully recharge all shield by 30/60/90 turns."
                            )
                            +"This change makes iron will recharge the seal super fast, even when not upgraded. At the very start of the game, the time to fully recharge two shield is halved!"
                ),
                new ChangeButton(new Image(TALENT_ICONS, 16*10, 0, 16,16), "Strongman", "I've implemented and (obviously) buffed Shattered's Strongman (13%/21%/29% up from 8%/13%/18%), now that Evan has finally gotten his act together and realized that I was right all along."
                        +"\n\n_+3:_"+list(1,
                            ":14-:17 strength -> +4 -> 18/19/20/21 down from 19/20/21/22",
                            "12-13 strength -> +3 -> 15/16 down from 17/18",
                            "10-11 strength -> +2 -> 12/13 down from 15/16",
                            "It is effectively around the same starting around 15 strength (16 if you were using a greataxe), but is increasingly worse prior to that."
                        ) + "\n_+2_:"+list(1,
                            "Gives a worse boost (2 down from 3) before the hero naturally attains 15 strength.",
                            "Gives a better boost once the hero has natural 20 strength"
                        )
                        +"\n_+1_:"+list(1,
                            "Gives +2 strength, up from its effective previous value of +1, once the hero naturally attains 16 strength."
                        )
                        +"\nOverall Strongman is a bit worse at +3 and a bit better at +2 and +1. Its ability to be exploited is down due to now being reliant on having strength, but in return it also gives true strength bonuses (thus opening up synergies with rings of force and might...)"
                ),
                new ChangeButton(new Image(TALENT_ICONS,16*3,16,16,16), "Backup Barrier",
                        "Backup barrier now generates 5/8 shield, down from 6/9, to reflect the Shattered nerf to Backup Barrier."))
        },
            // v0.0.1
        {
            new ChangeInfo("v0.0.1", true, TITLE_COLOR, "") {{
                addButton(new ChangeButton(get(Icons.INFO), "Developer Commentary", "This update is mostly just bugfixes and balance adjustments. More substantial changes should come when SHPD v0.9.3 is released.\n\nDo note that while things are intended to be broken, I'm aiming for a state where things are 'evenly' overpowered such that you can play any class or do any build and be like 'that's really damn good' for everything, rather than resetting (or just choosing talents!) for that same broken build every time."));
                addButton(new ChangeButton(avatar(WARRIOR, 6), "Warrior", "This is intended to make Warrior (and Berserker) a little more balanced powerwise compared to other stuff in the game."
                    + "\n\nGeneral:" + list(1,
                        "Implemented buffed runic transference.",
                        "Nerfed Iron Stomach to be in line with SHPD.",
                        "Removed Iron Will charge speed increase and bonus shielding.",
                        "Strongman buffed to 1/3/5 from 1/2/3. It's good now I swear!")
                    + "\nBerserker:" + list(1,
                        "Fixed a bug that made it MUCH easier to get rage while recovering than intended.",
                        "Berserking Stamina recovery nerfed from 1.5/1/.5 to 1.67/1.33/1. Shielding unchanged.",
                        "Enraged Catalyst was bugged to be at shpd levels (17/33/50), but now that's intended.",
                        "One Man Army now only gives boost if you're in combat, rather than simply relying on line of sight.",
                        "Berserker no longer gets rage from non-melee sources. (this was an unintentional addition)",
                        "Current HP weighting now refers to actual current HP, instead of current HP after damage is dealt.")
                ));
                addButton(new ChangeButton(avatar(ROGUE, 6), "Rogue", "Of all the classes, Rogue has the most exploits and uneven gameplay. These changes are intended to make him seem less repetitive/unbalanced.\n"
                    + list(1,
                        "Removed Mystical Meal-Horn of Plenty exploit, it was WAY more potent than I'd thought it would be.",
                        "Sucker punch now only works on the first surprise attack, numbers unchanged.",
                        "Marathon runner potency halved (is now 17%/33%/50%).",
                        "Mystical Upgrade is now more potent, gives instant 8/12 recharge to non-cloak artifacts, up from 5/8.",
                        "Enhanced Rings now stacks.")
                ));
                addButton(new ChangeButton(avatar(RAT_KING, 6), "Rat King", list(2,
                        "Mercenary Commander renamed to Flash, no longer gives the same blink benefit as Assassin's Reach (was bugged lol), instead gives the standard SHPD blink scaling it was supposed to have.",
                        "Changed Rat King's hero description in Hero Select and the descriptions of many of his t3 talents.",
                        "Added a note about how to toggle special actions for Rat King's subclass, and added a hotkey for toggling special action for desktop users.",
                        "Added unique dialog for Ambitious Imp.")
                ));
                addButton(new ChangeButton(new ItemSprite(KIT), "Epic Armor", ""
                    + list(2,
                        "Overcharging is now standardized to +100% per overcharge, down from 200% for everyone but Rat King.",
                        "Rogue Garb now has no limit on the distance it can jump.",
                        "Huntress Spectral Blades now has no distance limit.")
                    + "Rat King's Wrath:" + list(1,
                        "jump takes an extra turn.",
                        "Changed effects to make them more distinct (and fixed a couple omitted visual effects)",
                        "Changed the priority of the invisibility to actually cover the last turn of delay.")
                ));
                addButton(misc(list(2,"Huntress spirit bow now scales to +10, down from +12.",
                    "Empowering Scrolls talent's effect now lasts for 40 turns, up from 30.",
                    "Shopkeepers now offer reasonable deals for both buying and selling...",
                    "ID VFX is now used for on-equip identification.",
                    "Adjusted VFX for Hearty Meal and Test Subject.",
                    "Added a shatter vfx effect when 'skipping' DK.",
                    "Added a sfx for fully recovering, since apparently it's needed.",
                    "Troll Blacksmith now transitions directly into the reforge window after initially talking to him.")
                ));
                addButton(bugFixes("Fixed many bugs involving soul marking that were directly impacting the power of soul mark:"
                    + list(1,
                        "Warlock's Touch can now actually provide its benefit immediately.",
                        "Fixed melee attacks proccing soul mark twice.",
                        "Fixed melee attacks coming from the hero being considered indirect damage and thus requiring Soul Siphon or Presence to work with soul mark at all.",
                        "Rare crashes when zapping the shopkeeper with a wand that can soul-mark.")
                    + "\nOther fixes:" + list(1,
                        "Huntress and Rat King getting more berries at +2 from their respective talents than intended.",
                        "Being able to apply sniper's mark to multiple foes when it shouldn't be possible.",
                        "Typos")
                ));
            }},
            new ChangeInfo("v0.0.1a", false, TITLE_COLOR, "",
                new ChangeButton(avatar(HUNTRESS, 6), "Multishot and Warden", ""
                    + "\nMultishot:" + list(1,
                        "now has a unique icon.",
                        "levels for targets are now evaluated independently for the purposes of shared upgrades.",
                        "the total duration of sniper's mark is now the sum of the durations of each marked target.",
                        "free shot is now kept for 1/2/3x duration at +1/2/3.",
                        "Improved target handling between floors and when some targets cannot be shot.")
                    + "\nWarden's power seems to currently be concentrated in Nature's Better Aid:"
                    + list(1,
                        "NBA seed boost now +17/33/50% down from +33/66/100%",
                        "NBA dew boost now +8/17/25% down from +33/66/100%",
                        "Shielding Dew now also gives upfront shielding equal to 25%/50%/75% of the heal when not at full HP.")),
                new ChangeButton(get(Icons.TALENT), "General Talents", ""
                    + list(1,
                        "Scholar's Intuition +2 now has a 50% chance to identify consumables when picking them up, down from 2/3",
                        "Scholar's Intuition +1 now identifies wand levels on the first zap.")
                    + list(1, "Restored Nature now roots for 4/6 turns, up from 3/5")
                    + list(1,
                        "Seer Shot cooldown reduced from 20 to 5. (It was bugged to be 20 instead of 10)",
                        "Natural Dominance's Seer Shot cooldown increased to 20 from 10")
                    + "\n_-_ Fixed an oversight that caused Soul Siphon to cause wands to trigger soul mark at 200/300/400% effectiveness instead of 20/30/40%"
                ),
                misc(list(1,
                        "The level at which enemies stop giving exp has been increased again by 1 (now +2 compared to shattered)",
                        "The level at which enemies stop dropping stuff has been decreased by 1 to match this new value. (now same as shattered)"
                    ).trim() + list(2,
                        "explosive arrows can now proc on miss regardless of where they are shot.",
                        "Adrenaline and Sniper's Mark visual fades now match their initial durations",
                        "Dwarf King now gloats if he manages to kill you.")),
                bugFixes(list(2,
                        "natural dominance incorrectly giving ally swap instead of king's presence",
                        "Rogue not actually recieving innate 2x ring id speed.",
                        "display bugs with rings, especially might and cursed rings.",
                        "Multishot not checking if a given character is already marked when marking an enemy with sniper's mark",
"'Hang' bug when attempting to target an out-of-range character with free shot from multi-shot",
                        "Shattered bug where dk shows his alert during the whole second phase.",
                        "Gamebreaking typo in update screen.")))
        },
            // v0.0.0
        {
            new ChangeInfo("v0.0.0",true, TITLE_COLOR, "This is a list of all the stuff I've changed from SHPD, for those who are curious."),
            new ChangeInfo("Classes",false,"") {{
                    addButton(new ChangeButton(avatar(RAT_KING,6), "New Class!",
                            "Added a new class! Rat King is supposed to be a sort of 'omniclass', with the perks of all the shattered classes combined.\n\n"
                                    + "He also has his subclass implemented, which, in line with the above, is of course all subclasses in one. I'm pretty proud of this one, give it a shot!"));
                    addButton(new ChangeButton(avatar(WARRIOR,6),"Warrior",""
                            + list(1,
                                "Iron Will now increases the amount of upgrades the seal can store.",
                                "All t1 talents buffed.")
                            + "\n_Berserker_:" + list(1,
                                "Rage gain now scales with HP as talents are upgraded",
                                "Added a new talent that makes him attack faster with more enemies around")
                            + "\n_Gladiator_:" + list(1,
                                "All finishers get an extra damage roll check for increased consistency via Skill talent")));
                    addButton(new ChangeButton(avatar(MAGE,6),"Mage", list(2,
                            "Mage now has intrinsic +2 level to all wands for purposes of power and recharge",
                            "Battlemage now has a new talent that lets him spread his staff effects to all his attacks",
                            "Battlemage gets +2 effect on his staff.",
                            "Warlock can now soul mark with weapons and all damage can now trigger soul mark through new Warlock's Touch talent",
                            "Most talents buffed.",
                            "Empowering meal has been removed (for mage at least) and replaced with Energizing Meal, t2 meal replaced with something else.")));
                    addButton(new ChangeButton(avatar(ROGUE,6),"Rogue", list(2,
                            "Now gets an invisible +1 to weapons when cloak is active",
                            "Subclasses get more invisible upgrades to various item types.",
                            "Subclasses have their t3s buffed.",
                            "Cloak recharges faster",
                            "Talents buffed",
                            "Protective Shadows replaced by mending shadows.")));
                    addButton(new ChangeButton(avatar(HUNTRESS,6),"Huntress",
                        "Huntress has also recieved 'tweaks'!" + list(2,
                                "Spirit Bow now scales more.",
                                "The Spirit bow can be enchanted simply by using a scroll of upgrade on it.",
                                "Huntress has her innate heightened vision and missile durability back!",
                                "Huntress talents have been buffed across the board by ~50%.",
                                "Replaced invigorating meal with adrenalizing meal. ;)",
                                "Added new talents to both subclasses")));
                    addButton(new ChangeButton(new ItemSprite(new VelvetPouch()), "Bags",
                            "All heroes start with all bags. This should reduce the stress of inventory management throughout the game and also make gold more plentiful."));
                }},
            new ChangeInfo("Misc", false, "") {{
                    addButton(new ChangeButton(new WandOfFirebolt(), "Added wand of firebolt."
                            + '\n' + list(1,
                                "Does 0-9 damage with +1/+6 scaling instead of its vanilla/early shattered 1-8 with exponential scaling.",
                                "Is otherwise unchanged.")
                            + "\nThis should make it more consistently overpowered instead of requiring 10+ upgrades to reach actually astounding performance. I can probably call this my greatest mistake of all time. Oh well..."));
                    addButton(new ChangeButton(get(Icons.DEPTH),"Level gen changes", list(2,
                            "Amount of standard rooms reduced by 30%-50% in honor of precedent set by Rat King Dungeon.",
                            "Gold yield from Rat King's room increased by ~10x, general gold yield increased by 50%",
                            "Hero takes 1/3 damage from Grim traps.",
                            "Rat King room has a sign in it if playing as rat king as a homage to RKPD",
                            "Enemy level caps increased by 1")));
                    addButton(new ChangeButton(new KingSprite(),"Boss Changes","Certain bosses have recieved adjustments:" +
                            list(2,"Dwarf King fight start altered.",
                                "Dwarf King's phases can now be skipped with enough burst damage.",
                                "Dwarf King and DM-300 have altered quotes for Rat King",
                                "DM-300 fight starts directly upon moving initially, instead of when you see a pylon. Better know what you are doing!")));
                    addButton(new ChangeButton(new ItemSprite(new SpiritBow().enchant(new Explosive())),"Spirit Bow enchantments",
                            "The idea here is to lower the chance of 'misfiring' when enchanting the spirit bow dramatically."
                                    + list(2,
                                        "Added a new spirit-bow exclusive enchantment.",
                                        "It, along with grim, will replace blocking and lucky when using a scroll of enchantment on the bow (or a scroll of upgrade in the case of huntress).")));
                    addButton(new ChangeButton(new ItemSprite(new WarriorArmor()),"Class Armor","Who liked limits anyway? You can trade current HP for a full charge."));
                    addButton(new ChangeButton(get(Icons.LANGS),"Text Changes",
                            "I've changed some of the text in the game, including:"
                            + list(1,
                                "Class descriptions both ingame and in the select screen",
                                "Some enemy descriptions",
                                "Some boss quotes",
                                "This changelog")
                            + "\nHowever, since I only know English and do not have an active translation project, I've set the language to English. Sorry!"));
                }}
        },
    };
}
