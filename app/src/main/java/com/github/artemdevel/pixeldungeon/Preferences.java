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

import com.github.artemdevel.pixeldungeon.game.common.Game;
import com.github.artemdevel.pixeldungeon.game.common.audio.GameMusic;
import com.github.artemdevel.pixeldungeon.game.common.audio.GameSound;
import com.github.artemdevel.pixeldungeon.scenes.GameScene;
import com.github.artemdevel.pixeldungeon.scenes.TitleScene;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

public enum Preferences {

    INSTANCE;

    public static final String KEY_LANDSCAPE = "setLandscape";
    public static final String KEY_IMMERSIVE = "immersive";
    public static final String KEY_SCALE_UP = "scaleup";
    public static final String KEY_MUSIC = "setMusic";
    public static final String KEY_SOUND_FX = "soundfx";
    public static final String KEY_ZOOM = "setZoom";
    public static final String KEY_LAST_CLASS = "last_class";
    public static final String KEY_CHALLENGES = "setChallenges";
    public static final String KEY_INTRO = "setIntro";
    public static final String KEY_BRIGHTNESS = "setBrightness";

    private SharedPreferences prefs;

    public static void setZoom(int value) {
        INSTANCE.put(KEY_ZOOM, value);
    }

    public static int getZoom() {
        return INSTANCE.getInt(KEY_ZOOM, 0);
    }

    public static void setScaleUp(boolean value) {
        // TODO: Find out what it actually does
        INSTANCE.put(KEY_SCALE_UP, value);
        Game.switchScene(TitleScene.class);
    }

    public static boolean getScaleUp() {
        return INSTANCE.getBoolean(KEY_SCALE_UP, true);
    }

    public static boolean getImmersed() {
        return INSTANCE.getBoolean(KEY_IMMERSIVE, false);
    }

    public static void setMusic(boolean value) {
        GameMusic.INSTANCE.enable(value);
        INSTANCE.put(KEY_MUSIC, value);
    }

    public static boolean getMusic() {
        return INSTANCE.getBoolean(KEY_MUSIC, true);
    }

    public static void setSoundFx(boolean value) {
        GameSound.INSTANCE.enable(value);
        INSTANCE.put(KEY_SOUND_FX, value);
    }

    public static boolean getSoundFx() {
        return INSTANCE.getBoolean(KEY_SOUND_FX, true);
    }

    public static void setBrightness(boolean value) {
        INSTANCE.put(KEY_BRIGHTNESS, value);
        if (Game.scene() instanceof GameScene) {
            ((GameScene) Game.scene()).brightness(value);
        }
    }

    public static boolean getBrightness() {
        return INSTANCE.getBoolean(KEY_BRIGHTNESS, false);
    }

    public static void setLastClass(int value) {
        INSTANCE.put(KEY_LAST_CLASS, value);
    }

    public static int getLastClass() {
        return INSTANCE.getInt(KEY_LAST_CLASS, 0);
    }

    public static void setChallenges(int value) {
        INSTANCE.put(KEY_CHALLENGES, value);
    }

    public static int getChallenges() {
        return INSTANCE.getInt(KEY_CHALLENGES, 0);
    }

    public static void setIntro(boolean value) {
        INSTANCE.put(KEY_INTRO, value);
    }

    public static boolean getIntro() {
        return INSTANCE.getBoolean(KEY_INTRO, true);
    }

    public static void setLandscape(boolean value) {
        // TODO: The whole landscape feature must be refactored somehow
        Game.instance.setRequestedOrientation(value ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        INSTANCE.put(KEY_LANDSCAPE, value);
    }

    public static boolean getLandscape() {
        boolean value = INSTANCE.getBoolean(Preferences.KEY_LANDSCAPE, false);
        boolean landscape = Game.width > Game.height;
        if (value != landscape) {
            setLandscape(value);
        }
        return value;
    }

    private SharedPreferences get() {
        if (prefs == null) {
            prefs = Game.instance.getPreferences(Game.MODE_PRIVATE);
        }
        return prefs;
    }

    int getInt(String key, int defValue) {
        return get().getInt(key, defValue);
    }

    boolean getBoolean(String key, boolean defValue) {
        return get().getBoolean(key, defValue);
    }

    void put(String key, int value) {
        get().edit().putInt(key, value).apply();
    }

    void put(String key, boolean value) {
        get().edit().putBoolean(key, value).apply();
    }

}
