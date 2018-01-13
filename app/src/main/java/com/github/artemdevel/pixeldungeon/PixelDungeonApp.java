package com.github.artemdevel.pixeldungeon;

import android.app.Application;

import com.github.artemdevel.pixeldungeon.game.common.audio.GameMusic;
import com.github.artemdevel.pixeldungeon.game.common.audio.GameSound;

public final class PixelDungeonApp extends Application {

    private Preferences preferences;
    private GameMusic gameMusic;
    private GameSound gameSound;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(getApplicationContext());
        gameMusic = new GameMusic(getApplicationContext());
        gameSound = new GameSound(getApplicationContext());
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

    public GameSound getGameSound() {
        if (gameSound == null) {
            gameSound = new GameSound(getApplicationContext());
        }
        return gameSound;
    }

}
