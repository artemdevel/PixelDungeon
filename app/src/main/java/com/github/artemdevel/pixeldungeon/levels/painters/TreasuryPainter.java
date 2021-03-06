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
package com.github.artemdevel.pixeldungeon.levels.painters;

import com.github.artemdevel.pixeldungeon.items.Gold;
import com.github.artemdevel.pixeldungeon.items.Heap;
import com.github.artemdevel.pixeldungeon.items.keys.IronKey;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.levels.Room;
import com.github.artemdevel.pixeldungeon.levels.Terrain;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class TreasuryPainter extends Painter {

    public static void paint(Level level, Room room) {
        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.EMPTY);

        set(level, room.center(), Terrain.STATUE);

        Heap.Type heapType = Random.Int(2) == 0 ? Heap.Type.CHEST : Heap.Type.HEAP;

        int n = Random.IntRange(2, 3);
        int pos;
        for (int i = 0; i < n; i++) {
            do {
                pos = room.random();
            } while (level.map[pos] != Terrain.EMPTY || level.heaps.get(pos) != null);
            level.drop(new Gold().random(), pos).type = (i == 0 && heapType == Heap.Type.CHEST ? Heap.Type.MIMIC : heapType);
        }

        if (heapType == Heap.Type.HEAP) {
            for (int i = 0; i < 6; i++) {
                do {
                    pos = room.random();
                } while (level.map[pos] != Terrain.EMPTY);
                level.drop(new Gold(Random.IntRange(1, 3)), pos);
            }
        }

        room.entrance().set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey());
    }
}
