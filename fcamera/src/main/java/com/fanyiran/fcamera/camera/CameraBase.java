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
    public boolean checkAndRequestPermission(int requestCode) {
        if (getActivity() == null) {
            return false;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            return false;
        }
        return true;
    }

    protected Activity getActivity() {
        if (activityWeakReference == null) {
            return null;
        }
        return activityWeakReference.get();
    }
}
