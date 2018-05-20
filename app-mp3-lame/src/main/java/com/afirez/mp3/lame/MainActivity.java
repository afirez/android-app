package com.afirez.mp3.lame;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Mp3Encoder";
    private Button btnEncodeMp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnEncodeMp3 = (Button) findViewById(R.id.mp3_encoder_btn);
        btnEncodeMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        Mp3Encoder encoder = new Mp3Encoder();
                        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String pcmPath = sdcardPath + "/vocal.pcm";
                        int channels = 2;
                        int bitRate = 128 * 1024;
                        String mp3Path = sdcardPath + "/vocal.mp3";
                        int sampleRate = 44100;
                        int code = encoder.init(pcmPath, channels, bitRate, sampleRate, mp3Path);
                        if (code >= 0) {
                            encoder.encode();
                            encoder.destroy();
                            Log.i(TAG, "Encode Mp3 Success");
                        } else {
                            Log.i(TAG, "Encoder initialed failed...");
                        }
                    }
                }.start();
            }
        });
    }

    static {
        System.loadLibrary("mp3-encoder");
    }
}
