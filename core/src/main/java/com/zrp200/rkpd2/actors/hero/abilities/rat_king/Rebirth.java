package com.zrp200.rkpd2.actors.hero.abilities.rat_king;

import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.items.armor.ClassArmor;

public class Rebirth extends ArmorAbility {
    {
        baseChargeUse = 0;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        //does nothing
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.TURNABOUT, Talent.REINFORCEMENT, Talent.RATCELERATE, Talent.EYE_THERAPY};
    }
}
