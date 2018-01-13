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

import com.github.artemdevel.pixeldungeon.game.common.Game;
import com.github.artemdevel.pixeldungeon.Assets;
import com.github.artemdevel.pixeldungeon.Dungeon;
import com.github.artemdevel.pixeldungeon.actors.Actor;
import com.github.artemdevel.pixeldungeon.actors.hero.Hero;
import com.github.artemdevel.pixeldungeon.items.wands.WandOfBlink;
import com.github.artemdevel.pixeldungeon.levels.Level;
import com.github.artemdevel.pixeldungeon.scenes.InterLevelScene;
import com.github.artemdevel.pixeldungeon.sprites.ItemSprite.Glowing;
import com.github.artemdevel.pixeldungeon.sprites.ItemSpriteSheet;
import com.github.artemdevel.pixeldungeon.utils.GLog;
import com.github.artemdevel.pixeldungeon.utils.Utils;
import com.github.artemdevel.pixeldungeon.game.utils.Bundle;

public class LloydsBeacon extends Item {

    private static final Glowing WHITE = new Glowing(0xFFFFFF);

    private static final String TXT_PREVENTING =
        "Strong magic aura of this place prevents you from using the lloyd's beacon!";

    private static final String TXT_CREATURES =
        "Psychic aura of neighbouring creatures doesn't allow you to use the lloyd's beacon at this moment.";

    private static final String TXT_RETURN =
        "The lloyd's beacon is successfully set at your current location, now you can return here anytime.";

    private static final String TXT_INFO =
        "Lloyd's beacon is an intricate magic device, that allows you to return to a place you have already been.";

    private static final String TXT_SET =
        "\n\nThis beacon was set somewhere on the level %d of Pixel Dungeon.";

    public static final float TIME_TO_USE = 1;

    public static final String AC_SET = "SET";
    public static final String AC_RETURN = "RETURN";

    private int returnDepth = -1;
    private int returnPos;

    {
        name = "lloyd's beacon";
        image = ItemSpriteSheet.BEACON;

        unique = true;
    }

    private static final String DEPTH = "depth";
    private static final String POS = "pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, returnDepth);
        if (returnDepth != -1) {
            bundle.put(POS, returnPos);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        returnDepth = bundle.getInt(DEPTH);
        returnPos = bundle.getInt(POS);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SET);
        if (returnDepth != -1) {
            actions.add(AC_RETURN);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_SET) || action.equals(AC_RETURN)) {
            if (Dungeon.bossLevel()) {
                hero.spend(LloydsBeacon.TIME_TO_USE);
                GLog.logWarning(TXT_PREVENTING);
                return;
            }

            for (int i = 0; i < Level.NEIGHBOURS8.length; i++) {
                if (Actor.findChar(hero.pos + Level.NEIGHBOURS8[i]) != null) {
                    GLog.logWarning(TXT_CREATURES);
                    return;
                }
            }
        }

        if (action.equals(AC_SET)) {
            returnDepth = Dungeon.depth;
            returnPos = hero.pos;

            hero.spend(LloydsBeacon.TIME_TO_USE);
            hero.busy();

            hero.sprite.operate(hero.pos);
            Game.sound.play(Assets.SND_BEACON);

            GLog.logInfo(TXT_RETURN);
        } else if (action.equals(AC_RETURN)) {
            if (returnDepth == Dungeon.depth) {
                reset();
                WandOfBlink.appear(hero, returnPos);
                Dungeon.level.press(returnPos, hero);
                Dungeon.observe();
            } else {
                InterLevelScene.mode = InterLevelScene.Mode.RETURN;
                InterLevelScene.returnDepth = returnDepth;
                InterLevelScene.returnPos = returnPos;
                reset();
                Game.switchScene(InterLevelScene.class);
            }
        } else {
            super.execute(hero, action);
        }
    }

    public void reset() {
        returnDepth = -1;
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
    public Glowing glowing() {
        return returnDepth != -1 ? WHITE : null;
    }

    @Override
    public String info() {
        return TXT_INFO + (returnDepth == -1 ? "" : Utils.format(TXT_SET, returnDepth));
    }
}
