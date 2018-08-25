package com.afirez.app.face.detection;

import android.graphics.Bitmap;

public class FaceDetection {

    static {
        System.loadLibrary("face-detection");
    }

    public native int detect(Bitmap bitmap);

    public native int loadCascade(String filePath);
}
