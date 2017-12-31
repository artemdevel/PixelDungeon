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
package com.github.artemdevel.pixeldungeon.actors.mobs;

import com.github.artemdevel.pixeldungeon.Badges;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.buffs.Blindness;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.hero.Hero;
import com.github.artemdevel.pixeldungeon.items.Item;
import com.github.artemdevel.pixeldungeon.sprites.BanditSprite;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Bandit extends Thief {

    public Item item;

    {
        name = "crazy bandit";
        spriteClass = BanditSprite.class;
    }

    @Override
    protected boolean steal( Hero hero ) {
        if (super.steal( hero )) {

            Buff.prolong( hero, Blindness.class, Random.Int( 5, 12 ) );
            Dungeon.observe();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void die( Object cause ) {
        super.die( cause );
        Badges.validateRare( this );
    }
}
