package com.kariaki.choice.ui.Chat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.Time.TimeFactor;

import java.util.Timer;
import java.util.TimerTask;

public class ChatVideoViewer extends AppCompatActivity {

    VideoView videoPreview;
    RelativeLayout topAppbar;
    RelativeLayout downAppbar;
    RelativeLayout rootView;
    AppCompatSeekBar videoViewerSeekbar;
    TextView videoViewerDuration;
    ProgressBar videoPlayProgressBar;
    ImageView videoViewerPlaybutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_video_viewer);

        videoPreview = findViewById(R.id.videoPreview);
        topAppbar = findViewById(R.id.topAppbar);
        rootView = findViewById(R.id.rootView);
        videoViewerPlaybutton = findViewById(R.id.videoViewerPlaybutton);
        videoPlayProgressBar = findViewById(R.id.videoPlayProgressBar);
        videoViewerSeekbar = findViewById(R.id.videoViewerSeekbar);
        videoViewerDuration = findViewById(R.id.videoViewerDuration);
        downAppbar = findViewById(R.id.downAppbar);

        Intent image_to_view = getIntent();
        String url = image_to_view.getStringExtra("url");
        int messageState = image_to_view.getIntExtra("state", 1);
        String fileExtension=image_to_view.getStringExtra("message content");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        playVideo(url);
        View decorView = getWindow().getDecorView();

        decorView.setBackgroundColor(getResources().getColor(R.color.dialogColor));
        rootView.setOnClickListener(v -> {
            handleView();
        });
        videoPreview.setOnClickListener(v -> {
            handleView();
        });

        videoViewerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (videoPreview.isPlaying()) {
                    int seekPosition = (int) (duration * seekBar.getProgress() / 100);
                    videoPreview.seekTo(seekPosition);
                    videoViewerDuration.setText(TimeFactor.convertMillieToHMmSs(videoPreview.getCurrentPosition()));

                }
            }
        });

    }

    private void downloadFile(String url,String fileExtension){

    }

    private void handleView() {
        if (downAppbar.getVisibility() == View.GONE) {
            downAppbar.setVisibility(View.VISIBLE);
            topAppbar.setVisibility(View.VISIBLE);


            showStatusBar();
        } else {
            downAppbar.setVisibility(View.GONE);
            topAppbar.setVisibility(View.GONE);
            hideStatusBar();

        }
    }


    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uioption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uioption);
    }

    private void showStatusBar() {

        View decorView = getWindow().getDecorView();
        decorView.setBackgroundColor(getResources().getColor(R.color.dialogColor));
        int uioption = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uioption);
    }

    public void backClick(View view) {
        onBackPressed();
    }


    private int duration = 0;

    private void setDuration() {
        duration = videoPreview.getDuration();
    }

    private Timer timer;

    private void timerCounter() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUI() {
        if (videoViewerSeekbar.getProgress() >= 100) {
            timer.cancel();
        }
        int current = videoPreview.getCurrentPosition();
        videoViewerDuration.setText(TimeFactor.convertMillieToHMmSs(current));
        int progress = current * 100 / duration;
        videoViewerSeekbar.setProgress(progress);
    }


    public void playVideo(String videoUrl) {

        videoPreview.setVideoURI(Uri.parse(videoUrl));
        videoPreview.requestFocus();


        // postViewVideoView.start();

        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.start();
                videoPlayProgressBar.setVisibility(View.GONE);
                videoViewerDuration.setText(TimeFactor.convertMillieToHMmSs(mp.getCurrentPosition()));
                setDuration();
                timerCounter();

            }
        });

        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoViewerPlaybutton.setImageResource(R.drawable.play_filled);
            }
        });


        videoViewerPlaybutton.setOnClickListener(v -> {
            YoYo.with(Techniques.SlideInLeft).duration(150).playOn(videoViewerPlaybutton);
            if (videoPreview.isPlaying()) {
                videoViewerPlaybutton.setImageResource(R.drawable.play_filled);
                videoPreview.pause();
            } else {
                videoViewerPlaybutton.setImageResource(R.drawable.pause_filled);
                videoPreview.start();
            }

        });


    }

}
