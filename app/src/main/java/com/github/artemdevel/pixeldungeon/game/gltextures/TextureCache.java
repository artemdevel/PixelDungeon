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

package com.github.artemdevel.pixeldungeon.game.gltextures;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.artemdevel.pixeldungeon.game.glwrap.Texture;

import static com.github.artemdevel.pixeldungeon.utils.Utils.reportException;

public final class TextureCache {

    private final Context context;
    private final HashMap<Object, SmartTexture> cache = new HashMap<>();
    private final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

    public TextureCache(Context context) {
        this.context = context;
        // No dithering, no scaling, 32 bits per pixel
        bitmapOptions.inScaled = false;
        bitmapOptions.inDither = false;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }

    public SmartTexture createSolid(int color) {
        final String key = "1x1:" + color;

        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bmp.eraseColor(color);
            SmartTexture texture = new SmartTexture(bmp);
            cache.put(key, texture);
            return texture;
        }
    }

    public void add(Object key, SmartTexture texture) {
        cache.put(key, texture);
    }

    public SmartTexture get(Object src) {
        if (cache.containsKey(src)) {
            return cache.get(src);
        } else if (src instanceof SmartTexture) {
            return (SmartTexture) src;
        } else {
            SmartTexture texture = new SmartTexture(getBitmap(src));
            cache.put(src, texture);
            return texture;
        }
    }

    public void clear() {
        for (Texture texture : cache.values()) {
            texture.delete();
        }
        cache.clear();
    }

    public void reload() {
        for (SmartTexture texture : cache.values()) {
            texture.reload();
        }
    }

    public boolean contains(Object key) {
        return cache.containsKey(key);
    }

    private Bitmap getBitmap(Object src) {
        try {
            if (src instanceof Integer) {
                return BitmapFactory.decodeResource(context.getResources(), (Integer) src, bitmapOptions);
            } else if (src instanceof String) {
                return BitmapFactory.decodeStream(context.getAssets().open((String) src), null, bitmapOptions);
            } else if (src instanceof Bitmap) {
                return (Bitmap) src;
            } else {
                return null;
            }
        } catch (Exception ex) {
            reportException(ex);
            return null;
        }
    }

}
