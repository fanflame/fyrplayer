package com.fanyiran.mediaplayer;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fanyiran.mediaplayer.fyrplayer.PlayerConfig;
import com.fanyiran.mediaplayer.fyrplayer.PlayerManager;
import com.fanyiran.mediaplayer.fyrplayer.VideoInfo;
import com.fanyiran.mediaplayer.fyrplayer.callback.OnPlayCallback;
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
        initView();
    }

    private void initView() {
        //todo videoview播放？
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
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath());
        File[] files = file.listFiles();
        if (files.length == 0) {
            return null;
        }
        PlayerConfig config = new PlayerConfig.PlayerConifgBuild()
                .surface(surface)
//                .frameRate(10)
//                .setPlayRepeatTime(3)
                .setUrl(files[0].getAbsolutePath())
                .setOnPlayCallback(new OnPlayCallback() {
                    @Override
                    public void onGetVideoInfo(final VideoInfo videoInfo) {
                        LogUtil.v(TAG, videoInfo.toString());
                        surfaceView.post(new Runnable() {
                            @Override
                            public void run() {
                                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
//                                layoutParams.width = videoInfo.getWidth();
//                                layoutParams.height = videoInfo.getHeight();
                                int width = surfaceView.getWidth();
                                int height = surfaceView.getHeight();
                                if (videoInfo.getWidth() < width && videoInfo.getHeight() < height) {
                                    if ((videoInfo.getWidth() / (videoInfo.getHeight() * 1.f)) > (width / (height * 1.f))) {
                                        layoutParams.width = width;
                                        layoutParams.height = (int) ((width / (videoInfo.getWidth() * 1.f)) * videoInfo.getHeight());
                                    }
                                }
                                surfaceView.setLayoutParams(layoutParams);
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorContent) {

                    }

                    @Override
                    public void onStart() {
                        LogUtil.v(TAG, "start");
                    }

                    @Override
                    public void onFinish() {
                        LogUtil.v(TAG, "finish");
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
