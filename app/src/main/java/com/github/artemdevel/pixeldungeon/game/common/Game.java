/*
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

package com.github.artemdevel.pixeldungeon.game.common;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.github.artemdevel.pixeldungeon.BuildConfig;
import com.github.artemdevel.pixeldungeon.game.common.audio.GameMusic;
import com.github.artemdevel.pixeldungeon.game.glscripts.Script;
import com.github.artemdevel.pixeldungeon.game.gltextures.TextureCache;
import com.github.artemdevel.pixeldungeon.game.input.Keys;
import com.github.artemdevel.pixeldungeon.game.input.Touchscreen;
import com.github.artemdevel.pixeldungeon.game.common.audio.GameSound;
import com.github.artemdevel.pixeldungeon.game.utils.BitmapCache;
import com.github.artemdevel.pixeldungeon.game.utils.SystemTime;
import com.github.artemdevel.pixeldungeon.scenes.PixelScene;

import android.app.Activity;
import android.media.AudioManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

// Global game state
public class Game extends Activity implements GLSurfaceView.Renderer, View.OnTouchListener {

    public static Game instance;

    // Actual size of the screen
    public static int width;
    public static int height;

    // Density: mdpi=1, hdpi=1.5, xhdpi=2...
    public static float density = 1; // affects zoom

    public static String version = BuildConfig.VERSION_NAME;

    // Current scene
    protected Scene scene;
    // New scene we are going to switch to
    protected Scene requestedScene;
    // true if scene switch is requested
    protected boolean requestedReset = true;
    // New scene class
    protected Class<? extends Scene> sceneClass;

    // Current time in milliseconds
    protected long now;
    // Milliseconds passed since previous update
    protected long step;

    public static float timeScale = 1f;
    public static float elapsed = 0f;

    protected GLSurfaceView view;

    // Accumulated touch events
    protected final ArrayList<MotionEvent> motionEvents = new ArrayList<>();

    // Accumulated key events
    protected final ArrayList<KeyEvent> keysEvents = new ArrayList<>();

    public Game(Class<? extends Scene> scene) {
        super();
        sceneClass = scene;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BitmapCache.context = this;
        TextureCache.context = this;
        instance = this;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Google doesn't recommend this in its docs
        // https://developer.android.com/training/graphics/opengl/environment.html
        // "... you may be tempted to skip extending it and just create an unmodified GLSurfaceView instance, but don’t do that."
        // TODO: Create a thin wrapper around GLSurfaceView
        view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);
        view.setEGLConfigChooser(false);
        view.setRenderer(this);
        view.setOnTouchListener(this);
        setContentView(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        now = 0;
        view.onResume();

        GameMusic.INSTANCE.resume();
        GameSound.INSTANCE.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (scene != null) {
            scene.pause();
        }

        view.onPause();
        Script.reset();

        GameMusic.INSTANCE.pause();
        GameSound.INSTANCE.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyGame();

        GameMusic.INSTANCE.mute();
        GameSound.INSTANCE.reset();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        synchronized (motionEvents) {
            motionEvents.add(MotionEvent.obtain(event));
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == Keys.VOLUME_DOWN || keyCode == Keys.VOLUME_UP) {
            return false;
        }

        synchronized (motionEvents) {
            keysEvents.add(event);
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == Keys.VOLUME_DOWN || keyCode == Keys.VOLUME_UP) {
            return false;
        }

        synchronized (motionEvents) {
            keysEvents.add(event);
        }
        return true;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // The main game loop
        if (width == 0 || height == 0) {
            return;
        }

        SystemTime.tick();
        long rightNow = SystemTime.now;
        step = (now == 0 ? 0 : rightNow - now);
        now = rightNow;

        step();

        GameScript.get().resetCamera();
        GLES20.glScissor(0, 0, width, height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        draw();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Game.width = width;
        Game.height = height;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glEnable(GL10.GL_BLEND);
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GL10.GL_SCISSOR_TEST);
        // To avoid EGL Context Lost
        TextureCache.reload();
    }

    protected void destroyGame() {
        if (scene != null) {
            scene.destroy();
            scene = null;
        }

        instance = null;
    }

    public static void switchScene(Class<? extends Scene> scene) {
        instance.sceneClass = scene;
        instance.requestedReset = true;
    }

    public static void switchSceneNoFade(Class<? extends PixelScene> c) {
        PixelScene.noFade = true;
        switchScene(c);
    }

    public static Scene scene() {
        return instance.scene;
    }

    protected void step() {
        if (requestedReset) {
            requestedReset = false;
            try {
                requestedScene = sceneClass.newInstance();
                switchScene();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        update();
    }

    protected void draw() {
        scene.draw();
    }

    protected void switchScene() {
        Camera.reset();

        if (scene != null) {
            scene.destroy();
        }
        scene = requestedScene;
        scene.create();

        Game.elapsed = 0f;
        Game.timeScale = 1f;
    }

    protected void update() {
        Game.elapsed = Game.timeScale * step * 0.001f;

        synchronized (motionEvents) {
            Touchscreen.processTouchEvents(motionEvents);
            motionEvents.clear();
        }
        synchronized (keysEvents) {
            Keys.processTouchEvents(keysEvents);
            keysEvents.clear();
        }

        scene.update();
        Camera.updateAll();
    }

}
