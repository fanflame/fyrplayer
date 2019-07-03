package com.fanyiran.fyrrecorder.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.utils.FileUtils;
import com.fanyiran.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Camera2Impl implements ICamera {
    private static final String TAG = "Camera2Impl";
    private static final String CAMERA_THREAD = "CAMERA_THREAD";
    private CameraConfig cameraConfig;
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession previewSession;
    private boolean isFlashAvailable;
    private String cameraBackId;
    private String cameraFrontId;
    private String currentCameraId;
    private CaptureRequest.Builder builder;
    private Size mPreviewSize;
    private IRecorder iRecorder;

    private HandlerThread handlerThread;
    private Handler backgroundHandler;

    /**
     * The {@link android.util.Size} of video recording.
     */
    private Size mVideoSize;
    private ImageReader mImageReader;

    @Override
    public void setConfig(CameraConfig cameraConfig) {
        this.cameraConfig = cameraConfig;
        init();
    }

    private void init() {
        cameraManager = (CameraManager) cameraConfig.getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraFrontId = cameraId;
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        cameraConfig.getTargetResolution().getWidth(),
                        cameraConfig.getTargetResolution().getHeight(),
                        mVideoSize);
                cameraConfig.getRecorderConfig().videSize = mVideoSize;
                isFlashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                this.cameraBackId = cameraId;
            }
            currentCameraId = getConfigCameraId();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        handlerThread = new HandlerThread(CAMERA_THREAD);
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    @RequiresPermission(android.Manifest.permission.CAMERA)
    @Override
    public void preview() {
        try {
            cameraManager.openCamera(currentCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    onCameraOpen();
                    cameraDevice = camera;
                    startPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    onCameraDisconnected();
                    cameraDevice.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    onCameraError(2);
                    cameraDevice.close();
                    cameraDevice = null;
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private String getConfigCameraId() {
        if (cameraConfig.getCameraId() == ICamera.CAMERA_FRONT) {
            return cameraFrontId;
        }
        return cameraBackId;
    }

    private void onCameraError(int error) {
        // FIXME: 2019-06-27  调用这个方法的所有errorid
        if (cameraConfig.getiCameraListener() != null) {
            cameraConfig.getiCameraListener().onCameraError(error);
        }
    }

    private void onCameraDisconnected() {
        if (cameraConfig.getiCameraListener() != null) {
            cameraConfig.getiCameraListener().onCameraDisconnected();
        }
    }

    private void onCameraOpen() {
        if (cameraConfig.getiCameraListener() != null) {
            cameraConfig.getiCameraListener().onCameraOpen();
        }
    }

    private void startPreview() {
        setupImageReader();
        try {
            final ArrayList<Surface> surfaces = new ArrayList<>();
            for (Surface surface : cameraConfig.getSurface()) {
                surfaces.add(surface);
            }
            surfaces.add(mImageReader.getSurface());
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    previewSession = session;
                    try {
                        builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        for (Surface surface : surfaces) {
                            builder.addTarget(surface);
                        }
                        session.setRepeatingRequest(builder.build(), captureCallback, backgroundHandler);
                    } catch (CameraAccessException e) {
                        LogUtil.v(TAG, e.getMessage());
                        onCameraError(3);
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    onCameraError(2);
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
        }
        if (currentCameraId.equals(cameraBackId)) {
            currentCameraId = cameraFrontId;
        } else {
            currentCameraId = cameraBackId;
        }
        preview();
    }

    @Override
    public void release() {
        if (cameraDevice != null) {
            cameraDevice.close();
        }
    }

    @Override
    public void startRecord() {
        closePreviewSession();
        record();
    }

    private void record() {
        //        recorderConfig.getSurface().get(0).setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        getiRecorder().init(cameraConfig.getRecorderConfig());
        try {
            final ArrayList<Surface> surfaces = new ArrayList<>();
            for (Surface surface : cameraConfig.getSurface()) {
                surfaces.add(surface);
            }
            surfaces.add(getiRecorder().getSurface());
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    previewSession = session;
                    try {
                        builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                        for (Surface surface : surfaces) {
                            builder.addTarget(surface);
                        }
//                        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        session.setRepeatingRequest(builder.build(), null, null);
                        getiRecorder().startRecord();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    onCameraError(2);
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            onCameraError(2);
        }
    }


    private void closePreviewSession() {
        if (previewSession != null) {
            previewSession.close();
            previewSession = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    @Override
    public void pauseRecord() {
        getiRecorder().pause();
    }

    @Override
    public void resumeRecord() {
        getiRecorder().resume();
    }

    @Override
    public void stopRecord() {
        getiRecorder().stopRecord();
        closePreviewSession();
        startPreview();
    }

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            LogUtil.v(TAG, "onCaptureStarted");
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            LogUtil.v(TAG, "onCaptureProgressed");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            LogUtil.v(TAG, "onCaptureCompleted");
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            LogUtil.v(TAG, "onCaptureFailed");
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            LogUtil.v(TAG, "onCaptureSequenceCompleted");
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            LogUtil.v(TAG, "onCaptureSequenceAborted");
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            LogUtil.v(TAG, "onCaptureBufferLost");
        }
    };

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the autoPreview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            LogUtil.v(TAG, String.format("preview suport width:%s,height%s", option.getWidth(), option.getHeight()));
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            LogUtil.v(TAG, "Couldn't find any suitable autoPreview size");
            return choices[0];
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            LogUtil.v(TAG, String.format("MediaRecorder suport width:%s,height%s", size.getWidth(), size.getHeight()));
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        LogUtil.v(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private void setupImageReader() {
        //2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                ImageFormat.JPEG, 2);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
//                switch (mState) {
//                    case STATE_PREVIEW:
                //这里一定要调用reader.acquireNextImage()和img.close方法否则不会一直回掉了
                Image img = reader.acquireNextImage();
                try {
                    Image.Plane[] planes = img.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    buffer.rewind();
                    byte[] data = new byte[buffer.capacity()];
                    buffer.get(data);

                    //从byte数组得到Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //得到的图片是我们的预览图片的大小进行一个缩放到水印图片里面可以完全显示
                    bitmap = drawTextToCenter(bitmap,
                            "这是水印", 16, Color.RED);
                    // 获取到画布
//                    Canvas canvas = cameraConfig.getSurface().get(0).lockCanvas();
//                    if (canvas == null) {
//                        img.close();
//                        return;
//                    }
//                    canvas.drawBitmap(bitmap, 0, 0, new Paint());
//                    cameraConfig.getSurface().get(0).unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                img.close();
            }
        }, backgroundHandler);
    }

    private Bitmap drawTextToCenter(Bitmap bitmap, String content, int size, int color) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (currentCameraId.equals(cameraBackId)) {
        } else {
            Matrix matrix = new Matrix();
            matrix.setScale(-1, 1);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        Bitmap bitmapBg = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmapBg);
        Paint paint = new Paint();
//        float[] array = {1.438f, -0.062f, -0.062f, 0, 0,
//                -0.122f, 1.378f, -0.122f, 0, 0,
//                -0.016f, -0.016f, 1.483f, 0, 0,
//                -0.03f, 0.05f, -0.02f, 1, 0};
//        float[] array = {0.393f,0.769f,0.189f,0,0,
//                0.349f,0.686f,0.168f,0,0,
//                0.272f,0.534f,0.131f,0,0,
//                0,0,0,1,0};
        float[] array = {2,0,0,0,0,
                0,1,0,0,0,
                0,0,1,0,0,
                0,0,0,1,0};
        ColorMatrix colorMatrix = new ColorMatrix(array);
//        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        paint.setColor(color);
        paint.setTextSize(size);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);

        canvas.save();
        canvas.translate(width, height);
        canvas.rotate(90);
        canvas.translate(-height, width - height);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
        canvas.drawText(content, 100, 100, paint);

        return bitmapBg;
    }

    private IRecorder getiRecorder() {
        if (iRecorder == null) {
            iRecorder = RecorderManager.getInstance().createRecorder();
        }
        return iRecorder;
    }
}
