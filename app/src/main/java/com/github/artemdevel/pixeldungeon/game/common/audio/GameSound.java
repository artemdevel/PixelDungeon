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

package com.github.artemdevel.pixeldungeon.game.common.audio;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;

import com.github.artemdevel.pixeldungeon.Assets;

public final class GameSound {

    private static final int MAX_STREAMS = 8;

    private final Context context;

    private SoundPool pool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
    private HashMap<Object, Integer> ids = new HashMap<>();
    private LinkedList<String> loadingQueue = new LinkedList<>(Arrays.asList(
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
            Assets.SND_MIMIC
    ));

    private boolean enabled = true;

    public GameSound(Context context) {
        this.context = context;
    }

    public void reset() {
        pool.release();
        pool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        ids.clear();
    }

    public void onPause() {
        if (pool != null) {
            pool.autoPause();
        }
    }

    public void onResume() {
        if (pool != null) {
            pool.autoResume();
        }
    }

    public void load(String... assets) {
        loadingQueue.addAll(Arrays.asList(assets));
        loadNext();
    }

    private void loadNext() {
        final String asset = loadingQueue.poll();
        if (asset != null) {
            if (!ids.containsKey(asset)) {
                try {
                    pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            loadNext();
                        }
                    });

                    AssetFileDescriptor afd = context.getAssets().openFd(asset);
                    int streamID = pool.load(afd, 1);
                    ids.put(asset, streamID);
                    afd.close();
                } catch (IOException ex) {
                    loadNext();
                } catch (NullPointerException ex) {
                    // Do nothing (stop loading sounds)
                }
            } else {
                loadNext();
            }
        }
    }

    public int play(Object id) {
        return play(id, 1, 1, 1);
    }

    public int play(Object id, float volume) {
        return play(id, volume, volume, 1);
    }

    public int play(Object id, float leftVolume, float rightVolume, float rate) {
        if (enabled && ids.containsKey(id)) {
            return pool.play(ids.get(id), leftVolume, rightVolume, 0, 0, rate);
        } else {
            return -1;
        }
    }

    public void enable(boolean value) {
        enabled = value;
    }
}
