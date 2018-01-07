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

import com.github.artemdevel.pixeldungeon.game.common.audio.GameSound;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.mobs.npcs.NPC;
import com.github.artemdevel.pixeldungeon.effects.CellEmitter;
import com.github.artemdevel.pixeldungeon.effects.MagicMissile;
import com.github.artemdevel.pixeldungeon.effects.Speck;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.mechanics.Ballistica;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.sprites.SheepSprite;
import com.github.artemdevel.pixeldungeon.utils.BArray;
import com.github.artemdevel.pixeldungeon.game.utils.Callback;
import com.github.artemdevel.pixeldungeon.game.utils.PathFinder;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class WandOfFlock extends Wand {

    {
        name = "Wand of Flock";
    }

    @Override
    protected void onZap(int cell) {
        int level = power();

        int n = level + 2;

        if (Actor.findChar(cell) != null && Ballistica.distance > 2) {
            cell = Ballistica.trace[Ballistica.distance - 2];
        }

        boolean[] passable = BArray.or(Level.passable, Level.avoid, null);
        for (Actor actor : Actor.all()) {
            if (actor instanceof Char) {
                passable[((Char) actor).pos] = false;
            }
        }

        PathFinder.buildDistanceMap(cell, passable, n);
        int dist = 0;

        if (Actor.findChar(cell) != null) {
            PathFinder.distance[cell] = Integer.MAX_VALUE;
            dist = 1;
        }

        float lifespan = level + 3;

        sheepLabel:
        for (int i = 0; i < n; i++) {
            do {
                for (int j = 0; j < Level.LENGTH; j++) {
                    if (PathFinder.distance[j] == dist) {
                        Sheep sheep = new Sheep();
                        sheep.lifespan = lifespan;
                        sheep.pos = j;
                        GameScene.add(sheep);
                        Dungeon.level.mobPress(sheep);

                        CellEmitter.get(j).burst(Speck.factory(Speck.WOOL), 4);

                        PathFinder.distance[j] = Integer.MAX_VALUE;

                        continue sheepLabel;
                    }
                }
                dist++;
            } while (dist < n);
        }
    }

    protected void fx(int cell, Callback callback) {
        MagicMissile.wool(curUser.sprite.parent, curUser.pos, cell, callback);
        GameSound.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public String desc() {
        return "A flick of this wand summons a flock of magic sheep, creating temporary impenetrable obstacle.";
    }

    // TODO: Move it to a separated file?
    public static class Sheep extends NPC {

        private static final String[] QUOTES = {
            "Baa!",
            "Baa?",
            "Baa.",
            "Baa..."
        };

        {
            name = "sheep";
            spriteClass = SheepSprite.class;
        }

        public float lifespan;

        private boolean initialized = false;

        @Override
        protected boolean act() {
            if (initialized) {
                HP = 0;
                destroy();
                sprite.die();
            } else {
                initialized = true;
                spend(lifespan + Random.Float(2));
            }
            return true;
        }

        @Override
        public void damage(int dmg, Object src) {
        }

        @Override
        public String description() {
            return
                "This is a magic sheep. What's so magical about it? You can't kill it. " +
                "It will stand there until it magcially fades away, all the while chewing cud with a blank stare.";
        }

        @Override
        public void interact() {
            yell(Random.element(QUOTES));
        }
    }
}
