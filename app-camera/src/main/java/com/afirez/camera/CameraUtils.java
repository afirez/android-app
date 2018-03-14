package com.afirez.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by afirez on 18-1-17.
 */

public class CameraUtils {

    private static final String TAG = "CameraUtils";

    public static Camera openByFacing(int cameraFacing) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                return openById(i);
            }
        }
        return null;
    }

    public static Camera openById(int cameraId) {
        try {
            return Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(Camera camera) {
        if (camera == null) {
            return;
        }
        isPreviewing = false;
        try {
            camera.release();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void followScreenOrientation(Context context, Camera camera) {
        if (context == null || camera == null) {
            Log.e(TAG, "context == null or camera == null while followScreenOrientation for camera");
            return;
        }
        final int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
            Log.e(TAG, "orientation " + 180);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
            Log.e(TAG, "orientation " + 90);
        }
    }

    public static volatile boolean isPreviewing;

    public static void startPreview(Camera camera, SurfaceHolder holder) {
        if (camera == null || holder == null) {
            Log.e(TAG, "camera == null or holder == null while START preview for camera");
            return;
        }
        if (isPreviewing) {
            Log.e(TAG, "isPreviewing: " + isPreviewing);
            return;
        }
        isPreviewing = true;

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            isPreviewing = false;
            Log.e(TAG, "Error while START preview for camera", e);
        }
    }

    public static void stopPreview(Camera camera) {
        if (camera == null) {
            Log.e(TAG, "camera == null while START preview for camera");
            return;
        }
        isPreviewing = false;
        try {
            camera.stopPreview();
            camera.setPreviewDisplay(null);
        } catch (Exception e) {
            Log.e(TAG, "Error while STOP preview for camera", e);
        }
    }

    public static void setPreviewCallback(Camera camera, Camera.PreviewCallback previewCallback) {
        if (camera == null) {
            Log.e(TAG, "camera == null while setOneShotPreviewCallback for camera");
            return;
        }
        Log.e(TAG, "previewCallback: " + previewCallback);
        try {
            camera.setPreviewCallback(previewCallback);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setOneShotPreviewCallback(Camera camera, Camera.PreviewCallback previewCallback) {
        if (camera == null) {
            Log.e(TAG, "camera == null while setOneShotPreviewCallback for camera");
            return;
        }
        Log.e(TAG, "previewCallback: " + previewCallback);
        try {
            camera.setOneShotPreviewCallback(previewCallback);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setPreviewCallbackWithBuffer(Camera camera, byte[] buffer, Camera.PreviewCallback previewCallback) {
        if (camera == null) {
            Log.e(TAG, "camera == null while setOneShotPreviewCallback for camera");
            return;
        }
        Log.e(TAG, "previewCallback: " + previewCallback);
        try {
            camera.addCallbackBuffer(buffer);
            camera.setPreviewCallbackWithBuffer(previewCallback);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static boolean hasCameraDevice(Context context) {
        return context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isAutoFocusSupported(Camera.Parameters params) {
        List<String> modes = params.getSupportedFocusModes();
        return modes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
    }
}
