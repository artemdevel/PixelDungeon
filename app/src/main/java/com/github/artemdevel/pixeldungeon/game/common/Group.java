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

public class Group extends Entity {

    // Accessing it is a little faster, than calling members.getSize()
    public int length;
    protected ArrayList<Entity> members;

    public Group() {
        members = new ArrayList<>();
        length = 0;
    }

    @Override
    public void destroy() {
        for (Entity entity : members) {
            if (entity != null) {
                entity.destroy();
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
            Entity entity = members.get(i);
            if (entity != null && entity.exists && entity.active) {
                entity.update();
            }
        }
    }

    @Override
    public void draw() {
        for (Entity entity : members) {
            if (entity != null && entity.exists && entity.visible) {
                entity.draw();
            }
        }
    }

    @Override
    public void kill() {
        // A killed group keeps all its members, but they get killed too
        for (Entity entity : members) {
            if (entity != null && entity.exists) {
                entity.kill();
            }
        }

        super.kill();
    }

    public Entity add(Entity entity) {
        if (entity.parent == this) {
            return entity;
        }

        if (entity.parent != null) {
            entity.parent.remove(entity);
        }

        // Trying to find an empty space for a new member
        int index = members.indexOf(null);
        if (index != -1) {
            members.set(index, entity);
            entity.parent = this;
            return entity;
        }

        members.add(entity);
        entity.parent = this;
        length++;
        return entity;
    }

    public Entity addToBack(Entity entity) {
        if (entity.parent == this) {
            sendToBack(entity);
            return entity;
        }

        if (entity.parent != null) {
            entity.parent.remove(entity);
        }

        if (members.get(0) == null) {
            members.set(0, entity);
            entity.parent = this;
            return entity;
        }

        members.add(0, entity);
        entity.parent = this;
        length++;
        return entity;
    }

    public Entity recycle(Class<? extends Entity> entityClass) {
        Entity entity = getFirstAvailable(entityClass);
        if (entity != null) {
            return entity;
        } else if (entityClass == null) {
            return null;
        } else {
            try {
                return add(entityClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    // Fast removal - replacing with null
    public Entity erase(Entity entity) {
        int index = members.indexOf(entity);
        if (index != -1) {
            members.set(index, null);
            entity.parent = null;
            return entity;
        } else {
            return null;
        }
    }

    // Real removal
    public Entity remove(Entity entity) {
        if (members.remove(entity)) {
            length--;
            entity.parent = null;
            return entity;
        } else {
            return null;
        }
    }

    public Entity getFirstAvailable(Class<? extends Entity> entityClass) {
        for (Entity entity : members) {
            if (entity != null && !entity.exists && ((entityClass == null) || entity.getClass() == entityClass)) {
                return entity;
            }
        }

        return null;
    }

    public int countLiving() {
        int count = 0;

        for (Entity entity : members) {
            if (entity != null && entity.exists && entity.alive) {
                count++;
            }
        }

        return count;
    }

    public Entity random() {
        if (length > 0) {
            // TODO: Is it the source of poor randomness?
            return members.get((int) (Math.random() * length));
        } else {
            return null;
        }
    }

    public void clear() {
        for (Entity entity : members) {
            if (entity != null) {
                entity.parent = null;
            }
        }
        members.clear();
        length = 0;
    }

    public Entity bringToFront(Entity entity) {
        if (members.contains(entity)) {
            members.remove(entity);
            members.add(entity);
            return entity;
        } else {
            return null;
        }
    }

    public Entity sendToBack(Entity entity) {
        if (members.contains(entity)) {
            members.remove(entity);
            members.add(0, entity);
            return entity;
        } else {
            return null;
        }
    }
}
