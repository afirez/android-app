package com.afirez.app.face.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView ivFace;
    private Bitmap bitmap;
    private FaceDetection faceDetection;
    private File cascadeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivFace = findViewById(R.id.iv_face);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my2);
        ivFace.setImageBitmap(bitmap);
        faceDetection = new FaceDetection();
        copyCascade();
        String path = cascadeFile.getAbsolutePath();
        Log.i(TAG, "onCreate: " + path);
        faceDetection.loadCascade(path);
    }

    private void copyCascade() {
        try {
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            if (cascadeFile.exists()) {
                return;
            }

            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    public void onFaceDetection(View view) {
        long start = System.currentTimeMillis();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my2);
        int count = faceDetection.detect(bitmap);
        Toast.makeText(this, String.format("检测到 %s 个人脸", count), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onFaceDetection: time = " + (System.currentTimeMillis() - start));
        ivFace.setImageBitmap(bitmap);
    }
}
