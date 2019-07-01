package com.fanyiran.fyrrecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Rational;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fanyiran.fyrrecorder.camera.CameraConfig;
import com.fanyiran.fyrrecorder.camera.RecorderManager;
import com.fanyiran.fyrrecorder.camera.ICamera;
import com.fanyiran.utils.LogUtil;
import com.fanyiran.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 458;
    private static final String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private ICamera camera;
    private String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.textureView);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions,REQUEST_CODE);
            return;
        }
        surfaceView.getHolder().addCallback(holderCallBack);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: 2019-07-01 stoppreview
    }

    private SurfaceHolder.Callback holderCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.v(TAG,"surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.v(TAG,"surfaceChanged");
            preview(holder,width,height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.v(TAG,"surfaceDestroyed");
            if (camera != null) {
                camera.release();
            }
        }
    };

    private void preview(SurfaceHolder holder,int width,int height) {
        ArrayList<SurfaceHolder> surfaces = new ArrayList<>();
        surfaces.add(holder);
        CameraConfig cameraConfig = new CameraConfig.CameraConfigBuilder()
                .setContext(this)
                .setSurfaces(surfaces)
                .setTargetResolution(new Size(640,640))
                .setOutputFile(new File(Environment.getExternalStorageDirectory()+"/fyrvideo/",System.currentTimeMillis()+".mp4"))
                .setTargetAspectRatio(new Rational(1,1))
                .build();
        camera = RecorderManager.getInstance().createCamera(cameraConfig);
        camera.preview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            int length = grantResults.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showText(this,"cameraFactory not permissed");
                    return;
                }
            }
        }
    }

    public void onRecordClick(View view) {
        camera.startRecord();
    }

    public void onStopClick(View view) {
        camera.stopRecord();
    }

    public void onSwitchCameraClick(View view) {
        camera.switchCamera();
    }
}
