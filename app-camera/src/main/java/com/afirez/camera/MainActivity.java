package com.afirez.camera;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SurfaceView svPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mCamera = CameraUtils.openByFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    private void initView() {
        svPreview = (SurfaceView) findViewById(R.id.camera_sv_preview);
        SurfaceHolder surfaceHolder = svPreview.getHolder();
        if (surfaceHolder != null) {
            surfaceHolder.addCallback(mSurfaceCallback);
        }
    }

    public void onTvStartCameraPreview(View view) {
        CameraUtils.startPreview(mCamera, mSurfaceHolder);
    }

    public void onTvStopCameraPreview(View view) {
        CameraUtils.stopPreview(mCamera);
    }

    public void onTvSetCameraPreviewCallback(View view) {
        CameraUtils.setPreviewCallback(mCamera, mPreviewCallback);
    }

    public void onTvSetCameraOneShotPreviewCallback(View view) {
        CameraUtils.setOneShotPreviewCallback(mCamera, mPreviewCallback);
    }

    public void onTvSetCameraPreviewCallbackWithBuffer(View view) {
        CameraUtils.setPreviewCallbackWithBuffer(mCamera, mBuffer, mPreviewCallback);
    }

    private byte[] mBuffer  = new byte[3110400];

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d("camera", "onPreviewFrame: ");
            Log.d("camera", "mBuffer: " + mBuffer);
            Log.d("camera", "data: " + data);
        }
    };

    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;

    private SurfaceHolder.Callback mSurfaceCallback =
            new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mSurfaceHolder = holder;
                    CameraUtils.startPreview(mCamera, mSurfaceHolder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mSurfaceHolder = holder;
                    if (mSurfaceHolder.getSurface() == null) {
                        Log.d("camera", "surfaceChanged -> restartPreview failed (surface == null)");
                        return;
                    }
                    CameraUtils.followScreenOrientation(MainActivity.this, mCamera);
                    CameraUtils.stopPreview(mCamera);
                    CameraUtils.startPreview(mCamera, mSurfaceHolder);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    CameraUtils.stopPreview(mCamera);
                    mSurfaceHolder = null;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        CameraUtils.startPreview(mCamera, mSurfaceHolder);
    }

    @Override
    protected void onStop() {
        CameraUtils.stopPreview(mCamera);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        CameraUtils.close(mCamera);
        mCamera = null;
        super.onDestroy();
    }

}
