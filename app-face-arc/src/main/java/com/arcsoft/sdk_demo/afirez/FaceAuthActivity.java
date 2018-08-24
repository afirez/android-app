package com.arcsoft.sdk_demo.afirez;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.R;
import com.guo.android_extend.widget.ExtImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class FaceAuthActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private CameraHandlerThread cameraHandlerThread;
    private volatile boolean hasPreview;
    private TextView tvTips;

    public FaceRecongnization faceRecongnization;
    private int cameraID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cameraID = getIntent().getIntExtra("Camera", 0) == 0 ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
        setContentView(R.layout.activity_face_auth);
        faceRecongnization = new FaceRecongnization(this);
        surfaceView = (SurfaceView) findViewById(R.id.sv_preview);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        cameraHandlerThread = new CameraHandlerThread(this);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cameraHandlerThread.openCamera(cameraID);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraHandlerThread.releaseCamera();
            }
        });
        cameraHandlerThread.setCameraCallback(new CameraHandlerThread.CameraCallback() {
            @Override
            public void onOpen(Camera camera) {
                if (camera == null) {
                    tvTips.setTextColor(Color.RED);
                    tvTips.setText("相机被占用");
                    return;
                }
                try {
                    camera.setPreviewDisplay(surfaceView.getHolder());
                    camera.startPreview();
                    camera.setPreviewCallbackWithBuffer(previewCallback);
                    if (task == null) {
                        task = new FrameTask();
                        task.setCallback(callback);
                    }
                    byte[] nextBuffer = task.obtainData(cameraHandlerThread.bufferSize);
                    cameraHandlerThread.camera.addCallbackBuffer(nextBuffer);
                    hasPreview = true;
                } catch (Throwable e) {
                    e.printStackTrace();
                    cameraHandlerThread.releaseCamera();
                    hasPreview = false;
                }
            }
        });
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, final Camera camera) {
            if (task == null) {
                task = new FrameTask();
                task.setCallback(callback);
            }
            task.post(data);
        }

    };

    private FrameTask task;

    private FrameTask.Callback callback = new FrameTask.Callback() {
        boolean next = true;

        @Override
        public void onPostFrame(byte[] buffer) {
            if (next) {
                cameraHandlerThread.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Camera camera = cameraHandlerThread.camera;
                        byte[] nextBuffer = task.obtainData(cameraHandlerThread.bufferSize);
                        camera.addCallbackBuffer(nextBuffer);
                    }
                }, 1000);
            }
        }

        @Override
        public void onFrame(byte[] buffer) {
            final List<AFT_FSDKFace> faces = faceRecongnization.detect(
                    buffer,
                    cameraHandlerThread.width,
                    cameraHandlerThread.height
            );
            next = true;
            if (faces.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTips.setTextColor(Color.RED);
                        tvTips.setText("未检测到人脸");
                    }
                });
            } else if (faces.size() > 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTips.setTextColor(Color.RED);
                        tvTips.setText(String.format("检测到%s张人脸", faces.size()));
                    }
                });
            } else {
                final FaceRecongnization.RecognizeResult result = faceRecongnization.recognize(
                        buffer,
                        cameraHandlerThread.width,
                        cameraHandlerThread.height,
                        faces.get(0)
                );
                if (TextUtils.isEmpty(result.name) || result.score < 0.6f) {
                    // no face registered
                    next = false;
                    YuvImage yuvImage = new YuvImage(buffer, ImageFormat.NV21, cameraHandlerThread.width, cameraHandlerThread.height, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(faces.get(0).getRect(), 80, baos);
                    byte[] cropped = baos.toByteArray();
                    final Bitmap croppedBitmap = BitmapFactory.decodeByteArray(cropped, 0, cropped.length);
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTips.setText("");
                            LayoutInflater inflater = LayoutInflater.from(FaceAuthActivity.this);
                            View layout = inflater.inflate(R.layout.dialog_register, null);
                            final EditText editText = (EditText) layout.findViewById(R.id.editview);
                            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                            ExtImageView imageView = (ExtImageView) layout.findViewById(R.id.extimageview);
                            imageView.setImageBitmap(croppedBitmap);
                            imageView.setRotation(faces.get(0).getDegree());
                            new AlertDialog.Builder(FaceAuthActivity.this)
                                    .setTitle("我以前不认识你吧，怎么称呼啊？")
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setView(layout)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((Application) FaceAuthActivity.this.getApplicationContext())
                                                    .mFaceDB.addFace(editText.getText().toString(), result.faceId);
                                            dialog.dismiss();
                                            cameraHandlerThread.handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Camera camera = cameraHandlerThread.camera;
                                                    byte[] nextBuffer = task.obtainData(cameraHandlerThread.bufferSize);
                                                    camera.addCallbackBuffer(nextBuffer);
                                                }
                                            }, 1000);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            cameraHandlerThread.handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Camera camera = cameraHandlerThread.camera;
                                                    byte[] nextBuffer = task.obtainData(cameraHandlerThread.bufferSize);
                                                    camera.addCallbackBuffer(nextBuffer);
                                                }
                                            }, 1000);
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTips.setTextColor(Color.GREEN);
                            tvTips.setText(String.format("FaceId: %s\n可信度：%s", result.name, result.score));
                        }
                    });
                }
            }

        }

        @Override
        public void onPreFrame(byte[] buffer) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraHandlerThread != null) {
            cameraHandlerThread.releaseCamera();
            cameraHandlerThread.quit();
            cameraHandlerThread = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (faceRecongnization != null) {
            faceRecongnization.destroy();
        }
    }
}