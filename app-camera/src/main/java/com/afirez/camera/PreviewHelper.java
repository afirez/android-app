package com.afirez.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by lenovo on 2018/3/14.
 */

public class PreviewHelper implements SurfaceHolder.Callback {

    private static final String TAG = "PreviewHelper";
    private Context mContext;

    public PreviewHelper(Context context) {
        mContext = context;
    }

    private Camera mCamera;

    private SurfaceHolder mSurfaceHolder;

    public void setCamera(Camera camera) {
        if (camera == null) {
            Log.d(TAG, "setCamera (camera == null)");
        }
        mCamera = camera;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            Log.d(TAG, "setSurfaceHolder (surfaceHolder == null)");
        }
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated -> startPreview");
        CameraUtils.startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
            Log.d(TAG, "surfaceChanged -> restartPreview failed (surface == null)");
            return;
        }
        Log.d(TAG, "surfaceChanged -> restartPreview success");
        CameraUtils.followScreenOrientation(mContext, mCamera);
        CameraUtils.stopPreview(mCamera);
        CameraUtils.startPreview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed -> stopPreview");
        CameraUtils.stopPreview(mCamera);
    }
}
