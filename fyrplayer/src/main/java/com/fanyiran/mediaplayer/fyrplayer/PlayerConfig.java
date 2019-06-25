package com.fanyiran.mediaplayer.fyrplayer;

import android.view.Surface;

import com.fanyiran.mediaplayer.fyrplayer.callback.OnPlayCallback;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerConfig {
    public static final int REPEATE_INFINITE = -1;
    private PlayerConifgBuild build;

    private PlayerConfig() {

    }

    private PlayerConfig(PlayerConifgBuild playerConifgBuild) {
        this.build = playerConifgBuild;
    }

    public Surface getSurface() {
        return build.surface;
    }

    public String getUrl() {
        return build.url;
    }

    public Executor getExecutor() {
        return build.executor;
    }

    public int getPlayRepeatTime() {
        return build.playRepeatTime;
    }

    public OnPlayCallback getOnPlayCallback() {
        return build.onPlayCallback;
    }

    public int getFrameRate() {
        return build.frameRate;
    }

    public static class PlayerConifgBuild {
        private Surface surface;
        private String url;
        private Executor executor;
        private int playRepeatTime = 0;
        private OnPlayCallback onPlayCallback;
        private int frameRate;

        public PlayerConifgBuild() {
            executor = Executors.newSingleThreadExecutor();
        }

        public PlayerConifgBuild surface(Surface surface) {
            this.surface = surface;
            return this;
        }

        public PlayerConfig build() {
            return new PlayerConfig(this);
        }

        public PlayerConifgBuild setUrl(String url) {
            this.url = url;
            return this;
        }

        public PlayerConifgBuild setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public PlayerConifgBuild setPlayRepeatTime(int playRepeatTime) {
            this.playRepeatTime = playRepeatTime;
            return this;
        }

        public PlayerConifgBuild setOnPlayCallback(OnPlayCallback onPlayCallback) {
            this.onPlayCallback = onPlayCallback;
            return this;
        }

        public PlayerConifgBuild frameRate(int frameRate) {
            this.frameRate = frameRate;
            return this;
        }
    }


}
