package com.zrp200.rkpd2.actors.hero.spells;

import static com.zrp200.rkpd2.Dungeon.hero;

import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.zrp200.rkpd2.actors.buffs.Cooldown;
import com.zrp200.rkpd2.actors.buffs.CounterBuff;
import com.zrp200.rkpd2.actors.buffs.Recharging;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.items.artifacts.HolyTome;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.ui.HeroIcon;

public abstract class SpellEmpower extends ClericSpell {

    public static void useCharge(float initialCharges, float charge) {
        SpellEmpower.Buff empower = hero.virtualBuff(SpellEmpower.Buff.class);
        DivineAdvent.Tracker tracker = hero.buff(SpellEmpower.DivineAdvent.Tracker.class);
        float tracked = 0;
        boolean detach = false;
        if (tracker != null && initialCharges < charge && (empower != null || forceTrackCharges)) {
            tracked = charge - Math.max(0, initialCharges);
        }
        if (empower != null) {
            empower.countUp(charge);
            float excess = empower.count() - limit();
            if (excess > tracked) tracked = excess;
            detach = excess >= 0;
        }
        if (tracker != null) tracker.countUp(tracked);
        if (detach) {
            empower.applyCooldown();
            empower.detach();
        }
    }

    private static boolean forceTrackCharges = false;

    public static boolean isActive() {
        return hero != null && hero.virtualBuff(SpellEmpower.Buff.class) != null;
    }

    public static float limit() {
        return Math.max(
                1 << hero.pointsInTalent(Talent.LIMIT_BREAK),
                2 + hero.pointsInTalent(Talent.DIVINE_ADVENT)
        );
    }

    public static float left() {
        Buff buff = hero.virtualBuff(Buff.class);
        return buff != null ? buff.left() : 0;
    }

    protected Talent talent;

    @Override
    public boolean canCast(Hero hero) {
        return hero.hasTalent(talent) && !isActive() && hero.virtualBuff(SpellCooldown.class) == null;
    }

    @Override
    protected Object[] getDescArgs() {
        return new Object[]{(int)SpellEmpower.limit()};
    }

    @Override
    public abstract float chargeUse(Hero hero);
    protected abstract void applyBuff(Hero hero);

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        onSpellCast(tome, hero);
        applyBuff(hero);
        hero.sprite.showStatus(CharSprite.POSITIVE, this.name());
    }

    public static class DivineAdvent extends SpellEmpower {

        public static final DivineAdvent INSTANCE = new DivineAdvent();

        {
            talent = Talent.DIVINE_ADVENT;
        }

        protected void applyBuff(Hero hero) {
            Buff.append(hero, Recharging.class, 2 * (1 + hero.pointsInTalent(talent)));
            Buff.affect(hero, Buff.class);
        }

        @Override
        public void onCast(HolyTome tome, Hero hero) {
            forceTrackCharges = true;
            try {
                Buff.affect(hero, Tracker.class);
                super.onCast(tome, hero);
            } finally {
                forceTrackCharges = false;
            }
        }

        @Override
        public int icon() { return HeroIcon.DIVINE_ADVENT; }

        @Override
        public float chargeUse(Hero hero) {
            return 8 - hero.pointsInTalent(talent);
        }

        @Override
        public boolean ignoreChargeUse() {
            return true;
        }

        public static class Buff extends SpellEmpower.Buff {

            @Override
            public int icon() { return BuffIndicator.DIVINE_ADVENT; }

            @Override
            public void applyCooldown() {
                Cooldown.affectHero(Cooldown.class);
            }
        }

        public static class Cooldown extends SpellCooldown {
            @Override
            public int icon() { return BuffIndicator.DIVINE_ADVENT; }

            @Override
            public void detach() {
                detach(target, Tracker.class);
                super.detach();
            }

            public float duration() {
                Tracker tracker = target.buff(Tracker.class);
                return 150 + 25 * (tracker != null ? tracker.count() : 0);
            }
        }

        public static class Tracker extends CounterBuff {};
    }

    public static class LimitBreak extends SpellEmpower {
        public static final LimitBreak INSTANCE = new LimitBreak();

        @Override
        public int icon() { return HeroIcon.LIMIT_BREAK; }

        {
            talent = Talent.LIMIT_BREAK;
        }

        @Override
        protected Object[] getDescArgs() {
            Object[] descArgs = super.getDescArgs(), res = new Object[descArgs.length + 1];
            System.arraycopy(descArgs, 0, res, 0, descArgs.length);
            res[descArgs.length] = Math.round(new Cooldown().duration() - 100 * (1 - hpFactor(hero)));
            return res;
        }

        @Override
        protected void applyBuff(Hero hero) {
            Buff.affect(hero, Buff.class).hpFactor = hpFactor(hero);
        }

        @Override
        public float chargeUse(Hero hero) {
            return (float)Math.ceil(2 + 4 * hpFactor(hero));
        }

        public static class Buff extends SpellEmpower.Buff {
            @Override
            public int icon() { return BuffIndicator.LIMIT_BREAK; }

            @Override
            public void applyCooldown() {
                Cooldown.affectHero(Cooldown.class).spend(-100 * (1 - hpFactor));
            }

            private float hpFactor;

            private static final String LOW_HP="low hp";

            @Override
            public void storeInBundle(Bundle bundle) {
                super.storeInBundle(bundle);
                bundle.put(LOW_HP, hpFactor);
            }

            @Override
            public void restoreFromBundle(Bundle bundle) {
                super.restoreFromBundle(bundle);
                hpFactor = bundle.getFloat(LOW_HP);
            }
        }

        public static class Cooldown extends SpellCooldown {
            public int icon() { return BuffIndicator.LIMIT_BREAK; }
            public float duration() {
                return 400;
            }
        }

        private static float hpFactor(Hero hero) {
            // normalized to give maximum reduction at 30% hp or lower
            final float MIN = 0.3f;
            return Math.max(0, (hero.HP / (float)hero.HT - MIN) / (1 - MIN));
        }
    }

    public abstract static class Buff extends CounterBuff {
        {
            type = buffType.POSITIVE;
        }

        public float left() {
            return limit() - count();
        }

        @Override
        public abstract int icon();

        public abstract void applyCooldown();

        @Override
        public float iconFadePercent() {
            return GameMath.gate(0, count() / limit(), 1);
        }

        @Override
        public String iconTextDisplay() {
            return String.valueOf((int)left());
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.get(this, "overspend"), iconTextDisplay());
        }
    }

    private static abstract class SpellCooldown extends Cooldown {
        public abstract int icon();

        @Override
        public void tintIcon(Image icon) {
            icon.tint(0x80808080);
        }
    }
}
