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
package com.github.artemdevel.pixeldungeon.levels;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.github.artemdevel.pixeldungeon.levels.painters.*;
import com.github.artemdevel.pixeldungeon.game.utils.BundleAble;
import com.github.artemdevel.pixeldungeon.game.utils.Bundle;
import com.github.artemdevel.pixeldungeon.game.utils.Graph;
import com.github.artemdevel.pixeldungeon.game.utils.Point;
import com.github.artemdevel.pixeldungeon.game.utils.Random;
import com.github.artemdevel.pixeldungeon.game.utils.Rect;
import com.github.artemdevel.pixeldungeon.utils.Utils;

public class Room extends Rect implements Graph.Node, BundleAble {

    private static final String ROOMS = "rooms";
    private static final String ROOM_TOP = "top";
    private static final String ROOM_BOTTOM = "bottom";
    private static final String ROOM_LEFT = "left";
    private static final String ROOM_RIGHT = "right";
    private static final String ROOM_TYPE = "type";

    public HashSet<Room> neighbours = new HashSet<>();
    public HashMap<Room, Door> connected = new HashMap<>();

    public int distance;
    public int price = 1;

    public enum Type {
        NULL(null),
        STANDARD(StandardPainter.class),
        ENTRANCE(EntrancePainter.class),
        EXIT(ExitPainter.class),
        BOSS_EXIT(BossExitPainter.class),
        TUNNEL(TunnelPainter.class),
        PASSAGE(PassagePainter.class),
        SHOP(ShopPainter.class),
        BLACKSMITH(BlacksmithPainter.class),
        TREASURY(TreasuryPainter.class),
        ARMORY(ArmoryPainter.class),
        LIBRARY(LibraryPainter.class),
        LABORATORY(LaboratoryPainter.class),
        VAULT(VaultPainter.class),
        TRAPS(TrapsPainter.class),
        STORAGE(StoragePainter.class),
        MAGIC_WELL(MagicWellPainter.class),
        GARDEN(GardenPainter.class),
        CRYPT(CryptPainter.class),
        STATUE(StatuePainter.class),
        POOL(PoolPainter.class),
        RAT_KING(RatKingPainter.class),
        WEAK_FLOOR(WeakFloorPainter.class),
        PIT(PitPainter.class),
        ALTAR(AltarPainter.class);

        private Method paint;

        Type(Class<? extends Painter> painter) {
            try {
                paint = painter.getMethod("paint", Level.class, Room.class);
            } catch (Exception e) {
                paint = null;
            }
        }

        public void paint(Level level, Room room) {
            try {
                paint.invoke(null, level, room);
            } catch (Exception e) {
                Utils.reportException(e);
            }
        }
    }

    public static final ArrayList<Type> SPECIALS = new ArrayList<>(Arrays.asList(
        Type.ARMORY,
        Type.WEAK_FLOOR,
        Type.MAGIC_WELL,
        Type.CRYPT,
        Type.POOL,
        Type.GARDEN,
        Type.LIBRARY,
        Type.TREASURY,
        Type.TRAPS,
        Type.STORAGE,
        Type.STATUE,
        Type.LABORATORY,
        Type.VAULT,
        Type.ALTAR
    ));

    public Type type = Type.NULL;

    public int random() {
        return random(0);
    }

    public int random(int m) {
        int x = Random.Int(left + 1 + m, right - m);
        int y = Random.Int(top + 1 + m, bottom - m);
        return x + y * Level.WIDTH;
    }

    public void addNeighbour(Room other) {
        Rect i = intersect(other);
        if ((i.width() == 0 && i.height() >= 3) || (i.height() == 0 && i.width() >= 3)) {
            neighbours.add(other);
            other.neighbours.add(this);
        }
    }

    public void connect(Room room) {
        if (!connected.containsKey(room)) {
            connected.put(room, null);
            room.connected.put(this, null);
        }
    }

    public Door entrance() {
        return connected.values().iterator().next();
    }

    public boolean inside(int p) {
        int x = p % Level.WIDTH;
        int y = p / Level.WIDTH;
        return x > left && y > top && x < right && y < bottom;
    }

    public Point center() {
        return new Point(
                (left + right) / 2 + (((right - left) & 1) == 1 ? Random.Int(2) : 0),
                (top + bottom) / 2 + (((bottom - top) & 1) == 1 ? Random.Int(2) : 0));
    }

    // **** Graph.Node interface ****

    @Override
    public int distance() {
        return distance;
    }

    @Override
    public void distance(int value) {
        distance = value;
    }

    @Override
    public int price() {
        return price;
    }

    @Override
    public void price(int value) {
        price = value;
    }

    @Override
    public Collection<Room> edges() {
        return neighbours;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(ROOM_LEFT, left);
        bundle.put(ROOM_TOP, top);
        bundle.put(ROOM_RIGHT, right);
        bundle.put(ROOM_BOTTOM, bottom);
        bundle.put(ROOM_TYPE, type.toString());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        left = bundle.getInt(ROOM_LEFT);
        top = bundle.getInt(ROOM_TOP);
        right = bundle.getInt(ROOM_RIGHT);
        bottom = bundle.getInt(ROOM_BOTTOM);
        type = Type.valueOf(bundle.getString(ROOM_TYPE));
    }

    public static void shuffleTypes() {
        int size = SPECIALS.size();
        for (int i = 0; i < size - 1; i++) {
            int j = Random.Int(i, size);
            if (j != i) {
                Type t = SPECIALS.get(i);
                SPECIALS.set(i, SPECIALS.get(j));
                SPECIALS.set(j, t);
            }
        }
    }

    public static void useType(Type type) {
        if (SPECIALS.remove(type)) {
            SPECIALS.add(type);
        }
    }

    public static void restoreRoomsFromBundle(Bundle bundle) {
        if (bundle.contains(ROOMS)) {
            SPECIALS.clear();
            for (String type : bundle.getStringArray(ROOMS)) {
                SPECIALS.add(Type.valueOf(type));
            }
        } else {
            shuffleTypes();
        }
    }

    public static void storeRoomsInBundle(Bundle bundle) {
        String[] array = new String[SPECIALS.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = SPECIALS.get(i).toString();
        }
        bundle.put(ROOMS, array);
    }

    public static class Door extends Point {

        public enum Type {
            EMPTY,
            TUNNEL,
            REGULAR,
            UNLOCKED,
            HIDDEN,
            BARRICADE,
            LOCKED
        }

        public Type type = Type.EMPTY;

        public Door(int x, int y) {
            super(x, y);
        }

        public void set(Type type) {
            if (type.compareTo(this.type) > 0) {
                this.type = type;
            }
        }
    }
}
