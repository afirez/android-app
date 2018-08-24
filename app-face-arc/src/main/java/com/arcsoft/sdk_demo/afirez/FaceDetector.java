package com.arcsoft.sdk_demo.afirez;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.sdk_demo.FaceDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FaceDetector {

    private final AFD_FSDKEngine engine;
    private final AFD_FSDKVersion version;
    private Paint paint = new Paint();
    private Rect src = new Rect();
    private Rect dst = new Rect();

    public FaceDetector() {
        engine = new AFD_FSDKEngine();
        version = new AFD_FSDKVersion();

    }

    public void detect(byte[] data, Bitmap bitmap, SurfaceHolder holder, Callback callback) {
        src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        AFD_FSDKError error = engine.AFD_FSDK_InitialFaceEngine(
                FaceDB.appid,
                FaceDB.fd_key,
                AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT,
                16,
                5
        );
        if (error.getCode() != AFD_FSDKError.MOK) {
            if (callback != null) {
                callback.onDetected(data, Collections.<AFD_FSDKFace>emptyList());
            }
            return;
        }
        error = engine.AFD_FSDK_GetVersion(version);
        ArrayList<AFD_FSDKFace> faces = new ArrayList<>();
        error = engine.AFD_FSDK_StillImageFaceDetection(
                data,
                bitmap.getWidth(),
                bitmap.getHeight(),
                AFD_FSDKEngine.CP_PAF_NV21,
                faces
        );
        Canvas canvas = holder.lockCanvas();
        try {
            if (canvas != null) {
                boolean fitHorizontal = 1.0f * canvas.getWidth() / src.width()
                        < 1.0f * canvas.getHeight() / src.height();
                float scale;
                if (fitHorizontal) {
                    scale = 1.0f * canvas.getWidth() / src.width();
                    dst.left = 0;
                    dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
                    dst.right = dst.left + canvas.getWidth();
                    dst.bottom = dst.top + canvas.getHeight();
                } else {
                    scale = 1.0f * canvas.getHeight() / src.height();
                    dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
                    dst.top = 0;
                    dst.right = dst.left + (int) (src.width() * scale);
                    dst.bottom = dst.top + canvas.getHeight();
                }
                canvas.drawBitmap(bitmap, src, dst, paint);
                canvas.save();
                canvas.scale(1.0f * dst.width() / src.width(), 1.0f * dst.height() / src.height());
                canvas.translate(dst.left / scale, dst.top / scale);
                paint.setColor(faces.size() == 1 ? Color.GREEN : Color.RED);
                paint.setStrokeWidth(10f);
                paint.setStyle(Paint.Style.STROKE);
                for (AFD_FSDKFace face : faces) {
                    canvas.drawRect(face.getRect(), paint);
                }
                canvas.restore();
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
            if (callback != null) {
                callback.onDetected(data, faces);
            }
            engine.AFD_FSDK_UninitialFaceEngine();
        }

    }

    public interface Callback {
        void onDetected(byte[] data, List<AFD_FSDKFace> faces);
    }
}
