package com.github.artemdevel.pixeldungeon;

import android.app.Application;

public final class PixelDungeonApp extends Application {

    private Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(getApplicationContext());
    }

    public Preferences getPreferences() {
        if (preferences == null) {
            preferences = new Preferences(getApplicationContext());
        }
        return preferences;
    }

}
