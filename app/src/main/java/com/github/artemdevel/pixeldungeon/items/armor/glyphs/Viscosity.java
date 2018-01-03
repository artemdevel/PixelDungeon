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

import com.github.artemdevel.pixeldungeon.Badges;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.ResultDescriptions;
import com.github.artemdevel.pixeldungeon.actors.Char;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.items.armor.Armor;
import com.github.artemdevel.pixeldungeon.items.armor.Armor.Glyph;
import com.github.artemdevel.pixeldungeon.sprites.CharSprite;
import com.github.artemdevel.pixeldungeon.sprites.ItemSprite;
import com.github.artemdevel.pixeldungeon.sprites.ItemSprite.Glowing;
import com.github.artemdevel.pixeldungeon.ui.BuffIndicator;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.utils.Utils;
import com.github.artemdevel.pixeldungeon.game.utils.Bundle;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Viscosity extends Glyph {

    private static final String TXT_VISCOSITY = "%s of viscosity";

    private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x8844CC);

    @Override
    public int process(Armor armor, Char attacker, Char defender, int damage) {
        if (damage == 0) {
            return 0;
        }

        int level = Math.max(0, armor.effectiveLevel());

        if (Random.Int(level + 7) >= 6) {
            DeferredDamage debuff = defender.buff(DeferredDamage.class);
            if (debuff == null) {
                debuff = new DeferredDamage();
                debuff.attachTo(defender);
            }
            debuff.prolong(damage);

            defender.sprite.showStatus(CharSprite.WARNING, "deferred %d", damage);

            return 0;
        } else {
            return damage;
        }
    }

    @Override
    public String name(String weaponName) {
        return String.format(TXT_VISCOSITY, weaponName);
    }

    @Override
    public Glowing glowing() {
        return PURPLE;
    }

    public static class DeferredDamage extends Buff {

        protected int damage = 0;

        private static final String DAMAGE = "damage";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DAMAGE, damage);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            damage = bundle.getInt(DAMAGE);
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                postpone(TICK);
                return true;
            } else {
                return false;
            }
        }

        public void prolong(int damage) {
            this.damage += damage;
        }

        @Override
        public int icon() {
            return BuffIndicator.DEFERRED;
        }

        @Override
        public String toString() {
            return Utils.format("Deferred damage (%d)", damage);
        }

        @Override
        public boolean act() {
            if (target.isAlive()) {
                target.damage(1, this);
                if (target == Dungeon.hero && !target.isAlive()) {
                    // FIXME
                    Dungeon.fail(Utils.format(ResultDescriptions.GLYPH, "enchantment of viscosity", Dungeon.depth));
                    GLog.n("The enchantment of viscosity killed you...");

                    Badges.validateDeathFromGlyph();
                }
                spend(TICK);

                if (--damage <= 0) {
                    detach();
                }
            } else {
                detach();
            }
            return true;
        }
    }
}
