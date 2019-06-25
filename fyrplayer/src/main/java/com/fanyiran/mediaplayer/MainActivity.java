package com.fanyiran.mediaplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.fanyiran.mediaplayer.fyrplayer.PlayerManager;
import com.fanyiran.mediaplayer.fyrplayer.callback.OnPlayCallback;
import com.fanyiran.mediaplayer.fyrplayer.PlayerConfig;
import com.fanyiran.mediaplayer.fyrplayer.VideoInfo;
import com.fanyiran.utils.LogUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 556;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            initView();
        }
    }

    private void initView() {
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                PlayerManager.getInstance().createFyrPlayer();
                PlayerManager.getInstance().setPlayerConfig(getPlayConfig(holder.getSurface()));
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private PlayerConfig getPlayConfig(Surface surface) {
        File file = new File(Environment.getExternalStorageDirectory(), "1.mp4");
        PlayerConfig config = new PlayerConfig.PlayerConifgBuild()
                .surface(surface)
                .frameRate(10)
                .setPlayRepeatTime(3)
                .setUrl(file.getAbsolutePath())
                .setOnPlayCallback(new OnPlayCallback() {
                    @Override
                    public void onGetVideoInfo(VideoInfo videoInfo) {
                        LogUtil.v(TAG,videoInfo.toString());
                    }

                    @Override
                    public void onError(int error, String errorContent) {

                    }

                    @Override
                    public void onStart() {
                        LogUtil.v(TAG,"start");
                    }

                    @Override
                    public void onFinish() {
                        LogUtil.v(TAG,"finish");
                    }
                })
                .build();
        return config;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            int length = grantResults.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "请赋予权限", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initView();
        }
    }

    public void onPlayClick(View view) {
        PlayerManager.getInstance().play();
    }

    public void onReplayClick(View view) {
        // FIXME: 2019-06-25 重新播放有问题。。
        if (!PlayerManager.getInstance().play()) {
            PlayerManager.getInstance().release();
            PlayerManager.getInstance().createFyrPlayer();
            PlayerManager.getInstance().setPlayerConfig(getPlayConfig(surfaceView.getHolder().getSurface()));
        }
        PlayerManager.getInstance().play();
    }

    public void onResumeClick(View view) {
        PlayerManager.getInstance().resume();
    }

    public void onPauseClick(View view) {
        PlayerManager.getInstance().pause();
    }
}
