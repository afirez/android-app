package com.example.videoplayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView vvVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vvVideo = (VideoView) findViewById(R.id.vp_vv_video);
        // local source
        //vvVideo.setVideoPath("");
        // cloud source
        vvVideo.setVideoURI(Uri.parse(""));

        MediaController controller = new MediaController(this);
        vvVideo.setMediaController(controller);
        controller.setMediaPlayer(vvVideo);
    }
}
