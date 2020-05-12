package com.fanyiran.fcamera.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;

public abstract class CameraBase implements ICamera {
    protected WeakReference<Activity> activityWeakReference;

    @Override
    public void init(Activity context) {
        activityWeakReference = new WeakReference<>(context);
    }

    @Override
    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean checkAndRequestPermission(Context context) {
        return context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED;
    }

    protected Activity getActivity() {
        if (activityWeakReference == null) {
            return null;
        }
        return activityWeakReference.get();
    }
}
