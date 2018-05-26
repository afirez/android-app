package com.afirez.camera;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 相机使用步骤:
 * 1. 打开相机
 * 2. 预览相机
 * 3. 设置预览回调
 * 1. setPreviewCallback
 * 2. setOneShotPreviewCallback
 * 3. setPreviewCallbackWithBuffer / addCallbackBuffer
 * 4. 预览回调数据处理
 * <p>
 * 注意：
 * 1. 设置预览回调时，setPreviewCallback 和 setOneShotPreviewCallback 都存在内存抖动
 * 2. Camera API 在打开相机是在哪个线程（该线程含 Looper），那么 onPreviewFrame 回调执行就在哪个线程
 * 3. onPreviewFrame方法中不要执行过于复杂的逻辑操作，这样会阻塞Camera，无法获取新的Frame，导致帧率下降
 */
public class MainActivity extends AppCompatActivity {

    private SurfaceView svPreview;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;

    private int mCameraId;

    private int previewWidth = 1280;
    private int previewHeight = 720;
    private int fps = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mCameraId = CameraUtils.getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCamera = CameraUtils.openById(mCameraId);
    }

    private void initView() {
        svPreview = (SurfaceView) findViewById(R.id.camera_sv_preview);
        SurfaceHolder surfaceHolder = svPreview.getHolder();
        if (surfaceHolder != null) {
            surfaceHolder.addCallback(mSurfaceCallback);
        }
    }

    public void onTvSwitchCamera(View view) {
        CameraUtils.close(mCamera);
        mCameraId = 1 - mCameraId;
        mCamera = CameraUtils.openById(mCameraId);
        configCamera();
        CameraUtils.startPreview(mCamera, mSurfaceHolder);
    }

    public void onTvCameraMirror(View view) {

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
        CameraUtils.setPreviewCallbackWithBuffer(mCamera, mPreviewCallback);
    }

    public void onTvAddCameraCallbackBuffer(View view) {
        CameraUtils.addCallbackBuffer(mCamera, getBuffer());
    }

    // 3110400
    private volatile byte[] mBuffer;

    public byte[] getBuffer() {
        if (mBuffer == null) {
            int size = CameraUtils.calculateBufferSize(mCamera);
            mBuffer = new byte[size];
        }
        Log.d("camera", "getBuffer: size : " + mBuffer.length);
        return mBuffer;
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d("camera", "onPreviewFrame: ");
            Log.d("camera", "mBuffer: " + getBuffer());
            Log.d("camera", "data: " + data);
        }
    };

    private SurfaceHolder.Callback mSurfaceCallback =
            new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mSurfaceHolder = holder;
                    configCamera();
                    CameraUtils.startPreview(mCamera, mSurfaceHolder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mSurfaceHolder = holder;
                    if (mSurfaceHolder.getSurface() == null) {
                        Log.d("camera", "surfaceChanged -> restartPreview failed (surface == null)");
                        return;
                    }
                    configCamera();
                    CameraUtils.stopPreview(mCamera);
                    CameraUtils.startPreview(mCamera, mSurfaceHolder);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    CameraUtils.stopPreview(mCamera);
                    mSurfaceHolder = null;
                }
            };

    private void configCamera() {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            CameraUtils.calculateAndSetFps(parameters, CameraUtils.calculateAndSetFps(parameters, fps));
            Camera.Size size = CameraUtils.calculateSize(parameters.getSupportedPreviewSizes(), previewWidth, previewHeight);
            parameters.setPreviewSize(size.width, size.height);
            int degrees = CameraUtils.calculateRotation(MainActivity.this, mCameraId);
            mCamera.setDisplayOrientation(degrees);
            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        configCamera();
        CameraUtils.startPreview(mCamera, mSurfaceHolder);
    }

    @Override
    protected void onStop() {
        CameraUtils.stopPreview(mCamera);
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configCamera();
    }

    @Override
    protected void onDestroy() {
        CameraUtils.close(mCamera);
        mCamera = null;
        super.onDestroy();
    }

}