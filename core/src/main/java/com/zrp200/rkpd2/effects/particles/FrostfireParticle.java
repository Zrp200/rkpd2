package com.zrp200.rkpd2.effects.particles;

import com.watabou.noosa.particles.Emitter;

public class FrostfireParticle extends FlameParticle {

    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            ((FrostfireParticle)emitter.recycle( FrostfireParticle.class )).reset( x, y );
        }
        @Override
        public boolean lightMode() {
            return true;
        };
    };

    public FrostfireParticle() {
        super();

        color( 0x6de8e4 );
        lifespan = 0.6f;

        acc.set( 0, -80 );
    }
}
