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
package com.github.artemdevel.pixeldungeon.items;

import java.util.ArrayList;

import com.github.artemdevel.pixeldungeon.game.common.audio.GameSound;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.actors.hero.Hero;
import com.github.artemdevel.pixeldungeon.effects.Speck;
import com.github.artemdevel.pixeldungeon.effects.particles.ShaftParticle;
import com.github.artemdevel.pixeldungeon.sprites.CharSprite;
import com.github.artemdevel.pixeldungeon.sprites.ItemSprite.Glowing;
import com.github.artemdevel.pixeldungeon.sprites.ItemSpriteSheet;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.utils.Utils;
import com.github.artemdevel.pixeldungeon.game.utils.Bundle;

public class DewVial extends Item {

    private static final int MAX_VOLUME = 10;

    private static final String AC_DRINK = "DRINK";

    private static final float TIME_TO_DRINK = 1f;

    private static final String TXT_VALUE = "%+dHP";
    private static final String TXT_STATUS = "%d/%d";

    private static final String TXT_AUTO_DRINK = "The dew vial was emptied to heal your wounds.";
    private static final String TXT_COLLECTED = "You collected a dewdrop into your dew vial.";
    private static final String TXT_FULL = "Your dew vial is full!";
    private static final String TXT_EMPTY = "Your dew vial is empty!";

    private static final double NUM = 20;
    private static final double POW = Math.log10(NUM);

    private static final Glowing WHITE = new Glowing(0xFFFFCC);

    {
        name = "dew vial";
        image = ItemSpriteSheet.VIAL;

        defaultAction = AC_DRINK;

        unique = true;
    }

    private int volume = 0;

    private static final String VOLUME = "volume";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(VOLUME, volume);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        volume = bundle.getInt(VOLUME);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (volume > 0) {
            actions.add(AC_DRINK);
        }
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        if (action.equals(AC_DRINK)) {
            if (volume > 0) {
                int value = (int) Math.ceil(Math.pow(volume, POW) / NUM * hero.HT);
                int effect = Math.min(hero.HT - hero.HP, value);
                if (effect > 0) {
                    hero.HP += effect;
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), volume > 5 ? 2 : 1);
                    hero.sprite.showStatus(CharSprite.POSITIVE, TXT_VALUE, effect);
                }

                volume = 0;

                hero.spend(TIME_TO_DRINK);
                hero.busy();

                GameSound.INSTANCE.play(Assets.SND_DRINK);
                hero.sprite.operate(hero.pos);

                updateQuickSlot();
            } else {
                GLog.logWarning(TXT_EMPTY);
            }
        } else {
            super.execute(hero, action);
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public boolean isFull() {
        return volume >= MAX_VOLUME;
    }

    public void collectDew(Dewdrop dew) {
        GLog.logInfo(TXT_COLLECTED);
        volume += dew.quantity;
        if (volume >= MAX_VOLUME) {
            volume = MAX_VOLUME;
            GLog.logPositive(TXT_FULL);
        }

        updateQuickSlot();
    }

    public void fill() {
        volume = MAX_VOLUME;
        updateQuickSlot();
    }

    public static void autoDrink(Hero hero) {
        DewVial vial = hero.belongings.getItem(DewVial.class);
        if (vial != null && vial.isFull()) {
            vial.execute(hero);
            hero.sprite.emitter().start(ShaftParticle.FACTORY, 0.2f, 3);

            GLog.logWarning(TXT_AUTO_DRINK);
        }
    }

    @Override
    public Glowing glowing() {
        return isFull() ? WHITE : null;
    }

    @Override
    public String status() {
        return Utils.format(TXT_STATUS, volume, MAX_VOLUME);
    }

    @Override
    public String info() {
        return "You can store excess dew in this tiny vessel for drinking it later. " +
            "If the vial is full, in a moment of deadly peril the dew will be " +
            "consumed automatically.";
    }

    @Override
    public String toString() {
        return super.toString() + " (" + status() + ")";
    }
}
