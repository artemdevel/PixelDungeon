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
package com.github.artemdevel.pixeldungeon;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import com.github.artemdevel.pixeldungeon.game.utils.BundleAble;
import com.github.artemdevel.pixeldungeon.game.utils.Bundle;

public class Journal {

    private static final String JOURNAL = "journal";

    public enum Feature {
        WELL_OF_HEALTH("Well of Health"),
        WELL_OF_AWARENESS("Well of Awareness"),
        WELL_OF_TRANSMUTATION("Well of Transmutation"),
        SACRIFICIAL_FIRE("Sacrificial chamber"),
        ALCHEMY("Alchemy pot"),
        GARDEN("Garden"),
        STATUE("Animated statue"),

        GHOST("Sad ghost"),
        WANDMAKER("Old wandmaker"),
        TROLL("Troll blacksmith"),
        IMP("Ambitious imp");

        public String desc;

        Feature(String desc) {
            this.desc = desc;
        }
    }

    public static final ArrayList<Record> records = new ArrayList<>();

    public static class Record implements Comparable<Record>, BundleAble {

        private static final String FEATURE = "feature";
        private static final String DEPTH = "depth";

        public Feature feature;
        public int depth;

        // NOTE: This empty constructor is required for restore from Bundle
        public Record() {
        }

        public Record(Feature feature, int depth) {
            this.feature = feature;
            this.depth = depth;
        }

        @Override
        public int compareTo(@NonNull Record another) {
            return another.depth - depth;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            feature = Feature.valueOf(bundle.getString(FEATURE));
            depth = bundle.getInt(DEPTH);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(FEATURE, feature.toString());
            bundle.put(DEPTH, depth);
        }
    }

    public static void reset() {
        records.clear();
    }

    public static void storeInBundle(Bundle bundle) {
        bundle.put(JOURNAL, records);
    }

    public static void restoreFromBundle(Bundle bundle) {
        records.clear();
        for (BundleAble rec : bundle.getCollection(JOURNAL)) {
            records.add((Record) rec);
        }
    }

    public static void add(Feature feature) {
        for (Record record : records) {
            if (record.feature == feature && record.depth == Dungeon.depth) {
                return;
            }
        }

        records.add(new Record(feature, Dungeon.depth));
    }

    public static void remove(Feature feature) {
        for (Record record : records) {
            if (record.feature == feature && record.depth == Dungeon.depth) {
                records.remove(record);
                return;
            }
        }
    }
}
