package com.kariaki.choice.UI.MakePost;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.kariaki.choice.R;
import com.kariaki.choice.UI.Post.PostTypes;
import com.kariaki.choice.UI.util.Time.TimeFactor;

import org.jetbrains.annotations.NotNull;

import me.bendik.simplerangeview.SimpleRangeView;

public class VideoPost extends AppCompatActivity {


    VideoView videoView;
    Button videoPostNextButton;

    String videoURL;
    ImageView CreateVideoPlayPauseButton;

    TextView videoDurationTextView;
    SimpleRangeView videoRangeView;
    int videoDuration;
    int currentPosition;
    //MediaMetadataRetriever retriever;
    private final int MAX_VIDEO_DURATION = 190 - 10;

    private MediaPlayer videMediaPlayer;
    long startcut = 0;
    long endCut = 1;
    ProgressBar videviewProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_post);


        videoView = findViewById(R.id.createVideoPostVideoView);

        CreateVideoPlayPauseButton = findViewById(R.id.CreateVideoPlayPauseButton);
        videoPostNextButton = findViewById(R.id.videoPostNextButton);
        videoDurationTextView = findViewById(R.id.videoDuration);
        videviewProgressBar = findViewById(R.id.videviewProgressBar);

        videoRangeView = findViewById(R.id.videoRangeView);

        Intent videoIntent = getIntent();
        videoURL = videoIntent.getStringExtra("videoURL");


        videoView.setVideoURI(Uri.parse(videoURL));
        videoView.setKeepScreenOn(true);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                videoDurationTextView.setText(TimeFactor.convertMillieToHMmSs(videoView.getDuration()));

                videoDuration=mp.getDuration();

            }
        });



        videoRangeView.setCount(100);

        int seekMax;
        if (videoDuration <= MAX_VIDEO_DURATION) {
            seekMax = 100;

            videoDurationTextView.setText(TimeFactor.convertMillieToHMmSs(videoDuration));
            videoRangeView.setStart(0);
            videoRangeView.setEnd(100);

        } else {
            seekMax = (int) (((double) MAX_VIDEO_DURATION / videoDuration) * 100);
            videoRangeView.setStart(0);
            videoRangeView.setEnd(seekMax);
            videoDurationTextView.setText(String.valueOf(((double) MAX_VIDEO_DURATION / 60)));
        }


        videoRangeView.setMaxDistance(seekMax);



        videoRangeView.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onStartRangeChanged(@NotNull SimpleRangeView simpleRangeView, int i) {

                double doubledPosition = ((double) i * videoView.getDuration()) / 100;

                currentPosition = (int) doubledPosition;
                int currentPositionInSec = currentPosition ; //current position in seconds
                videoView.seekTo(currentPositionInSec);
                doubledPosition = doubledPosition / 100;
                String aim = String.valueOf(doubledPosition);
                String[] point = aim.split(".");

                //String leave=point[0];
                //+point[1].substring(0,1);
                videoDurationTextView.setText(TimeFactor.convertMillieToHMmSs(videoView.getCurrentPosition()));


            }

            @Override
            public void onEndRangeChanged(@NotNull SimpleRangeView simpleRangeView, int i) {



                //videoDurationTextView.setText(String.valueOf(i));
            }
        });

        CreateVideoPlayPauseButton.setOnClickListener(v -> {


            if (!videoView.isPlaying()) {

                videoView.start();
                CreateVideoPlayPauseButton.setImageResource(R.drawable.pause);

            } else {
                videoView.pause();
                CreateVideoPlayPauseButton.setImageResource(R.drawable.play_icon);
            }
        });




    }


    public void createVideoPostBackPress(View view) {
        onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!videoView.isPlaying()) {

            CreateVideoPlayPauseButton.setImageResource(R.drawable.play_icon);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void videoPostNext(View view) {

        Intent intent = new Intent(this, FinishPost.class);
        intent.putExtra("videoURL", videoURL);
        intent.putExtra("postType", PostTypes.VIDEO_POST);
        intent.putExtra("cut from ", new int[]{(int) 1024, (int) 1024*1024});
        startActivity(intent);


    }

}
