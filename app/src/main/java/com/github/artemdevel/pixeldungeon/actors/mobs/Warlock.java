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

import java.util.HashSet;

import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.ResultDescriptions;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Weakness;
import com.github.artemdevel.pixeldungeon.items.Generator;
import com.github.artemdevel.pixeldungeon.items.weapon.enchantments.Death;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.mechanics.Ballistica;
import com.github.artemdevel.pixeldungeon.sprites.CharSprite;
import com.github.artemdevel.pixeldungeon.sprites.WarlockSprite;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.utils.Utils;
import com.github.artemdevel.pixeldungeon.game.utils.Callback;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Warlock extends Mob implements Callback {

    private static final float TIME_TO_ZAP = 1f;

    private static final String TXT_SHADOWBOLT_KILLED = "%s's shadow bolt killed you...";

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(Death.class);
    }

    {
        name = "dwarf warlock";
        spriteClass = WarlockSprite.class;

        HP = HT = 70;
        defenseSkill = 18;

        EXP = 11;
        maxLvl = 21;

        loot = Generator.Category.POTION;
        lootChance = 0.83f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(12, 20);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int dr() {
        return 8;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
    }

    protected boolean doAttack(Char enemy) {
        if (Level.adjacent(pos, enemy.pos)) {
            return super.doAttack(enemy);
        } else {
            boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap(enemy.pos);
            } else {
                zap();
            }

            return !visible;
        }
    }

    private void zap() {
        spend(TIME_TO_ZAP);

        if (hit(this, enemy, true)) {
            if (enemy == Dungeon.hero && Random.Int(2) == 0) {
                Buff.prolong(enemy, Weakness.class, Weakness.duration(enemy));
            }

            int dmg = Random.Int(12, 18);
            enemy.damage(dmg, this);

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail(Utils.format(ResultDescriptions.MOB,
                        Utils.indefinite(name), Dungeon.depth));
                GLog.logNegative(TXT_SHADOWBOLT_KILLED, name);
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public void call() {
        next();
    }

    @Override
    public String description() {
        return "When dwarves' interests have shifted from engineering to arcane arts, " +
            "warlocks have come to power in the city. They started with elemental magic, " +
            "but soon switched to demonology and necromancy.";
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }
}
