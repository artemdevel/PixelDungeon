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

package com.github.artemdevel.pixeldungeon.game.common;

import java.util.ArrayList;

public class Group extends Gizmo {

    // Accessing it is a little faster, than calling members.getSize()
    public int length;
    protected ArrayList<Gizmo> members;

    public Group() {
        members = new ArrayList<>();
        length = 0;
    }

    @Override
    public void destroy() {
        for (Gizmo g : members) {
            if (g != null) {
                g.destroy();
            }
        }

        members.clear();
        members = null;
        length = 0;
    }

    @Override
    public void update() {
        // NOTE: Can't use the iterator here because of ConcurrentModificationException when
        // update a collection during the iteration
        // https://developer.android.com/reference/java/util/ConcurrentModificationException.html
        // This happens, for example, after a long click on an item which suits to a quick slot.
        for (int i = 0; i < length; i++) {
            Gizmo g = members.get(i);
            if (g != null && g.exists && g.active) {
                g.update();
            }
        }
    }

    @Override
    public void draw() {
        for (Gizmo g : members) {
            if (g != null && g.exists && g.visible) {
                g.draw();
            }
        }
    }

    @Override
    public void kill() {
        // A killed group keeps all its members, but they get killed too
        for (Gizmo g : members) {
            if (g != null && g.exists) {
                g.kill();
            }
        }

        super.kill();
    }

    public Gizmo add(Gizmo g) {
        if (g.parent == this) {
            return g;
        }

        if (g.parent != null) {
            g.parent.remove(g);
        }

        // Trying to find an empty space for a new member
        int index = members.indexOf(null);
        if (index != -1) {
            members.set(index, g);
            g.parent = this;
            return g;
        }

        members.add(g);
        g.parent = this;
        length++;
        return g;
    }

    public Gizmo addToBack(Gizmo g) {
        if (g.parent == this) {
            sendToBack(g);
            return g;
        }

        if (g.parent != null) {
            g.parent.remove(g);
        }

        if (members.get(0) == null) {
            members.set(0, g);
            g.parent = this;
            return g;
        }

        members.add(0, g);
        g.parent = this;
        length++;
        return g;
    }

    public Gizmo recycle(Class<? extends Gizmo> c) {
        Gizmo g = getFirstAvailable(c);
        if (g != null) {
            return g;
        } else if (c == null) {
            return null;
        } else {
            try {
                return add(c.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    // Fast removal - replacing with null
    public Gizmo erase(Gizmo g) {
        int index = members.indexOf(g);
        if (index != -1) {
            members.set(index, null);
            g.parent = null;
            return g;
        } else {
            return null;
        }
    }

    // Real removal
    public Gizmo remove(Gizmo g) {
        if (members.remove(g)) {
            length--;
            g.parent = null;
            return g;
        } else {
            return null;
        }
    }

    public Gizmo getFirstAvailable(Class<? extends Gizmo> c) {
        for (Gizmo g : members) {
            if (g != null && !g.exists && ((c == null) || g.getClass() == c)) {
                return g;
            }
        }

        return null;
    }

    public int countLiving() {
        int count = 0;

        for (Gizmo g : members) {
            if (g != null && g.exists && g.alive) {
                count++;
            }
        }

        return count;
    }

    public Gizmo random() {
        if (length > 0) {
            // TODO: Is it the source of poor randomness?
            return members.get((int) (Math.random() * length));
        } else {
            return null;
        }
    }

    public void clear() {
        for (Gizmo g : members) {
            if (g != null) {
                g.parent = null;
            }
        }
        members.clear();
        length = 0;
    }

    public Gizmo bringToFront(Gizmo g) {
        if (members.contains(g)) {
            members.remove(g);
            members.add(g);
            return g;
        } else {
            return null;
        }
    }

    public Gizmo sendToBack(Gizmo g) {
        if (members.contains(g)) {
            members.remove(g);
            members.add(0, g);
            return g;
        } else {
            return null;
        }
    }
}
