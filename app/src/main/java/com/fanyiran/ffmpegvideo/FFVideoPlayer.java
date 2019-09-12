package com.fanyiran.ffmpegvideo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceView;

import com.fanyiran.ffmpegvideo.utils.DefaultExecutor;

import java.io.File;
import java.util.concurrent.Executor;

public class FFVideoPlayer extends SurfaceView {
    public FFVideoPlayer(Context context) {
        super(context);
        init();
    }

    public FFVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().setFormat(PixelFormat.RGBA_8888);
    }

    public void play(final String url) {
        play(url, DefaultExecutor.getDefaultExecutor());
    }

    public void play(final String url, Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor is null");
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                render(url, FFVideoPlayer.this.getHolder().getSurface());
            }
        });
    }

    public void stop() {

    }

    native public void render(String url, Surface surface);
}
