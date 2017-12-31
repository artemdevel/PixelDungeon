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
package com.github.artemdevel.pixeldungeon.actors.blobs;

import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Roots;
import com.github.artemdevel.pixeldungeon.effects.BlobEmitter;
import com.github.artemdevel.pixeldungeon.effects.particles.LeafParticle;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.levels.Terrain;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;

public class Regrowth extends Blob {

    @Override
    protected void evolve() {
        super.evolve();

        if (volume > 0) {

            boolean mapUpdated = false;

            for (int i=0; i < LENGTH; i++) {
                if (off[i] > 0) {
                    int c = Dungeon.level.map[i];
                    int c1 = c;
                    if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
                        c1 = cur[i] > 9 ? Terrain.HIGH_GRASS : Terrain.GRASS;
                    } else if (c == Terrain.GRASS && cur[i] > 9) {
                        c1 = Terrain.HIGH_GRASS ;
                    }

                    if (c1 != c) {
                        Level.set( i, Terrain.HIGH_GRASS );
                        mapUpdated = true;

                        GameScene.updateMap( i );
                        if (Dungeon.visible[i]) {
                            GameScene.discoverTile( i, c );
                        }
                    }

                    Char ch = Actor.findChar( i );
                    if (ch != null) {
                        Buff.prolong( ch, Roots.class, TICK );
                    }
                }
            }

            if (mapUpdated) {
                Dungeon.observe();
            }
        }
    }

    @Override
    public void use( BlobEmitter emitter ) {
        super.use( emitter );

        emitter.start( LeafParticle.LEVEL_SPECIFIC, 0.2f, 0 );
    }
}
