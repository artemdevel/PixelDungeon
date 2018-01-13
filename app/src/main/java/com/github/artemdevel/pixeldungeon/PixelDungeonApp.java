package com.github.artemdevel.pixeldungeon;

import android.app.Application;

import com.github.artemdevel.pixeldungeon.game.common.audio.GameMusic;

public final class PixelDungeonApp extends Application {

    private Preferences preferences;
    private GameMusic gameMusic;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(getApplicationContext());
        gameMusic = new GameMusic(getApplicationContext());
    }

    public Preferences getPreferences() {
        if (preferences == null) {
            preferences = new Preferences(getApplicationContext());
        }
        return preferences;
    }

    public GameMusic getGameMusic() {
        if (gameMusic == null) {
            gameMusic = new GameMusic(getApplicationContext());
        }
        return gameMusic;
    }

}
