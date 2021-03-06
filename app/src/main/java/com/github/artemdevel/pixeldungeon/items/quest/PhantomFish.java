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
package com.github.artemdevel.pixeldungeon.items.quest;

import java.util.ArrayList;

import com.github.artemdevel.pixeldungeon.game.common.Game;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.actors.buffs.Buff;
import com.github.artemdevel.pixeldungeon.actors.buffs.Invisibility;
import com.github.artemdevel.pixeldungeon.actors.hero.Hero;
import com.github.artemdevel.pixeldungeon.items.Item;
import com.github.artemdevel.pixeldungeon.sprites.ItemSpriteSheet;
import com.github.artemdevel.pixeldungeon.utils.GLog;

public class PhantomFish extends Item {

    private static final String AC_EAT = "EAT";

    private static final float TIME_TO_EAT = 2f;

    {
        name = "phantom fish";
        image = ItemSpriteSheet.PHANTOM;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_EAT);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        if (action.equals(AC_EAT)) {
            detach(hero.belongings.backpack);

            hero.sprite.operate(hero.pos);
            hero.busy();
            Game.sound.play(Assets.SND_EAT);
            Game.sound.play(Assets.SND_MELD);

            GLog.logInfo("You see your hands turn invisible!");
            Buff.affect(hero, Invisibility.class, Invisibility.DURATION);

            hero.spend(TIME_TO_EAT);
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

    @Override
    public String info() {
        return
            "You can barely see this tiny translucent fish in the air. " +
            "In the water it becomes effectively invisible.";
    }
}
