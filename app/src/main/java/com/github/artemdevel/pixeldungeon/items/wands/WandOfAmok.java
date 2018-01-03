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

import com.github.artemdevel.pixeldungeon.game.common.audio.Sample;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.buffs.Amok;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Vertigo;
import com.github.artemdevel.pixeldungeon.effects.MagicMissile;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.game.utils.Callback;

public class WandOfAmok extends Wand {

    {
        name = "Wand of Amok";
    }

    @Override
    protected void onZap(int cell) {
        Char ch = Actor.findChar(cell);
        if (ch != null) {
            if (ch == Dungeon.hero) {
                Buff.affect(ch, Vertigo.class, Vertigo.duration(ch));
            } else {
                Buff.affect(ch, Amok.class, 3f + power());
            }
        } else {
            GLog.i("nothing happened");
        }
    }

    protected void fx(int cell, Callback callback) {
        MagicMissile.purpleLight(curUser.sprite.parent, curUser.pos, cell, callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public String desc() {
        return
            "The purple light from this wand will make the target run amok " +
            "attacking random creatures in its vicinity.";
    }
}
