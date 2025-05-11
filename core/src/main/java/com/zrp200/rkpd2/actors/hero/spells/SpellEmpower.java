package com.zrp200.rkpd2.actors.hero.spells;

import static com.zrp200.rkpd2.Dungeon.hero;

import com.watabou.noosa.Image;
import com.watabou.utils.GameMath;
import com.zrp200.rkpd2.actors.Char;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if (tracker != null && tracked >= 1) tracker.countUp(tracked);
        if (detach) {
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
    protected List<Object> getDescArgs() {
        return Collections.singletonList(
                (int)SpellEmpower.limit()
        );
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
            public String desc() {
                return super.desc() + "\n\n" + Messages.get(this, "cooldown", (int)Math.ceil(Cooldown.duration(target)));
            }

            @Override
            public int icon() { return BuffIndicator.DIVINE_ADVENT; }

            @Override
            public float getTurnsPerCharge() {
                return 25;
            }

            @Override
            public void detach() {
                super.detach();
                // cooldown starts after finish
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

            private static final float BASE_DURATION = 100, PENALTY = 25f;

            public static float duration(Char target) {
                Tracker tracker = target.buff(Tracker.class);
                return BASE_DURATION + PENALTY * (tracker != null ? tracker.count() : 0);
            }

            public float duration() { return duration(target); }
        }

        public static class Tracker extends CounterBuff {
            @Override
            public boolean act() {
                if (!SpellEmpower.isActive()) {
                    diactivate();
                } else {
                    spend(TICK);
                    countDown(Math.min(count(), 1/Cooldown.PENALTY));
                }
                return true;
            }
        };
    }

    public static class LimitBreak extends SpellEmpower {
        public static final LimitBreak INSTANCE = new LimitBreak();

        @Override
        public int icon() { return HeroIcon.LIMIT_BREAK; }

        {
            talent = Talent.LIMIT_BREAK;
        }

        @Override
        public boolean ignoreChargeUse() { return heroAtCriticalHealth(); }

        @Override
        protected List<Object> getDescArgs() {
            List<Object> args = new ArrayList(super.getDescArgs());
            args.add(heroAtCriticalHealth() ? 300 : 150);
            return args;
        }

        @Override
        protected void applyBuff(Hero hero) {
            Buff.affect(hero, Buff.class);
            Cooldown cd = Cooldown.affectHero(Cooldown.class);
            if (heroAtCriticalHealth()) cd.spend(-150);
        }

        @Override
        public float chargeUse(Hero hero) {
            return heroAtCriticalHealth() ? 2 : 4;
        }

        public static class Buff extends SpellEmpower.Buff {
            @Override
            public int icon() { return BuffIndicator.LIMIT_BREAK; }

            @Override
            public String desc() {
                return super.desc() + "\n" + Messages.get(this, "cooldown", target.buff(Cooldown.class).iconTextDisplay());
            }

            @Override
            public float getTurnsPerCharge() {
                return 15;
            }
        }

        public static class Cooldown extends SpellCooldown {
            public int icon() {
                return target.buff(Buff.class) != null ? BuffIndicator.NONE : BuffIndicator.LIMIT_BREAK;
            }
            public float duration() {
                return 300;
            }
        }

        private static boolean heroAtCriticalHealth() {
            return hero.HP * 10 <= hero.HT * 3;
        }
    }

    public abstract static class Buff extends CounterBuff {
        {
            type = buffType.POSITIVE;
        }

        public abstract float getTurnsPerCharge();

        @Override
        public boolean act() {
            spend(TICK);
            countUp(1/getTurnsPerCharge()); // doesn't last forever
            if (left() < 0) detach();

            return true;
        }

        private int turnsUntilCost() {
            float prop = 1 - count() % 1;
            return (int)Math.ceil(getTurnsPerCharge() * prop);
        }

        public float left() {
            return limit() - count();
        }

        @Override
        public abstract int icon();

        @Override
        public void detach() {
            super.detach();
            if (!RecallInscription.INSTANCE.isVisible((Hero)target)) {
                Buff.detach(target, RecallInscription.UsedItemTracker.class);
            }
        };

        @Override
        public float iconFadePercent() {
            return GameMath.gate(0, count() / limit(), 1);
        }

        @Override
        public String iconTextDisplay() {
            return String.valueOf((int)Math.ceil(left()));
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.get(this, "overspend"), iconTextDisplay(), turnsUntilCost());
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
