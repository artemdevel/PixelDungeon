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

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

public final class GameMusic implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private final Context context;

    private MediaPlayer player;
    private String lastPlayed;
    private boolean lastLooping;
    private boolean enabled = true;

    public GameMusic(Context context) {
        this.context = context;
    }

    public void play(String assetName, boolean looping) {
        if (isPlaying() && lastPlayed.equals(assetName)) {
            return;
        }

        stop();
        lastPlayed = assetName;
        lastLooping = looping;
        if (!enabled || assetName == null) {
            return;
        }

        try {
            AssetFileDescriptor afd = context.getAssets().openFd(assetName);
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            player.setLooping(looping);
            player.prepareAsync();
        } catch (IOException e) {
            player.release();
            player = null;
        }
    }

    public void mute() {
        lastPlayed = null;
        stop();
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (player != null) {
            player.release();
            player = null;
        }
        return true;
    }

    public void onPause() {
        if (player != null) {
            player.pause();
        }
    }

    public void onResume() {
        if (player != null) {
            player.start();
        }
    }

    public void volume(float value) {
        if (player != null) {
            player.setVolume(value, value);
        }
    }

    public void enable(boolean value) {
        enabled = value;
        if (isPlaying() && !value) {
            stop();
        } else if (!isPlaying() && value) {
            play(lastPlayed, lastLooping);
        }
    }

    private boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    private void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

}
