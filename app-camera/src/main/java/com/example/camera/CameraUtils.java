package com.example.camera;

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

    public static Camera open(int cameraId) {
        try {
            return Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void followScreenOrientation(Context context, Camera camera){
        if (context == null || camera == null) {
            return;
        }
        final int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }

    public static void startPreview(Camera camera, SurfaceHolder holder){
        if (camera == null || holder == null) {
            return;
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error while START preview for camera", e);
        }
    }

    public static void stopPreview(Camera camera){
        if (camera == null) {
            return;
        }
        try {
            camera.stopPreview();
            camera.setPreviewDisplay(null);
        } catch (Exception e){
            Log.e(TAG, "Error while STOP preview for camera", e);
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
