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

public class SacrificialParticle extends PixelParticle.Shrinking {

    public static final Emitter.Factory FACTORY = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SacrificialParticle) emitter.recycle(SacrificialParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public SacrificialParticle() {
        super();

        color(0x4488EE);
        lifespan = 0.6f;

        acc.set(0, -100);
    }

    public void reset(float x, float y) {
        revive();

        this.x = x;
        this.y = y - 4;

        left = lifespan;

        size = 4;
        speed.set(0);
    }

    @Override
    public void update() {
        super.update();
        float p = left / lifespan;
        am = p > 0.75f ? (1 - p) * 4 : 1;
    }
}