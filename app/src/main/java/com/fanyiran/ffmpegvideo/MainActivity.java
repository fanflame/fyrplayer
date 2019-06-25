package com.fanyiran.ffmpegvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpegcaller");
    }
    private FFVideoPlayer ffVideoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ffVideoPlayer = ((FFVideoPlayer) findViewById(R.id.ffVideoPlayer));
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getVersion();

    public native void render(String url, SurfaceView surfaceView);

    public void onPlayClick(View view) {
        ffVideoPlayer.play("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
    }
}
