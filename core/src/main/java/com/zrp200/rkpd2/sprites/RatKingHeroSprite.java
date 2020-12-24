package com.zrp200.rkpd2.sprites;

import com.watabou.noosa.TextureFilm;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;

public class RatKingHeroSprite extends HeroSprite {
    // placeholder
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 17;
    {
        //runFramerate = 10;
    }

    public void updateArmor() {
        // there's only two armors.
        TextureFilm film = new TextureFilm( tiers(), 0, FRAME_WIDTH, FRAME_HEIGHT );
        idle = new Animation( 2, true );
        idle.frames( film, 0, 0, 0, 1 );

        run = new Animation( runFramerate, true );
        run.frames( film, 6, 7, 8, 9, 10 );

        attack = new Animation( 15, false );
        attack.frames( film, 2, 3, 4, 5, 0 );

        die = new Animation( 10, false );
        die.frames( film, 0 );

        zap = attack.clone();

        operate = new Animation( 8, false );
        operate.frames( film, 2,6,2,6);

        fly = new Animation( 1, true );
        fly.frames( film, 0 );

        read = operate;

        if (Dungeon.hero.isAlive())
            idle();
        else
            die();
    }

    @Override
    public TextureFilm tiers() {
        if(tiers == null) tiers = tiers(Assets.Sprites.RAT_KING_HERO, FRAME_HEIGHT);
        return tiers;
    }
}
