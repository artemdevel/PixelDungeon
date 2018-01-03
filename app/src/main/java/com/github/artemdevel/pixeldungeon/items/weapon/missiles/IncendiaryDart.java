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
package com.github.artemdevel.pixeldungeon.items.weapon.missiles;

import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.blobs.Blob;
import com.github.artemdevel.pixeldungeon.actors.blobs.Fire;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Burning;
import com.github.artemdevel.pixeldungeon.items.Item;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.sprites.ItemSpriteSheet;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class IncendiaryDart extends MissileWeapon {

    {
        name = "incendiary dart";
        image = ItemSpriteSheet.INCENDIARY_DART;

        STR = 12;
    }

    public IncendiaryDart() {
        this(1);
    }

    public IncendiaryDart(int number) {
        super();
        quantity = number;
    }

    @Override
    public int min() {
        return 1;
    }

    @Override
    public int max() {
        return 2;
    }

    @Override
    protected void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if (enemy == null || enemy == curUser) {
            if (Level.flammable[cell]) {
                GameScene.add(Blob.seed(cell, 4, Fire.class));
            } else {
                super.onThrow(cell);
            }
        } else {
            if (!curUser.shoot(enemy, this)) {
                Dungeon.level.drop(this, cell).sprite.drop();
            }
        }
    }

    @Override
    public void process(Char attacker, Char defender, int damage) {
        Buff.affect(defender, Burning.class).reignite(defender);
        super.process(attacker, defender, damage);
    }

    @Override
    public String desc() {
        return
            "The spike on each of these darts is designed to pin it to its target " +
            "while the unstable compounds strapped to its length burst into brilliant flames.";
    }

    @Override
    public Item random() {
        quantity = Random.Int(3, 6);
        return this;
    }

    @Override
    public int price() {
        return 10 * quantity;
    }
}
