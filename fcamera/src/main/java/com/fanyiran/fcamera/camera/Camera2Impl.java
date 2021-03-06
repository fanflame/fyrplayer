//package com.fanyiran.fcamera.camera;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.ColorMatrix;
//import android.graphics.ColorMatrixColorFilter;
//import android.graphics.ImageFormat;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Typeface;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.hardware.camera2.CaptureFailure;
//import android.hardware.camera2.CaptureRequest;
//import android.hardware.camera2.CaptureResult;
//import android.hardware.camera2.TotalCaptureResult;
//import android.hardware.camera2.params.StreamConfigurationMap;
//import android.media.Image;
//import android.media.ImageReader;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.util.Range;
//import android.util.Size;
//import android.view.Surface;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresPermission;
//
//import com.fanyiran.fyrrecorder.recorder.IRecorder;
//import com.fanyiran.fyrrecorder.recorder.IRecorderAbstract;
//import com.fanyiran.utils.LogUtil;
//
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Camera2Impl implements ICamera {
//    private static final int WHAT_INIT = 0;
//    private static final int WHAT_PREVIEW = 1;
//    private static final int WHAT_START_RECORD = 2;
//    private static final int WHAT_RESMUE = 3;
//    private static final int WHAT_PAUSE = 4;
//    private static final int WHAT_STOP = 5;
//    private static final int WHAT_RELEASE = 6;
//
//    private static final String TAG = "Camera2Impl";
//    private static final String CAMERA_THREAD = "CAMERA_THREAD";
//    private CameraConfig cameraConfig;
//    private CameraManager cameraManager;
//    private CameraDevice cameraDevice;
//    private CameraCaptureSession previewSession;
//    private boolean isFlashAvailable;
//    private String cameraBackId;
//    private String cameraFrontId;
//    private CaptureRequest.Builder builder;
//    private IRecorder iRecorder;
//
//    private HandlerThread handlerThread;
//    private Handler backgroundHandler;
//
//    private ImageReader mImageReader;
//    private long lastSycPreviewFpsTime;
//    private volatile long previewCount;
//    private volatile long previewTimePassed;
//
//    private CameraInfo currentCameraInfo;
//    private Map<String, CameraInfo> allCameraInfoMap;
//
//    private class CameraInfo {
//        Boolean isFlashAvailable;
//        Size mVideoSize;
//        Size mPreviewSize;
//        String cameraId;
//        Range<Integer> fpsRanges;
//    }
//
//
//    @Override
//    public void setConfig(CameraConfig cameraConfig) {
//        this.cameraConfig = cameraConfig;
//        handlerThread = new HandlerThread(CAMERA_THREAD);
//        handlerThread.start();
//        backgroundHandler = new Handler(handlerThread.getLooper(), callback);
//        backgroundHandler.sendEmptyMessage(WHAT_INIT);
//    }
//
//    private void init() {
//        cameraManager = (CameraManager) cameraConfig.getContext().getSystemService(Context.CAMERA_SERVICE);
//        try {
//            for (String cameraId : cameraManager.getCameraIdList()) {
//                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
//                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
//                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
//                    cameraFrontId = cameraId;
//                    continue;
//                }
//                this.cameraBackId = cameraId;
//            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        String currentCameraId = getConfigCameraId();
//        currentCameraInfo = getCameraInfo(currentCameraId);
//        if (currentCameraInfo == null) {
//            throw new IllegalStateException("no camera id found");
//        }
//    }
//
//    private CameraInfo getCameraInfo(String cameraId) {
//        CameraCharacteristics cameraCharacteristics = null;
//        try {
//            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//            return null;
//        }
//        if (allCameraInfoMap == null) {
//            allCameraInfoMap = new HashMap<>();
//        }
//        if (allCameraInfoMap.get(cameraId) != null) {
//            return allCameraInfoMap.get(cameraId);
//        }
//        CameraInfo cameraInfo = new CameraInfo();
//        cameraInfo.cameraId = cameraId;
//        Range<Integer>[] ranges = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
//        LogUtil.v(TAG, String.format("cameraId %s,fps rangs:%s", cameraId, Arrays.toString(ranges)));
//        cameraInfo.fpsRanges = getSuitableFps(ranges,cameraConfig.getRecorderConfig().frameRate);
//
//        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//        if (map == null) {
//            return cameraInfo;
//        }
//        Size[] sizes = map.getOutputSizes(cameraConfig.getRecorderClass());
//        if (sizes != null) {
//            cameraInfo.mVideoSize = chooseVideoSize(sizes);
//        }
//        sizes = map.getOutputSizes(cameraConfig.getPreviewClass());
//        if (sizes != null) {
//            cameraInfo.mPreviewSize = chooseOptimalSize(sizes,
//                    cameraConfig.getTargetResolution().getWidth(),
//                    cameraConfig.getTargetResolution().getHeight(),
//                    cameraInfo.mVideoSize);
//
//        }
////        cameraConfig.getRecorderConfig().videSize = mVideoSize;
//        cameraInfo.isFlashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
//        return cameraInfo;
//    }
//
//    private Range<Integer> getSuitableFps(Range<Integer>[] ranges,int fps) {
//        if (ranges == null) {
//            return null;
//        }
//        for (int i = ranges.length - 1; i >= 0; i--) {
//            if (ranges[i].getLower() >= fps) {
//                return ranges[i];
//            }
//        }
//        return null;
//    }
//
//    private Handler.Callback callback = new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case WHAT_INIT:
//                    init();
//                    break;
//                case WHAT_PREVIEW:
//                    previewInner();
//                    break;
//                case WHAT_START_RECORD:
//                    closePreviewSession();
//                    record();
//                    break;
//                case WHAT_PAUSE:
//                    getiRecorder().pause();
//                    break;
//                case WHAT_RESMUE:
//                    getiRecorder().resume();
//                    break;
//                case WHAT_STOP:
//                    getiRecorder().stopRecord();
//                    closePreviewSession();
//                    startPreview();
//                    break;
//                case WHAT_RELEASE:
//                    if (previewSession != null) {
//                        try {
//                            previewSession.stopRepeating();
//                        } catch (CameraAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (cameraDevice != null) {
//                        cameraDevice.close();
//                    }
//                    getiRecorder().release();
//                    handlerThread.getLooper().quitSafely();
//                    break;
//            }
//            return true;
//        }
//    };
//
//    @Override
//    public void preview() {
//        backgroundHandler.sendEmptyMessage(WHAT_PREVIEW);
//    }
//
//    @RequiresPermission(android.Manifest.permission.CAMERA)
//    private void previewInner() {
//        try {
//            cameraManager.openCamera(currentCameraInfo.cameraId, new CameraDevice.StateCallback() {
//                @Override
//                public void onOpened(@NonNull CameraDevice camera) {
//                    onCameraOpen();
//                    cameraDevice = camera;
//                    startPreview();
//                }
//
//                @Override
//                public void onDisconnected(@NonNull CameraDevice camera) {
//                    onCameraDisconnected();
//                    cameraDevice.close();
//                    cameraDevice = null;
//                }
//
//                @Override
//                public void onError(@NonNull CameraDevice camera, int error) {
//                    onCameraError(2);
//                    cameraDevice.close();
//                    cameraDevice = null;
//                }
//            }, backgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String getConfigCameraId() {
//        if (cameraConfig.getCameraId() == ICamera.CAMERA_FRONT) {
//            return cameraFrontId;
//        }
//        return cameraBackId;
//    }
//
//    private void onCameraError(int error) {
//        // FIXME: 2019-06-27  调用这个方法的所有errorid
//        if (cameraConfig.getiCameraListener() != null) {
//            cameraConfig.getiCameraListener().onCameraError(error);
//        }
//    }
//
//    private void onCameraDisconnected() {
//        if (cameraConfig.getiCameraListener() != null) {
//            cameraConfig.getiCameraListener().onCameraDisconnected();
//        }
//    }
//
//    private void onCameraOpen() {
//        if (cameraConfig.getiCameraListener() != null) {
//            cameraConfig.getiCameraListener().onCameraOpen();
//        }
//    }
//
//    private void startPreview() {
//        setupImageReader();
//        try {
//            final ArrayList<Surface> surfaces = new ArrayList<>();
//            for (Surface surface : cameraConfig.getSurface()) {
//                surfaces.add(surface);
//            }
//            surfaces.add(mImageReader.getSurface());
//            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(@NonNull CameraCaptureSession session) {
//                    if (getiRecorder().getStatus() == IRecorderAbstract.RECORD_STATUS_RELEASE) {
//                        return;
//                    }
//                    previewSession = session;
//                    try {
//                        builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                        for (Surface surface : surfaces) {
//                            builder.addTarget(surface);
//                        }
//                        builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, currentCameraInfo.fpsRanges);
//                        session.setRepeatingRequest(builder.build(), captureCallback, backgroundHandler);
//                    } catch (CameraAccessException e) {
//                        LogUtil.v(TAG, e.getMessage());
//                        onCameraError(3);
//                    }
//                }
//
//                @Override
//                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                    onCameraError(2);
//                }
//            }, backgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void switchCamera() {
//        // TODO: 2019-07-10 切换camera可以继续录制 ？
//        if (cameraDevice != null) {
//            cameraDevice.close();
//        }
//        if (currentCameraInfo.cameraId.equals(cameraBackId)) {
//            currentCameraInfo = getCameraInfo(cameraFrontId);
//        } else {
//            currentCameraInfo = getCameraInfo(cameraBackId);
//        }
//        preview();
//    }
//
//    @Override
//    public void release() {
//        backgroundHandler.sendEmptyMessage(WHAT_RELEASE);
//    }
//
//    @Override
//    public void startRecord() {
//        backgroundHandler.sendEmptyMessage(WHAT_START_RECORD);
//    }
//
//    private void record() {
//        //        recorderConfig.getSurface().get(0).setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//        getiRecorder().init(cameraConfig.getRecorderConfig());
//        try {
//            final ArrayList<Surface> surfaces = new ArrayList<>();
////            for (Surface surface : cameraConfig.getSurface()) {
////                surfaces.add(surface);
////            }
////            surfaces.add(getiRecorder().getSurface());
//            surfaces.add(mImageReader.getSurface());
//            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(@NonNull CameraCaptureSession session) {
//                    previewSession = session;
//                    try {
//                        builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
////                        for (Surface surface : surfaces) {
////                            builder.addTarget(surface);
////                        }
//                        surfaces.add(getiRecorder().getSurface());
////                        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//                        session.setRepeatingRequest(builder.build(), null, null);
//                        getiRecorder().startRecord();
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                    onCameraError(2);
//                }
//            }, backgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//            onCameraError(2);
//        }
//    }
//
//
//    private void closePreviewSession() {
//        if (previewSession != null) {
//            previewSession.close();
//            previewSession = null;
//        }
////        if (mImageReader != null) {
////            mImageReader.close();
////            mImageReader = null;
////        }
//    }
//
//    @Override
//    public void pauseRecord() {
//        backgroundHandler.sendEmptyMessage(WHAT_PAUSE);
//    }
//
//    @Override
//    public void resumeRecord() {
//        backgroundHandler.sendEmptyMessage(WHAT_RESMUE);
//    }
//
//    @Override
//    public void stopRecord() {
//        backgroundHandler.sendEmptyMessage(WHAT_STOP);
//    }
//
//    @Override
//    public int getPreviewFps() {
//        if (previewTimePassed == 0) {
//            return 0;
//        }
//        return (int) (previewCount / previewTimePassed);
//    }
//
//    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
//        @Override
//        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
//        }
//
//        @Override
//        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
//        }
//
//        @Override
//        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
//        }
//
//        @Override
//        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
//        }
//
//        @Override
//        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
//        }
//
//        @Override
//        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
//        }
//
//        @Override
//        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
//        }
//    };
//
//    /**
//     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
//     * width and height are at least as large as the respective requested values, and whose aspect
//     * ratio matches with the specified value.
//     *
//     * @param choices     The list of sizes that the camera supports for the intended output class
//     * @param width       The minimum desired width
//     * @param height      The minimum desired height
//     * @param aspectRatio The aspect ratio
//     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
//     */
//    private Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
//        // Collect the supported resolutions that are at least as big as the autoPreview Surface
//        List<Size> bigEnough = new ArrayList<>();
//        int w = aspectRatio.getWidth();
//        int h = aspectRatio.getHeight();
//        for (Size option : choices) {
//            LogUtil.v(TAG, String.format(cameraConfig.getPreviewClass().getName() + " suport width:%s,height%s", option.getWidth(), option.getHeight()));
//            if (option.getHeight() == option.getWidth() * h / w &&
//                    option.getWidth() >= width && option.getHeight() >= height) {
//                bigEnough.add(option);
//            }
//        }
//
//        // Pick the smallest of those, assuming we found any
//        if (bigEnough.size() > 0) {
//            return Collections.min(bigEnough, new CompareSizesByArea());
//        } else {
//            LogUtil.v(TAG, "Couldn't find any suitable autoPreview size");
//            return choices[0];
//        }
//    }
//
//    /**
//     * Compares two {@code Size}s based on their areas.
//     */
//    static class CompareSizesByArea implements Comparator<Size> {
//
//        @Override
//        public int compare(Size lhs, Size rhs) {
//            // We cast here to ensure the multiplications won't overflow
//            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
//                    (long) rhs.getWidth() * rhs.getHeight());
//        }
//
//    }
//
//    /**
//     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
//     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
//     *
//     * @param choices The list of available sizes
//     * @return The video size
//     */
//    private Size chooseVideoSize(Size[] choices) {
//        for (Size size : choices) {
//            LogUtil.v(TAG, String.format(cameraConfig.getRecorderClass().getName() + " suport width:%s,height%s", size.getWidth(), size.getHeight()));
//            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
//                return size;
//            }
//        }
//        LogUtil.v(TAG, "Couldn't find any suitable video size");
//        return choices[choices.length - 1];
//    }
//
//    private void setupImageReader() {
//        //2代表ImageReader中最多可以获取两帧图像流
//        mImageReader = ImageReader.newInstance(currentCameraInfo.mPreviewSize.getWidth(), currentCameraInfo.mPreviewSize.getHeight(),
//                ImageFormat.YUV_420_888, 2);
//        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                if (lastSycPreviewFpsTime == 0) {
//                    lastSycPreviewFpsTime = System.currentTimeMillis();
//                }
//                if (System.currentTimeMillis() - lastSycPreviewFpsTime >= 1000) {
//                    previewTimePassed++;
//                    lastSycPreviewFpsTime = System.currentTimeMillis();
//                } else {
//                    previewCount++;
//                }
//
////                switch (mState) {
////                    case STATE_PREVIEW:
//                //这里一定要调用reader.acquireNextImage()和img.close方法否则不会一直回掉了
//                Image img = reader.acquireNextImage();
//                if (getiRecorder().getStatus() == IRecorderAbstract.RECORD_STATUS_START_RECORD) {
//                    try {
////                        LogUtil.v(TAG, "imageformat:" + img.getFormat());
//                        Image.Plane[] planes = img.getPlanes();
//                        ByteBuffer buffer = planes[0].getBuffer();
//                        buffer.rewind();
//                        byte[] dataY = new byte[buffer.capacity()];
//                        buffer.get(dataY);
//
//                        buffer = planes[1].getBuffer();
//                        buffer.rewind();
//                        int pixelStride = planes[1].getPixelStride();
//                        byte[] dataU = new byte[buffer.capacity() / pixelStride];
//                        if (pixelStride != 1) {// TODO: 2019-07-10 planes[1].getRowStride()?
//                            for (int i = 0; i < dataU.length; i++) {// TODO: 2019-07-11 为啥要这么获取uv值？
//                                dataU[i] = buffer.get(i * pixelStride + 2);
//                            }
//                        } else {
//                            buffer.get(dataU);
//                        }
//
//                        buffer = planes[2].getBuffer();
//                        buffer.rewind();
//                        pixelStride = planes[2].getPixelStride();
//                        byte[] dataV = new byte[buffer.capacity() / pixelStride];
//                        if (pixelStride != 1) {
//                            for (int i = 0; i < dataU.length; i++) {
//                                dataV[i] = buffer.get(i * pixelStride + 2);
//                            }
//                        } else {
//                            buffer.get(dataV);
//                        }
//                        getiRecorder().receiveData(dataY, dataU, dataV);
//
//                        //                    //从byte数组得到Bitmap
//                        //                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        //                    //得到的图片是我们的预览图片的大小进行一个缩放到水印图片里面可以完全显示
//                        //                    bitmap = drawTextToCenter(bitmap,
//                        //                            "这是水印", 16, Color.RED);
//                        // 获取到画布
//                        //                    Canvas canvas = cameraConfig.getSurface().get(0).lockCanvas();
//                        //                    if (canvas == null) {
//                        //                        img.close();
//                        //                        return;
//                        //                    }
//                        //                    canvas.drawBitmap(bitmap, 0, 0, new Paint());
//                        //                    cameraConfig.getSurface().get(0).unlockCanvasAndPost(canvas);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                img.close();
//            }
//        }, backgroundHandler);
//    }
//
//    private Bitmap drawTextToCenter(Bitmap bitmap, String content, int size, int color) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        if (currentCameraInfo.cameraId.equals(cameraBackId)) {
//        } else {
//            Matrix matrix = new Matrix();
//            matrix.setScale(-1, 1);
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//        }
//        Bitmap bitmapBg = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmapBg);
//        Paint paint = new Paint();
////        float[] array = {1.438f, -0.062f, -0.062f, 0, 0,
////                -0.122f, 1.378f, -0.122f, 0, 0,
////                -0.016f, -0.016f, 1.483f, 0, 0,
////                -0.03f, 0.05f, -0.02f, 1, 0};
////        float[] array = {0.393f,0.769f,0.189f,0,0,
////                0.349f,0.686f,0.168f,0,0,
////                0.272f,0.534f,0.131f,0,0,
////                0,0,0,1,0};
//        float[] array = {2, 0, 0, 0, 0,
//                0, 1, 0, 0, 0,
//                0, 0, 1, 0, 0,
//                0, 0, 0, 1, 0};
//        ColorMatrix colorMatrix = new ColorMatrix(array);
////        ColorMatrix colorMatrix = new ColorMatrix();
////        colorMatrix.setSaturation(0);
//        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
//        paint.setColor(color);
//        paint.setTextSize(size);
//        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
//        paint.setTypeface(font);
//
//        canvas.save();
//        canvas.translate(width, height);
//        canvas.rotate(90);
//        canvas.translate(-height, width - height);
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//        canvas.restore();
//        canvas.drawText(content, 100, 100, paint);
//
//        return bitmapBg;
//    }
//
//    private IRecorder getiRecorder() {
//        if (iRecorder == null) {
//            iRecorder = RecorderManager.getInstance().createRecorder();
//        }
//        return iRecorder;
//    }
//}
