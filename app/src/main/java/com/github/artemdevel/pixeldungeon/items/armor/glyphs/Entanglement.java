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
package com.github.artemdevel.pixeldungeon.items.armor.glyphs;

import com.github.artemdevel.pixeldungeon.game.common.Camera;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Roots;
import com.github.artemdevel.pixeldungeon.effects.CellEmitter;
import com.github.artemdevel.pixeldungeon.effects.particles.EarthParticle;
import com.github.artemdevel.pixeldungeon.items.armor.Armor;
import com.github.artemdevel.pixeldungeon.items.armor.Armor.Glyph;
import com.github.artemdevel.pixeldungeon.plants.Earthroot;
import com.github.artemdevel.pixeldungeon.sprites.ItemSprite;
import com.github.artemdevel.pixeldungeon.sprites.ItemSprite.Glowing;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Entanglement extends Glyph {

    private static final String TXT_ENTANGLEMENT = "%s of entanglement";

    private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x448822);

    @Override
    public int process(Armor armor, Char attacker, Char defender, int damage) {
        int level = Math.max(0, armor.effectiveLevel());

        if (Random.Int(4) == 0) {
            Buff.prolong(defender, Roots.class, 5 - level / 5);
            Buff.affect(defender, Earthroot.Armor.class).level(5 * (level + 1));
            CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8);
            Camera.main.shake(1, 0.4f);
        }

        return damage;
    }

    @Override
    public String name(String weaponName) {
        return String.format(TXT_ENTANGLEMENT, weaponName);
    }

    @Override
    public Glowing glowing() {
        return GREEN;
    }

}
