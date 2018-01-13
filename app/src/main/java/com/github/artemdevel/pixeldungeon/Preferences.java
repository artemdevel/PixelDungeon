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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

public final class Preferences {

    private static final String PREFS_NAME = "PREFS";
    private static final String KEY_LANDSCAPE = "setLandscape";
    private static final String KEY_IMMERSIVE = "immersive";
    private static final String KEY_SCALE_UP = "scaleup";
    private static final String KEY_MUSIC = "setMusic";
    private static final String KEY_SOUND_FX = "soundfx";
    private static final String KEY_ZOOM = "setZoom";
    private static final String KEY_LAST_CLASS = "last_class";
    private static final String KEY_CHALLENGES = "setChallenges";
    private static final String KEY_INTRO = "setIntro";
    private static final String KEY_BRIGHTNESS = "setBrightness";

    private SharedPreferences prefs;

    Preferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getZoom() {
        return getInt(KEY_ZOOM, 0);
    }

    public void setZoom(int value) {
        put(KEY_ZOOM, value);
    }

    public boolean getScaleUp() {
        return getBoolean(KEY_SCALE_UP, true);
    }

    public void setScaleUp(boolean value) {
        put(KEY_SCALE_UP, value);
        Game.switchScene(TitleScene.class);
    }

    public boolean getImmersed() {
        return getBoolean(KEY_IMMERSIVE, false);
    }

    public boolean getMusic() {
        return getBoolean(KEY_MUSIC, true);
    }

    public void setMusic(boolean value) {
        put(KEY_MUSIC, value);
        GameMusic.INSTANCE.enable(value);
    }

    public boolean getSoundFx() {
        return getBoolean(KEY_SOUND_FX, true);
    }

    public void setSoundFx(boolean value) {
        put(KEY_SOUND_FX, value);
        GameSound.INSTANCE.enable(value);
    }

    public boolean getBrightness() {
        return getBoolean(KEY_BRIGHTNESS, false);
    }

    public void setBrightness(boolean value) {
        put(KEY_BRIGHTNESS, value);
        if (Game.scene() instanceof GameScene) {
            ((GameScene) Game.scene()).brightness(value);
        }
    }

    public int getLastClass() {
        return getInt(KEY_LAST_CLASS, 0);
    }

    public void setLastClass(int value) {
        put(KEY_LAST_CLASS, value);
    }

    public int getChallenges() {
        return getInt(KEY_CHALLENGES, 0);
    }

    public void setChallenges(int value) {
        put(KEY_CHALLENGES, value);
    }

    public boolean getIntro() {
        return getBoolean(KEY_INTRO, true);
    }

    public void setIntro(boolean value) {
        put(KEY_INTRO, value);
    }

    public boolean getLandscape() {
        boolean value = getBoolean(KEY_LANDSCAPE, false);
        boolean landscape = Game.width > Game.height;
        if (value != landscape) {
            setLandscape(value);
        }
        return value;
    }

    public void setLandscape(boolean value) {
        // TODO: The whole landscape feature must be refactored somehow
        put(KEY_LANDSCAPE, value);
        Game.instance.setRequestedOrientation(value ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private int getInt(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    private boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    private void put(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    private void put(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

}
