package com.fanyiran.fcamera.camera;


import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;
import com.fanyiran.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


@SuppressWarnings("deprecation")
public class CameraImpl extends CameraBase {
    private static final String TAG = "CameraImpl";
    private int cameraNum;
    private int cameraFrontCount;
    private int cameraBackCount;
    private WeakReference<Object> surfaceReference;

    private Camera currentCamera;
    private int currentCameraId = -1;
    private long lastFpsTimes;
    private int fps;
    private boolean isTakePic;
    private boolean isPreviewing;

    private OrientationEventListener orientationEventListener;

    @Override
    public void init(Activity activity) {
        super.init(activity);
        LogUtil.v(TAG, "init");
        cameraNum = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo;
        for (int i = 0; i < cameraNum; i++) {
            cameraInfo = new Camera.CameraInfo();
            try {
                Camera.getCameraInfo(i, cameraInfo);
            } catch (RuntimeException e) {
                e.printStackTrace();
                continue;
            }
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraFrontCount++;
            } else if ((cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                cameraBackCount++;
            }
        }
        LogUtil.v(TAG, "cameraNum:" + cameraNum);
    }

    @Override
    public int getCameraCount(@ICameraNumber int orientation) {
        switch (orientation) {
            case ICamera.CAMERA_ALL:
                return cameraNum;
            case ICamera.CAMERA_FRONT:
                return cameraFrontCount;
            case ICamera.CAMREA_BACK:
                return cameraBackCount;
        }
        return 0;
    }

    @Override
    public int getCurrentPreviewFps() {
        return fps;
    }

    @Override
    public void setPreviewFps(int minFps, int maxFps) {
        checkCurrentCamera();
        int[] properPreviewFps = getProperPreviewFps(minFps, maxFps);
        LogUtil.v(TAG, String.format("preview fps:%d,%d", properPreviewFps[0], properPreviewFps[1]));
        Camera.Parameters parameters = currentCamera.getParameters();
        parameters.setPreviewFpsRange(properPreviewFps[0], properPreviewFps[1]);
        currentCamera.setParameters(parameters);
    }

    private int[] getProperPreviewFps(int minFps, int maxFps) {
        // TODO: 2020/5/12  获取最佳预览fps
        List<int[]> supportedPreviewFpsRange = currentCamera.getParameters().getSupportedPreviewFpsRange();
        for (int[] fps : supportedPreviewFpsRange) {
            if (fps[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] == minFps * 1000) {
                return fps;
            }
        }
        return supportedPreviewFpsRange.get(0);
    }

    @Override
    public int getOrientation(int cameraId) {
        if (cameraId < 0 || cameraId > cameraNum - 1) {
            return -1;
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, cameraInfo);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return -1;
        }
        return cameraInfo.orientation;
    }

//    @Override
//    public void setPreviewOrientation(int degree) {
//        setCameraDisplayOrientation();
//    }

    private void setCameraDisplayOrientation() {
        Activity context = getActivity();
        if (context == null) {
            return;
        }
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraId, info);
        int rotation = context.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        currentCamera.setDisplayOrientation(result);
    }

    @Override
    public int open(boolean isFront) {
        release();
        Camera.CameraInfo cameraInfo;
        currentCameraId = -1;
        for (int i = 0; i < cameraNum; i++) {
            cameraInfo = new Camera.CameraInfo();
            try {
                Camera.getCameraInfo(i, cameraInfo);
            } catch (RuntimeException e) {
                e.printStackTrace();
                continue;
            }
            if (isFront && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                currentCameraId = i;
                break;
            }
            if (!isFront && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                currentCameraId = i;
                break;
            }
        }
        if (currentCameraId != -1) {
            openInner(currentCameraId);
        }
        LogUtil.v(TAG, "currentCamera:" + currentCamera);
        return currentCameraId;
    }

    @Override
    public void setPreviewSize(Size size) {
        checkCurrentCamera();
//        Size previewSize = getPreviewSize(size);
        Size previewSize = getProperPreviewSize(size);
        LogUtil.v(TAG, previewSize + "");
        if (previewSize == null) {
            return;
        }
        Camera.Parameters parameters = currentCamera.getParameters();
        parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        try {
            currentCamera.setParameters(parameters);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private Size getProperPreviewSize(Size exceptSize) {
        // TODO: 2020/5/12 获取最佳预览大小
        int orientation = getActivity().getWindowManager().getDefaultDisplay().getOrientation();
        if (orientation == 0 || orientation == 180) {

        } else {

        }
        List<Camera.Size> supportedPreviewSizes = currentCamera.getParameters().getSupportedPreviewSizes();
//        Arrays.sort(objects);
        for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
            if (exceptSize.getWidth() == supportedPreviewSize.width
                    && exceptSize.getHeight() == supportedPreviewSize.height) {
                return exceptSize;
            }
            if (exceptSize.getWidth() == supportedPreviewSize.width) {
                return exceptSize;
            }
        }
        return null;
    }

    private boolean openInner(int tempCameraId) {
        try {
            //On some devices, this method may take a long time to complete
            currentCamera = Camera.open(tempCameraId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        currentCameraId = tempCameraId;
        currentCamera.setErrorCallback(new Camera.ErrorCallback() {

            @Override
            public void onError(int error, Camera camera) {

            }
        });
        return true;
    }

    @Override
    public void setConfig(CameraConfig cameraConfig) {

    }


    @Override
    public boolean preview(SurfaceTexture surface) {
        checkCurrentCamera();
        surfaceReference = new WeakReference<Object>(surface);
        try {
            currentCamera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        pveviewInner();
        return true;
    }

    private void pveviewInner() {
        currentCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                fps++;
                if (System.currentTimeMillis() - lastFpsTimes > 1000) {
                    lastFpsTimes = System.currentTimeMillis();
                    fps = 0;
                }
            }
        });
        if (getActivity() != null && orientationEventListener == null) {
            orientationEventListener = new OrientationEventListener(getActivity()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    CameraImpl.this.onOrientationChanged(orientation);
                }
            };
        }
        orientationEventListener.enable();
        isPreviewing = true;
        currentCamera.startPreview();
        setCameraDisplayOrientation();
    }

    private void checkCurrentCamera() {
        if (currentCamera == null) {
            throw new IllegalStateException("camera is null");
        }
    }

    @Override
    public boolean preview(SurfaceHolder holder) {
        checkTakingPic();
        checkCurrentCamera();
        surfaceReference = new WeakReference<Object>(holder);
        try {
            currentCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        pveviewInner();
        return true;
    }

    @Override
    public void stopPreview() {
        if (currentCamera != null) {
            try {
                currentCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkTakingPic() {
        if (isTakePic) {
            throw new IllegalStateException("camera is takingPic");
        }
    }

    @Override
    public boolean switchCamera() {
        int tempId = currentCameraId;
        release();
        if (tempId == -1) {
            if (open(true) != -1) {
                return false;
            }
            return previewInner();
        } else {
            tempId = (tempId + 1) % cameraNum;
            LogUtil.v(TAG, String.format("switchCamera cameraId:%d", tempId));
            if (!openInner(tempId)) {
                return false;
            }
            return previewInner();
        }
    }

    @Override
    public void takePicture(final File picFile, final OnTakePicCallBack onTakePicCallBack) {
        checkPreviewing();
        checkCurrentCamera();
        isTakePic = true;
        List<Camera.Size> supportedPictureSizes = currentCamera.getParameters().getSupportedPictureSizes();
        for (Camera.Size supportedPictureSize : supportedPictureSizes) {
            LogUtil.v(TAG, String.format("supportPicSize:%d;%d", supportedPictureSize.width, supportedPictureSize.height));
        }
        Camera.Parameters parameters = currentCamera.getParameters();
        parameters.setPictureSize(supportedPictureSizes.get(0).width, supportedPictureSizes.get(0).height);
        currentCamera.setParameters(parameters);
        try {
            currentCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    // TODO: 2020/5/12 左右反转
                    savePicFiles(picFile, data);
                    isTakePic = false;
                    if (onTakePicCallBack != null) {
                        onTakePicCallBack.onTakePicCallBack(picFile);
                    }
                }
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void savePicFiles(File picFile, byte[] data) {
        if (!picFile.exists()) {
            try {
                picFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 2020/5/12 创建文件失败
            }
        }
        int off = 0;
        int remainSize = data.length;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(picFile);
            fileOutputStream.write(data, off, remainSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkPreviewing() {
        if (!isPreviewing) {
            throw new IllegalStateException("take picture must");
        }
    }

    private boolean previewInner() {
        if (surfaceReference == null) {
            return false;
        }
        Object surface = surfaceReference.get();
        if (surface == null) {
            return false;
        }
        if (surface instanceof SurfaceTexture) {
            return preview((SurfaceTexture) surface);
        } else if (surface instanceof SurfaceHolder) {
            return preview((SurfaceHolder) surface);
        }
        return false;
    }

    public void onOrientationChanged(int orientation) {
        if (currentCameraId == -1) {
            return;
        }
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraId, info);
        orientation = (orientation + 45) / 90 * 90;
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        Camera.Parameters parameters = currentCamera.getParameters();
        parameters.setRotation(rotation);
        currentCamera.setParameters(parameters);
    }

    @Override
    public void release() {
        LogUtil.v(TAG, "release");
        if (currentCamera != null) {
            currentCamera.setPreviewCallback(null);
            currentCamera.release();
            currentCamera = null;
        }
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
        currentCameraId = -1;
        isPreviewing = false;
        isTakePic = false;
    }

    public Camera getCurrentCamera() {
        return currentCamera;
    }
}
