package com.arcsoft.sdk_demo.afirez;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraHandlerThread extends HandlerThread {

    public final Handler handler;

    public int id = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public volatile Camera camera;

    public int width = 1280;
    public int height = 720;
    public int bufferSize;
    public int fps = 30;
    private Activity activity;

    private CameraCallback cameraCallback;

    public interface CameraCallback {
        void onOpen(Camera camera);
    }

    public void setCameraCallback(CameraCallback cameraCallback) {
        this.cameraCallback = cameraCallback;
    }

    public CameraHandlerThread(Activity activity) {
        this("Camera");
        this.activity = activity;
    }

    public CameraHandlerThread(String name) {
        super(name);
        start();
        handler = new Handler(getLooper());
    }

    public void releaseCamera() {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void swithCamera() {
        openCamera(1 - id);
    }

    public void openCamera(final int id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (id != CameraHandlerThread.this.id && camera != null) {
                    releaseCamera();
                }
                if (camera != null) {
                    try {
                        camera.reconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                        camera = null;
                    }
                }
                try {
                    camera = Camera.open(id);
                } catch (Throwable ignored) {
                    try {
                        camera = Camera.open(id);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                if (camera != null) {
                    CameraHandlerThread.this.id = id;
                    configCamera(camera, activity, id);
                }
                if (cameraCallback != null) {
                    cameraCallback.onOpen(camera);
                }
            }
        });
    }

    public void configCamera(Camera camera, Activity activity, int id) {
        if (camera != null) {
            this.id = id;
            Camera.Parameters parameters = camera.getParameters();
            calculateAndSetFps(parameters, calculateAndSetFps(parameters, fps));
            Camera.Size size = calculateSize(parameters.getSupportedPreviewSizes(), width, height);
            parameters.setPreviewSize(size.width, size.height);
            int degrees = calculateRotation(activity, id);
            camera.setDisplayOrientation(degrees);
            camera.setParameters(parameters);
            width = parameters.getPreviewSize().width;
            height = parameters.getPreviewSize().height;
            bufferSize = calculateBufferSize(camera);
        }
    }

    public static Camera.Size calculateSize(List<Camera.Size> sizes, int targetWidth, int targetHeight) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        sortSizes(sizes);
        Camera.Size targetSize = sizes.get(0);
        boolean singleCalculated = false;
        for (Camera.Size size : sizes) {
            if (size.width == targetWidth && size.height == targetHeight) {
                // 宽高相等，直接返回
                targetSize = size;
                break;
            }
            if (size.width == targetWidth) {
                // 宽相等，选高最接近的
                singleCalculated = true;
                if (Math.abs(targetSize.height - targetHeight)
                        > Math.abs(size.height - targetHeight)) {
                    targetSize = size;
                }
            } else if (targetSize.height == targetHeight) {
                // 高相等，选宽最接近的
                singleCalculated = true;
                if (Math.abs(targetSize.width - targetWidth)
                        > Math.abs(size.width - targetWidth)) {
                    targetSize = size;
                }
            } else if (!singleCalculated) {
                // 没有宽或高相等的， 选宽和高均为最接近的
                if (Math.abs(targetSize.width - targetWidth)
                        > Math.abs(size.width - targetWidth)
                        && Math.abs(targetSize.height - targetHeight)
                        > Math.abs(size.height - targetHeight)) {
                    targetSize = size;
                }
            }
        }
        return targetSize;
    }

    private static void sortSizes(List<Camera.Size> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return;
        }
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                if (a.width > b.width) {
                    return 1;
                } else if (a.width < b.width) {
                    return -1;
                }
                return 0;
            }
        });
    }

    public static int calculateRotation(Activity activity, int cameraId) {
        if (activity == null) {
            return 0;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        int rotation = activity.getWindowManager().getDefaultDisplay()
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
        int targetRotation;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            targetRotation = (cameraInfo.orientation + degrees) % 360;
            targetRotation = (360 - targetRotation) % 360;
        } else {
            targetRotation = (360 + cameraInfo.orientation - degrees) % 360;
        }
        return targetRotation;
    }

    public static int calculateAndSetFps(Camera.Parameters parameters, int targetThousandFps) {
        if (parameters == null) {
            return 0;
        }
        List<int[]> supportedFpsRanges = parameters.getSupportedPreviewFpsRange();
        for (int[] fpsRange : supportedFpsRanges) {
            if (fpsRange[0] == fpsRange[1] && fpsRange[0] == targetThousandFps) {
                parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
                return fpsRange[0];
            }
        }
        int targetFps;
        int[] tempFpsRange = new int[2];
        parameters.getPreviewFpsRange(tempFpsRange);
        if (tempFpsRange[0] == tempFpsRange[1]) {
            targetFps = tempFpsRange[0];
        } else {
            targetFps = tempFpsRange[1] / 2;
        }
        return targetFps;
    }

    public static int calculateBufferSize(Camera camera) {
        if (camera == null) {
            return 0;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) {
            return 0;
        }
        Camera.Size previewSize = parameters.getPreviewSize();
        if (previewSize == null) {
            return 0;
        }
        int previewFormat = parameters.getPreviewFormat();
        int bitsPerPixel = ImageFormat.getBitsPerPixel(previewFormat);
        float bytesPerPixel = bitsPerPixel / 8f;
        return (int) (previewSize.height * previewSize.width * bytesPerPixel);
    }
}
