/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.github.artemdevel.pixeldungeon.effects.particles;

import com.github.artemdevel.pixeldungeon.game.common.particles.Emitter;
import com.github.artemdevel.pixeldungeon.game.common.particles.PixelParticle;
import com.github.artemdevel.pixeldungeon.game.common.particles.Emitter.Factory;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.game.utils.ColorMath;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class LeafParticle extends PixelParticle.Shrinking {

    public static int color1;
    public static int color2;


    public static final Emitter.Factory GENERAL = new Factory() {
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            LeafParticle p = ((LeafParticle)emitter.recycle( LeafParticle.class ));
            p.color( ColorMath.random( 0x004400, 0x88CC44 ) );
            p.reset( x, y );
        }
    };

    public static final Emitter.Factory LEVEL_SPECIFIC = new Factory() {
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            LeafParticle p = ((LeafParticle)emitter.recycle( LeafParticle.class ));
            p.color( ColorMath.random( Dungeon.level.color1, Dungeon.level.color2 ) );
            p.reset( x, y );
        }
    };

    public LeafParticle() {
        super();

        lifespan = 1.2f;
        acc.set( 0, 25 );
    }

    public void reset( float x, float y ) {
        revive();

        this.x = x;
        this.y = y;

        speed.set( Random.Float( -8, +8 ), -20 );

        left = lifespan;
        size = Random.Float( 2, 3 );
    }
}