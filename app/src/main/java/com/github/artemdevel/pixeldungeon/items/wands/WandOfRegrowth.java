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
package com.github.artemdevel.pixeldungeon.items.wands;

import com.github.artemdevel.pixeldungeon.game.common.Game;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.blobs.Blob;
import com.github.artemdevel.pixeldungeon.actors.blobs.Regrowth;
import com.github.artemdevel.pixeldungeon.effects.MagicMissile;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.levels.Terrain;
import com.github.artemdevel.pixeldungeon.mechanics.Ballistica;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.game.utils.Callback;

public class WandOfRegrowth extends Wand {

    {
        name = "Wand of Regrowth";
    }

    @Override
    protected void onZap(int cell) {
        for (int i = 1; i < Ballistica.distance - 1; i++) {
            int p = Ballistica.trace[i];
            int c = Dungeon.level.map[p];
            if (c == Terrain.EMPTY ||
                    c == Terrain.EMBERS ||
                    c == Terrain.EMPTY_DECO) {

                Level.set(p, Terrain.GRASS);
                GameScene.updateMap(p);
                if (Dungeon.visible[p]) {
                    GameScene.discoverTile(p, c);
                }

            }
        }

        int c = Dungeon.level.map[cell];
        if (c == Terrain.EMPTY ||
                c == Terrain.EMBERS ||
                c == Terrain.EMPTY_DECO ||
                c == Terrain.GRASS ||
                c == Terrain.HIGH_GRASS) {

            GameScene.add(Blob.seed(cell, (power() + 2) * 20, Regrowth.class));
        } else {
            GLog.logInfo("nothing happened");
        }
    }

    protected void fx(int cell, Callback callback) {
        MagicMissile.foliage(curUser.sprite.parent, curUser.pos, cell, callback);
        Game.sound.play(Assets.SND_ZAP);
    }

    @Override
    public String desc() {
        return
            "\"When life ceases new life always begins to grow... The eternal cycle always remains!\"";
    }
}
