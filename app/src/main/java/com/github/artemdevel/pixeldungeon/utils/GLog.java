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
package com.github.artemdevel.pixeldungeon.utils;

import com.github.artemdevel.pixeldungeon.game.utils.Signal;

import android.util.Log;

public class GLog {

    private static final String TAG = "GAME";

    public static final String POSITIVE = "++ ";
    public static final String NEGATIVE = "-- ";
    public static final String WARNING = "** ";
    public static final String HIGHLIGHT = "@@ ";

    public static Signal<String> update = new Signal<>();

    public static void logInfo(String text, Object... args) {
        if (args.length > 0) {
            text = Utils.format(text, args);
        }

        Log.i(TAG, text);
        update.dispatch(text);
    }

    public static void logPositive(String text, Object... args) {
        logInfo(POSITIVE + text, args);
    }

    public static void logNegative(String text, Object... args) {
        logInfo(NEGATIVE + text, args);
    }

    public static void logWarning(String text, Object... args) {
        logInfo(WARNING + text, args);
    }

    public static void logHighlight(String text, Object... args) {
        logInfo(HIGHLIGHT + text, args);
    }
}
