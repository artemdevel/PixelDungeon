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

import com.github.artemdevel.pixeldungeon.game.glwrap.Matrix;
import com.github.artemdevel.pixeldungeon.game.utils.Point;
import com.github.artemdevel.pixeldungeon.game.utils.PointF;
import com.github.artemdevel.pixeldungeon.game.utils.Random;

public class Camera {

    protected static ArrayList<Camera> all = new ArrayList<>();

    protected static float invW2;
    protected static float invH2;

    public static Camera main;

    public float zoom;

    public int x;
    public int y;
    public int width;
    public int height;

    int screenWidth;
    int screenHeight;

    public float[] matrix;

    public PointF scroll;
    public Visual target;

    public boolean visible;

    private float shakeMagX = 10f;
    private float shakeMagY = 10f;
    private float shakeTime = 0f;
    private float shakeDuration = 1f;

    protected float shakeX;
    protected float shakeY;

    public static Camera reset() {
        return reset(createFullscreen(1));
    }

    public static Camera reset(Camera newCamera) {
        invW2 = 2f / Game.width;
        invH2 = 2f / Game.height;

        for (Camera camera : all) {
            camera.destroy();
        }
        all.clear();
        add(newCamera);
        main = newCamera;

        return main;
    }

    public static void add(Camera camera) {
        all.add(camera);
    }

    public static Camera remove(Camera camera) {
        all.remove(camera);
        return camera;
    }

    public static void updateAll() {
        for (Camera camera : all) {
            camera.update();
        }
    }

    public static Camera createFullscreen(float zoom) {
        int w = (int) Math.ceil(Game.width / zoom);
        int h = (int) Math.ceil(Game.height / zoom);
        return new Camera(
                (int) (Game.width - w * zoom) / 2,
                (int) (Game.height - h * zoom) / 2,
                w, h, zoom);
    }

    public Camera(int x, int y, int width, int height, float zoom) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zoom = zoom;

        visible = true;

        screenWidth = (int) (width * zoom);
        screenHeight = (int) (height * zoom);

        scroll = new PointF();

        matrix = new float[16];
        Matrix.setIdentity(matrix);
    }

    public void destroy() {
        target = null;
        matrix = null;
        visible = false;
    }

    public void zoom(float value) {
        zoom(value, scroll.x + width / 2, scroll.y + height / 2);
    }

    public void zoom(float value, float fx, float fy) {
        zoom = value;
        width = (int) (screenWidth / zoom);
        height = (int) (screenHeight / zoom);

        focusOn(fx, fy);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        screenWidth = (int) (width * zoom);
        screenHeight = (int) (height * zoom);
    }

    public void update() {
        if (target != null) {
            focusOn(target);
        }

        if ((shakeTime -= Game.elapsed) > 0) {
            float damping = shakeTime / shakeDuration;
            shakeX = Random.Float(-shakeMagX, +shakeMagX) * damping;
            shakeY = Random.Float(-shakeMagY, +shakeMagY) * damping;
        } else {
            shakeX = 0;
            shakeY = 0;
        }

        updateMatrix();
    }

    public PointF center() {
        return new PointF(width / 2, height / 2);
    }

    public void focusOn(float x, float y) {
        scroll.set(x - width / 2, y - height / 2);
    }

    public void focusOn(PointF point) {
        focusOn(point.x, point.y);
    }

    public void focusOn(Visual visual) {
        focusOn(visual.center());
    }

    public PointF screenToCamera(int x, int y) {
        return new PointF((x - this.x) / zoom + scroll.x, (y - this.y) / zoom + scroll.y);
    }

    public Point cameraToScreen(float x, float y) {
        return new Point((int) ((x - scroll.x) * zoom + this.x), (int) ((y - scroll.y) * zoom + this.y));
    }

    public float screenWidth() {
        return width * zoom;
    }

    public float screenHeight() {
        return height * zoom;
    }

    protected void updateMatrix() {
//        Matrix.setIdentity(matrix);
//        Matrix.translate(matrix, -1, +1);
//        Matrix.scale(matrix, 2f / G.width, -2f / G.height);
//        Matrix.translate(matrix, x, y);
//        Matrix.scale(matrix, setZoom, setZoom);
//        Matrix.translate(matrix, scroll.x, scroll.y);

        matrix[0] = +zoom * invW2;
        matrix[5] = -zoom * invH2;

        matrix[12] = -1 + x * invW2 - (scroll.x + shakeX) * matrix[0];
        matrix[13] = +1 - y * invH2 - (scroll.y + shakeY) * matrix[5];
    }

    public void shake(float magnitude, float duration) {
        shakeMagX = shakeMagY = magnitude;
        shakeTime = shakeDuration = duration;
    }
}
