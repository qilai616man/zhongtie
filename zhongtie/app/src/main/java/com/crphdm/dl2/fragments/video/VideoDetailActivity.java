package com.crphdm.dl2.fragments.video;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.crphdm.dl2.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoDetailActivity extends AppCompatActivity {
    private JCVideoPlayerStandard playerStandard;
    TextView  textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        textView = (TextView) findViewById(R.id.video_name_id);
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String videoTitle = getIntent().getStringExtra("videoTitle");
        playerStandard = (JCVideoPlayerStandard) findViewById(R.id.playerstandard);
        playerStandard.setUp(videoUrl,JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,videoTitle.replace("_batch.mp4",""));
        playerStandard.startVideo();
        textView.setText(videoTitle.replace("_batch.mp4",""));
    }
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
