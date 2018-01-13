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
package com.github.artemdevel.pixeldungeon.items.scrolls;

import com.github.artemdevel.pixeldungeon.game.common.Game;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Invisibility;
import com.github.artemdevel.pixeldungeon.actors.buffs.Rage;
import com.github.artemdevel.pixeldungeon.actors.mobs.Mimic;
import com.github.artemdevel.pixeldungeon.actors.mobs.Mob;
import com.github.artemdevel.pixeldungeon.effects.Speck;
import com.github.artemdevel.pixeldungeon.items.Heap;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.utils.GLog;

public class ScrollOfChallenge extends Scroll {

    {
        name = "Scroll of Challenge";
    }

    @Override
    protected void doRead() {
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            mob.beckon(curUser.pos);
            if (Dungeon.visible[mob.pos]) {
                Buff.affect(mob, Rage.class, Level.distance(curUser.pos, mob.pos));
            }
        }

        for (Heap heap : Dungeon.level.heaps.values()) {
            if (heap.type == Heap.Type.MIMIC) {
                Mimic m = Mimic.spawnAt(heap.pos, heap.items);
                if (m != null) {
                    m.beckon(curUser.pos);
                    heap.destroy();
                }
            }
        }

        GLog.logWarning("The scroll emits a challenging roar that echoes throughout the dungeon!");
        setKnown();

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
        Game.sound.play(Assets.SND_CHALLENGE);
        Invisibility.dispel();

        readAnimation();
    }

    @Override
    public String desc() {
        return
            "When read aloud, this scroll will unleash a challenging roar " +
            "that will awaken all monsters and alert them to the reader's location.";
    }
}
