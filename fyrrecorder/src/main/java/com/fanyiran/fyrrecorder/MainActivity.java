package com.fanyiran.fyrrecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fanyiran.fyrrecorder.camera.CameraConfig;
import com.fanyiran.fyrrecorder.camera.ICameraListener;
import com.fanyiran.fyrrecorder.recorderview.IRecorderView;
import com.fanyiran.utils.ToastUtils;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final int WHAT_PREVIEW_FPS = 1;
    private static final int REQUEST_CODE = 458;
    private static final String TAG = "MainActivity";
    private TextView tvPreviewFps;
    private IRecorderView cameraView;
    private String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.textureView);
        tvPreviewFps = findViewById(R.id.tvPreviewFps);
        handler = new Handler(callback);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions,REQUEST_CODE);
            return;
        }
        preview();
    }

    Handler.Callback callback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_PREVIEW_FPS:
                    tvPreviewFps.setText(String.format("preview fps:%d",cameraView.getPreviewFps()));
                    handler.sendEmptyMessageDelayed(WHAT_PREVIEW_FPS,1000);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: 2019-07-01 stoppreview
    }

    private void preview() {
        CameraConfig cameraConfig = new CameraConfig.CameraConfigBuilder()
                .setContext(this)
                .setTargetResolution(new Size(640,640))
                .setOutputFile(new File(Environment.getExternalStorageDirectory()+"/fyrvideo/",System.currentTimeMillis()+".mp4"))
                .setTargetAspectRatio(new Rational(1,1))
                .setiCameraListener(new ICameraListener() {
                    @Override
                    public void onCameraOpen() {

                    }

                    @Override
                    public void onCameraDisconnected() {

                    }

                    @Override
                    public void onCameraError(int error) {

                    }
                })
                .build();
        cameraView.autoPreview(cameraConfig);
        handler.sendEmptyMessage(WHAT_PREVIEW_FPS);
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
            preview();
        }
    }

    public void onRecordClick(View view) {
        cameraView.startRecord();
    }

    public void onStopClick(View view) {
        cameraView.stopRecord();
        cameraView.release();
    }

    public void onSwitchCameraClick(View view) {
        cameraView.switchCamera();
    }
}
