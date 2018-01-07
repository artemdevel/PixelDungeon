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
package com.github.artemdevel.pixeldungeon;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.github.artemdevel.pixeldungeon.game.common.Game;
import com.github.artemdevel.pixeldungeon.game.common.audio.Music;
import com.github.artemdevel.pixeldungeon.game.common.audio.Sample;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.scenes.PixelScene;
import com.github.artemdevel.pixeldungeon.scenes.TitleScene;

public class PixelDungeon extends Game {

    public PixelDungeon() {
        super(TitleScene.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        instance.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        boolean landscape = metrics.widthPixels > metrics.heightPixels;

        if (Preferences.INSTANCE.getBoolean(Preferences.KEY_LANDSCAPE, false) != landscape) {
            landscape(!landscape);
        }

        Music.INSTANCE.enable(music());
        Sample.INSTANCE.enable(soundFx());

        Sample.INSTANCE.load(
            Assets.SND_CLICK,
            Assets.SND_BADGE,
            Assets.SND_GOLD,
            Assets.SND_DESCEND,
            Assets.SND_STEP,
            Assets.SND_WATER,
            Assets.SND_OPEN,
            Assets.SND_UNLOCK,
            Assets.SND_ITEM,
            Assets.SND_DEWDROP,
            Assets.SND_HIT,
            Assets.SND_MISS,
            Assets.SND_EAT,
            Assets.SND_READ,
            Assets.SND_LULLABY,
            Assets.SND_DRINK,
            Assets.SND_SHATTER,
            Assets.SND_ZAP,
            Assets.SND_LIGHTNING,
            Assets.SND_LEVELUP,
            Assets.SND_DEATH,
            Assets.SND_CHALLENGE,
            Assets.SND_CURSED,
            Assets.SND_EVOKE,
            Assets.SND_TRAP,
            Assets.SND_TOMB,
            Assets.SND_ALERT,
            Assets.SND_MELD,
            Assets.SND_BOSS,
            Assets.SND_BLAST,
            Assets.SND_PLANT,
            Assets.SND_RAY,
            Assets.SND_BEACON,
            Assets.SND_TELEPORT,
            Assets.SND_CHARMS,
            Assets.SND_MASTERY,
            Assets.SND_PUFF,
            Assets.SND_ROCKS,
            Assets.SND_BURNING,
            Assets.SND_FALLING,
            Assets.SND_GHOST,
            Assets.SND_SECRET,
            Assets.SND_BONES,
            Assets.SND_BEE,
            Assets.SND_DEGRADE,
            Assets.SND_MIMIC);
    }

    public static void switchNoFade(Class<? extends PixelScene> c) {
        PixelScene.noFade = true;
        switchScene(c);
    }

    public static void landscape(boolean value) {
        Game.instance.setRequestedOrientation(value ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Preferences.INSTANCE.put(Preferences.KEY_LANDSCAPE, value);
    }

    public static boolean landscape() {
        return width > height;
    }

    public static boolean immersed() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_IMMERSIVE, false);
    }

    public static void scaleUp(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_SCALE_UP, value);
        switchScene(TitleScene.class);
    }

    public static boolean scaleUp() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_SCALE_UP, true);
    }

    public static void zoom(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_ZOOM, value);
    }

    public static int zoom() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_ZOOM, 0);
    }

    public static void music(boolean value) {
        Music.INSTANCE.enable(value);
        Preferences.INSTANCE.put(Preferences.KEY_MUSIC, value);
    }

    public static boolean music() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_MUSIC, true);
    }

    public static void soundFx(boolean value) {
        Sample.INSTANCE.enable(value);
        Preferences.INSTANCE.put(Preferences.KEY_SOUND_FX, value);
    }

    public static boolean soundFx() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_SOUND_FX, true);
    }

    public static void brightness(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_BRIGHTNESS, value);
        if (scene() instanceof GameScene) {
            ((GameScene) scene()).brightness(value);
        }
    }

    public static boolean brightness() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_BRIGHTNESS, false);
    }

    public static void lastClass(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_LAST_CLASS, value);
    }

    public static int lastClass() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_LAST_CLASS, 0);
    }

    public static void challenges(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_CHALLENGES, value);
    }

    public static int challenges() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_CHALLENGES, 0);
    }

    public static void intro(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_INTRO, value);
    }

    public static boolean intro() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_INTRO, true);
    }

}
