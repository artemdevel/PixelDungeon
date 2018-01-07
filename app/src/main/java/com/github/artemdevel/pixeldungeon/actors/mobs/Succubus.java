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

import com.github.artemdevel.pixeldungeon.game.common.audio.GameSound;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Charm;
import com.github.artemdevel.pixeldungeon.actors.buffs.Light;
import com.github.artemdevel.pixeldungeon.actors.buffs.Sleep;
import com.github.artemdevel.pixeldungeon.effects.Speck;
import com.github.artemdevel.pixeldungeon.items.scrolls.ScrollOfLullaby;
import com.github.artemdevel.pixeldungeon.items.wands.WandOfBlink;
import com.github.artemdevel.pixeldungeon.items.weapon.enchantments.Leech;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.mechanics.Ballistica;
import com.github.artemdevel.pixeldungeon.sprites.SuccubusSprite;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Succubus extends Mob {

    private static final int BLINK_DELAY = 5;

    private int delay = 0;

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(Leech.class);
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Sleep.class);
    }

    {
        name = "succubus";
        spriteClass = SuccubusSprite.class;

        HP = HT = 80;
        defenseSkill = 25;
        viewDistance = Light.DISTANCE;

        EXP = 12;
        maxLvl = 25;

        loot = new ScrollOfLullaby();
        lootChance = 0.05f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(15, 25);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Random.Int(3) == 0) {
            Buff.affect(enemy, Charm.class, Charm.durationFactor(enemy) * Random.IntRange(3, 7)).object = id();
            enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
            GameSound.INSTANCE.play(Assets.SND_CHARMS);
        }

        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        if (Level.fieldOfView[target] && Level.distance(pos, target) > 2 && delay <= 0) {
            blink(target);
            spend(-1 / speed());
            return true;
        } else {
            delay--;
            return super.getCloser(target);
        }
    }

    private void blink(int target) {
        int cell = Ballistica.cast(pos, target, true, true);

        if (Actor.findChar(cell) != null && Ballistica.distance > 1) {
            cell = Ballistica.trace[Ballistica.distance - 2];
        }

        WandOfBlink.appear(this, cell);

        delay = BLINK_DELAY;
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }

    @Override
    public int dr() {
        return 10;
    }

    @Override
    public String description() {
        return "The succubi are demons that look like seductive (in a slightly gothic way) girls. Using its magic, the succubus " +
            "can charm a hero, who will become unable to attack anything until the charm wears off.";
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
