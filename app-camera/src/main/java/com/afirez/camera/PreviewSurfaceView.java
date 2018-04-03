package com.afirez.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by afirez on 18-1-17.
 */

public class PreviewSurfaceView extends SurfaceView
        implements SurfaceHolder.Callback {

    private static final String TAG = "PreviewSurfaceView";

    private final SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
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
        CameraUtils.followScreenOrientation(getContext(), mCamera);
        CameraUtils.stopPreview(mCamera);
        CameraUtils.startPreview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed -> stopPreview");
        CameraUtils.stopPreview(mCamera);
    }

    public void setCamera(Camera camera) {
        if (camera == null) {
            Log.d(TAG, "setCamera (camera == null)");
        }
        mCamera = camera;
    }
}
