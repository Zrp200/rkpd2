package com.zrp200.rkpd2.actors.hero.abilities.rat_king;

import static com.zrp200.rkpd2.Dungeon.hero;

import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.actors.hero.abilities.Ratmogrify;
import com.zrp200.rkpd2.effects.Transmuting;
import com.zrp200.rkpd2.items.armor.ClassArmor;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.ui.HeroIcon;

import java.util.*;

public class OmniAbility extends ArmorAbility {

    private ArmorAbility armorAbility = null;

    @Override public void use(ClassArmor armor, Hero hero) {
        if(armorAbility == null) setArmorAbility();
        else {
            armorAbility.use(armor, hero);
            // change armor ability afterwards ( see ClassArmor.useCharge() )
        }

    }
    @Override protected void activate(ClassArmor armor, Hero hero, Integer target) {/*do nothing*/}

    // talent choosing
    public ArmorAbility activeAbility() { return armorAbility; }
    public void setArmorAbility() {
        Set<ArmorAbility> pool = new HashSet<>(abilities);
        pool.removeAll(activeAbilities());
        pool.remove(armorAbility);
        new Transmuting(
                new HeroIcon(armorAbility),
                // shouldn't be null.
                new HeroIcon( armorAbility = Objects.requireNonNull( Random.element(pool) ) )
        ).show(hero);
        if(hero.talents.size() >= 4) hero.talents.set(3, transferTalents(armorAbility));
    }

    @Override public boolean useTargeting() {
        return armorAbility != null ? armorAbility.useTargeting() : super.useTargeting();
    }

    @Override public float chargeUse(Hero hero) {
        return armorAbility != null ? armorAbility.chargeUse(hero) : 0;
    }

    @Override
    public String desc() {
        String desc = Messages.get(this,"desc");
        if(armorAbility != null) {
            desc += "\n\n"
                    + Messages.get(this, "ability_desc")
                    + "\n" + armorAbility.desc();
        }
        return desc;
    }

    @Override
    public String shortDesc() {
        String desc = super.shortDesc();
        if(armorAbility != null) {
            desc += "\n\n"
                    + Messages.get(this, "ability_desc")
                    + "\n" + armorAbility.shortDesc();
        }
        return desc;
    }

    @Override public Talent[] talents() {
        return armorAbility != null ? armorAbility.talents() : new Talent[0];
    }

    @Override
    public String actionName() {
        return armorAbility != null ? armorAbility.actionName() : super.actionName();
    }

    // note that if any other class ends up using bundling I'll need to store the ability list as well.
    private static final String ABILITY = "armorAbility";
    @Override public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ABILITY, armorAbility);
    }
    @Override public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        armorAbility = (ArmorAbility) bundle.get(ABILITY);
    }
    // interactions with talent-related stuff.

    /** establishes that these interactions need to be performed. **/
    public static boolean isApplicable() {
        return hero != null && hero.armorAbility instanceof OmniAbility;
    }
    /** omniability checks based on ability usage. **/
    public static void markAbilityUsed(ArmorAbility ability) {
        if(isApplicable()) {
            OmniAbility omniAbility = (OmniAbility) hero.armorAbility;
            if(ability.equals(omniAbility.armorAbility)) omniAbility.setArmorAbility();
        }
    }
    public static LinkedHashMap<Talent, Integer> transferTalents(ArmorAbility armorAbility) {
        LinkedHashMap<Talent, Integer> talents = new LinkedHashMap<>();
        if(hero.talents.size() < 4) return talents;
        Iterator<Integer> iterator = hero.talents.get(3).values().iterator();
        for(Talent talent : armorAbility.talents()) {
            talents.put(talent, iterator.hasNext() ? iterator.next() : 0);
        }
        return talents;
    }
    /** returns the amount of points that would be in that talent if it corresponded, otherwise return null. **/
    public static Integer findTalent(Talent talent) {
        for(ArmorAbility ability : activeAbilities()) {
            for(Talent t : ability.talents()) {
                if(t == talent) return transferTalents(ability).get(talent);
            }
        }
        return null;
    }
    public static Set<ArmorAbility> activeAbilities() {
        HashSet<ArmorAbility> r = new HashSet();
        if(isApplicable()) for(ArmorAbility ability : abilities) {
            if(ability.isActive()
                    && !ability.equals(( (OmniAbility) hero.armorAbility ).armorAbility)) {
                r.add(ability);
            }
        }
        return r;
    }
    /** Allows you to select previous actions that still can be interacted with when applicable. **/
    public static Map<String, ArmorAbility> additionalActions() {
        HashMap<String, ArmorAbility> map = new HashMap<>();
        for(ArmorAbility ability : activeAbilities()) map.put(ability.actionName(), ability);
        return map;
    }
    private static final Set<ArmorAbility> abilities = new HashSet(); static {
        for(HeroClass cls : HeroClass.values()) {
            if(cls == HeroClass.RAT_KING) continue;
            abilities.addAll(Arrays.asList(cls.armorAbilities()));
        }
        abilities.add(new Ratmogrify());
    }
}
