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
import com.github.artemdevel.pixeldungeon.ResultDescriptions;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.blobs.Blob;
import com.github.artemdevel.pixeldungeon.actors.blobs.Fire;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Burning;
import com.github.artemdevel.pixeldungeon.effects.MagicMissile;
import com.github.artemdevel.pixeldungeon.effects.particles.FlameParticle;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.mechanics.Ballistica;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.utils.Utils;
import com.github.artemdevel.pixeldungeon.game.utils.Callback;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class WandOfFirebolt extends Wand {

    {
        name = "Wand of Firebolt";
    }

    @Override
    protected void onZap(int cell) {
        int level = power();

        for (int i = 1; i < Ballistica.distance - 1; i++) {
            int c = Ballistica.trace[i];
            if (Level.flammable[c]) {
                GameScene.add(Blob.seed(c, 1, Fire.class));
            }
        }

        GameScene.add(Blob.seed(cell, 1, Fire.class));

        Char ch = Actor.findChar(cell);
        if (ch != null) {
            ch.damage(Random.Int(1, 8 + level * level), this);
            Buff.affect(ch, Burning.class).reignite(ch);

            ch.sprite.emitter().burst(FlameParticle.FACTORY, 5);

            if (ch == curUser && !ch.isAlive()) {
                Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth));
                GLog.logNegative("You killed yourself with your own Wand of Firebolt...");
            }
        }
    }

    protected void fx(int cell, Callback callback) {
        MagicMissile.fire(curUser.sprite.parent, curUser.pos, cell, callback);
        Game.sound.play(Assets.SND_ZAP);
    }

    @Override
    public String desc() {
        return
            "This wand unleashes bursts of magical fire. It will ignite " +
            "flammable terrain, and will damage and burn a creature it hits.";
    }
}
