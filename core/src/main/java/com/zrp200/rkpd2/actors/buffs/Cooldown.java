package com.zrp200.rkpd2.actors.buffs;

import static com.zrp200.rkpd2.Dungeon.hero;

import com.watabou.utils.GameMath;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.messages.Messages;

public abstract class Cooldown extends FlavourBuff {
    public static <T extends Cooldown> void affectHero(Class<T> cls) {
        if (cls == Cooldown.class) return;
        T buff = Buff.affect(hero, cls);
        buff.spend(buff.duration());
    }

    public abstract float duration();

    public float iconFadePercent() {
        return GameMath.gate(0, visualcooldown() / duration(), 1);
    }

    public String toString() {
        return Messages.get(this, "name");
    }

    public String desc() {
        return Messages.get(this, "desc", dispTurns(visualcooldown()));
    }
}
