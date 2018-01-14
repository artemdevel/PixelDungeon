/*
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

package com.github.artemdevel.pixeldungeon.game.input;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.artemdevel.pixeldungeon.game.utils.PointF;
import com.github.artemdevel.pixeldungeon.game.utils.Signal;

import android.view.MotionEvent;

public class Touchscreen {

    public static Signal<Touch> event = new Signal<>(true);

    public static HashMap<Integer, Touch> pointers = new HashMap<>();

    public static float x;
    public static float y;
    public static boolean touched;

    public static void processTouchEvents(ArrayList<MotionEvent> events) {
        for (MotionEvent event : events) {
            Touch touch;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    touched = true;
                    touch = new Touch(event, 0);
                    pointers.put(event.getPointerId(0), touch);
                    Touchscreen.event.dispatch(touch);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    int index = event.getActionIndex();
                    touch = new Touch(event, index);
                    pointers.put(event.getPointerId(index), touch);
                    Touchscreen.event.dispatch(touch);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int count = event.getPointerCount();
                    for (int j = 0; j < count; j++) {
                        pointers.get(event.getPointerId(j)).update(event, j);
                    }
                    Touchscreen.event.dispatch(null);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    Touchscreen.event.dispatch(pointers.remove(event.getPointerId(event.getActionIndex())).up());
                    break;
                case MotionEvent.ACTION_UP:
                    touched = false;
                    Touchscreen.event.dispatch(pointers.remove(event.getPointerId(0)).up());
                    break;
            }

            event.recycle();
        }
    }

    public static class Touch {

        public PointF start;
        public PointF current;
        public boolean down;

        public Touch(MotionEvent event, int index) {
            float x = event.getX(index);
            float y = event.getY(index);

            start = new PointF(x, y);
            current = new PointF(x, y);
            down = true;
        }

        public void update(MotionEvent event, int index) {
            current.set(event.getX(index), event.getY(index));
        }

        public Touch up() {
            down = false;
            return this;
        }
    }

}
