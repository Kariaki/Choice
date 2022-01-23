package com.kariaki.choice.ui.post.viewer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kariaki.choice.model.database.entities.CloudMessage;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.mainpage.util.MainFunctions;
import com.kariaki.choice.ui.post.PostInfo;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.LastCheck;
import com.kariaki.choice.ui.util.PostLifeSpans;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.kariaki.choice.ui.util.UserNaming;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostVideoViewer extends AppCompatActivity {

    VideoView postViewVideoView;
    ImageView videoViewerPlaybutton, viewVideoBackArrow;
    AppCompatSeekBar videoViewerSeekbar;
    TextView videoViewerDuration;
    ProgressBar videoPlayProgressBar;
    ToggleButton videoPostlikeButton;
    TextView videoPostLikeCount, videoPostCommentCount;

    ImageView videoPostComment, videoPostshareButton;
    RelativeLayout videoPostRootView, videoPlayContainer;
    DatabaseReference databaseReference;
    ImageView videoBackground;
    LinearLayout viewHolder;
    UserInformation information;
    TextView viewsCount;
    RelativeLayout editCommentArea;
    CircleImageView profileImage;

    String url;
    ProgressBar voiceNoteSendProgress;
    Chronometer commentVoiceNoteCounter;
    EmojiEditText write_comment;
    ImageButton commentPageSend,commentPageEmoji,commentPageKeyboard,commentPageMic;

    Post post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video_viewer);

        profileImage=findViewById(R.id.profileImage);
        information = UserInformation.getInstance(this);

        UserNaming.getInstance().loadProfilePicture(information.getMainUserID(),profileImage,this);


        viewByID();

        Intent intent = getIntent();
        View decorView = getWindow().getDecorView();

        decorView.setBackgroundColor(getResources().getColor(R.color.dialogColor));

        int holder = intent.getIntExtra("state", 0);


        if (holder != 0) {
            viewHolder.setVisibility(View.GONE);
            url = intent.getStringExtra("url");


        } else {
             post = intent.getParcelableExtra("view images");

            databaseReference = FirebaseDatabase.getInstance().getReference("post")
                    .child(post.getPostID()).child("likes");
            url = post.getPOST_URL();
            onClickListener();
            Functions.countLikes(videoPostLikeCount, post.getPostID(), post);
            videoPostCommentCount.setTextColor(getResources().getColor(R.color.colorPrimary));
            Functions.countComments(videoPostCommentCount, post.getPostID());
            DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post").child(post.getPostID());
            String myID = new UserInformation(this).getMainUserID();

            postFolder.child("views").child(myID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                updateLifeSpan(postFolder.child("views"), postFolder.child("postData"), PostLifeSpans.VIEWS);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            postFolder.child("views").child(myID).setValue(myID);

            if(post.isRepeat()){
                saveRepeat(post);
            }

        }


        playVideo(url);
        rootviewClick();

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
                if (postViewVideoView.isPlaying()) {
                    int seekPosition = (int) (duration * seekBar.getProgress() / 100);
                    postViewVideoView.seekTo(seekPosition);
                    videoViewerDuration.setText(TimeFactor.convertMillieToHMmSs(postViewVideoView.getCurrentPosition()));

                }
            }
        });


        emojiSetup();

        textWatcher();
        voiceNoteFinishing();


    }

    private void textWatcher(){
        write_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    commentPageMic.setVisibility(View.VISIBLE);
                    commentPageSend.setVisibility(View.INVISIBLE);
                    // privateCommentNote.setVisibility(View.GONE);

                } else {
                    //  camera.setVisibility(View.INVISIBLE);
                    commentPageMic.setVisibility(View.INVISIBLE);
                    commentPageSend.setVisibility(View.VISIBLE);
                   /* if (privateCommentNote.getVisibility() != View.VISIBLE) {
                        privateCommentNote.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInDown).duration(200).playOn(privateCommentNote);
                    }

                    */
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {

                    commentPageMic.setVisibility(View.VISIBLE);
                    commentPageSend.setVisibility(View.INVISIBLE);

                } else {
                    commentPageMic.setVisibility(View.INVISIBLE);
                    commentPageSend.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void onClickComment(View view){
        editCommentArea.setVisibility(View.VISIBLE);
        viewHolder.setVisibility(View.INVISIBLE);

    }
    @Override
    public void onBackPressed() {
        if(viewHolder.getVisibility()==View.VISIBLE){
            finish();
        }else {
            viewHolder.setVisibility(View.VISIBLE);
            editCommentArea.setVisibility(View.GONE);
        }
    }
    private void emojiSetup(){


        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(videoPostRootView).build(write_comment);

        commentPageEmoji.setOnClickListener(v -> {
            commentPageEmoji.setVisibility(View.INVISIBLE);
            commentPageKeyboard.setVisibility(View.VISIBLE);

            emojiPopup.toggle();

        });

        commentPageKeyboard.setOnClickListener(v -> {
            commentPageEmoji.setVisibility(View.VISIBLE);
            commentPageKeyboard.setVisibility(View.INVISIBLE);
            emojiPopup.toggle();

        });
    }

    FloatingActionButton micFloat;

    private void viewByID(){


        postViewVideoView = findViewById(R.id.postViewVideoView);
        videoViewerPlaybutton = findViewById(R.id.videoViewerPlaybutton);
        commentPageMic=findViewById(R.id.commentPageMic);
        editCommentArea=findViewById(R.id.editCommentArea);
        micFloat=findViewById(R.id.micFloat);
        commentPageKeyboard=findViewById(R.id.commentPageKeyboard);
        commentPageEmoji=findViewById(R.id.commentPageEmoji);
        commentPageSend=findViewById(R.id.commentPageSend);
        commentVoiceNoteCounter=findViewById(R.id.commentVoiceNoteCounter);
        videoViewerSeekbar = findViewById(R.id.videoViewerSeekbar);
        write_comment=findViewById(R.id.write_comment);
        videoPostCommentCount = findViewById(R.id.videoPostCommentCount);
        videoPostRootView = findViewById(R.id.videoPostRootView);
        voiceNoteSendProgress=findViewById(R.id.voiceNoteSendProgress);
        videoPostLikeCount = findViewById(R.id.videoPostLikeCount);
        viewHolder = findViewById(R.id.viewHolder);
        videoBackground = findViewById(R.id.blurVideoBackground);
        videoPlayContainer = findViewById(R.id.videoPlayContainer);
        videoPostlikeButton = findViewById(R.id.videoPostlikeButton);
        videoPostshareButton = findViewById(R.id.videoPostshareButton);
        viewVideoBackArrow = findViewById(R.id.viewVideoBackArrow);
        videoPlayProgressBar = findViewById(R.id.videoPlayProgressBar);
        videoViewerDuration = findViewById(R.id.videoViewerDuration);
        videoPlayProgressBar.setVisibility(View.VISIBLE);
    }


    public void onClickListener() {
        videoPostlikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YoYo.with(Techniques.Pulse).duration(200).playOn(videoPostlikeButton);
                if (isChecked) {
                    databaseReference.child(information.getMainUserID()).setValue(information.getMainUserID());
                    videoPostLikeCount.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    databaseReference.child(information.getMainUserID()).removeValue();
                }

            }
        });
    }

    public void videoBackPress(View view) {
        onBackPressed();
    }


    public void playVideo(String videoUrl) {

        postViewVideoView.setVideoURI(Uri.parse(videoUrl));
        postViewVideoView.requestFocus();


        // postViewVideoView.start();
        videoPlayProgressBar.setMax(100);

        postViewVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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

        postViewVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoViewerPlaybutton.setImageResource(R.drawable.play_filled);
            }
        });




        videoViewerPlaybutton.setOnClickListener(v -> {
            YoYo.with(Techniques.SlideInLeft).duration(150).playOn(videoViewerPlaybutton);
            if (postViewVideoView.isPlaying()) {
                videoViewerPlaybutton.setImageResource(R.drawable.play_filled);
                postViewVideoView.pause();
            } else {
                videoViewerPlaybutton.setImageResource(R.drawable.pause_filled);
                postViewVideoView.start();
            }

        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Position", postViewVideoView.getCurrentPosition());
        postViewVideoView.pause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    int position;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        postViewVideoView.seekTo(position);
    }


    private int duration = 0;

    private void setDuration() {
        duration = postViewVideoView.getDuration();
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
        int current = postViewVideoView.getCurrentPosition();
        videoViewerDuration.setText(TimeFactor.convertMillieToHMmSs(current));
        int progress = current * 100 / duration;
        videoViewerSeekbar.setProgress(progress);
    }


    public void rootviewClick() {
        videoPostRootView.setOnClickListener(v -> {

            handleView();



        });
    }

    private void updateLifeSpan(DatabaseReference likeList, DatabaseReference postInfo, int minutes_to_add) {
        postInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostInfo info = dataSnapshot.getValue(PostInfo.class);

                likeList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                        long newLifeSpan = TimeFactor.updateLifeSpan(info.getPostLifeSpan(), minutes_to_add, (int) dataSnapshot1.getChildrenCount());
                        Map<String, Object> lifeSpanUpdate = new HashMap<>();
                        lifeSpanUpdate.put("postLifeSpan", newLifeSpan);
                        postInfo.updateChildren(lifeSpanUpdate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    DatabaseReference liveUsers=FirebaseDatabase.getInstance().getReference("users");
    public void saveRepeat(Post cloudPost) {

        if (!cloudPost.getOwnerID().equals(information.getMainUserID())) {

            if (cloudPost.isRepeat()) {

                DatabaseReference myrepeatPost = liveUsers.child(information.getMainUserID())
                        .child("post").child(cloudPost.getPostID());
                myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

            }

        }
    }

    String outputSourceFile;
    String outputFile = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/choice";
    MediaRecorder recorder;
    long startTime;
    public static final double maxRecordingDuration=1.5;


    public void startRecording() {

        File choiceFolder = new File(outputFile);
        if (!choiceFolder.exists()) {
            choiceFolder.mkdir();
        }


        if (recorder == null) {
            recorder = new MediaRecorder();
            outputSourceFile = outputFile + "/" + System.currentTimeMillis() + ".3gp";
            recorder.setOutputFile(outputSourceFile);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_WB);

            int maxDurationInMunite=1000*60;
            recorder.setMaxDuration((int)(maxDurationInMunite*maxRecordingDuration));

            try {
                recorder.prepare();
                startTime = System.currentTimeMillis();
                recorder.start();
            } catch (IllegalStateException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }
    boolean messageSending=false;
    public void stopRecording() {


        StorageReference chatFiles= FirebaseStorage.getInstance().getReference("post").child(String.valueOf(post.getPOST_TIME()));


        long duration = System.currentTimeMillis() - startTime;
        long messageTime = System.currentTimeMillis();

        String text = String.valueOf(duration);

        recorder.release();
        recorder = null;
        if ((duration / 1000) != 0) {
            Message message = new Message("", "", + MessageType.VOICE_NOTE,
                    information.getMainUserID(), messageTime,
                    text, outputSourceFile, MessageType.STATE_SEND, MessageState.SENDING);


            StorageReference voicenoteFile = chatFiles.child(System.currentTimeMillis() + "." + Functions.fileExtension((outputSourceFile)));


            voicenoteFile.putFile(Uri.fromFile(new File(outputSourceFile)))
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            int progress= (int) (taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount());
                            voiceNoteSendProgress.setVisibility(View.VISIBLE);
                            voiceNoteSendProgress.setProgress(progress);
                            profileImage.setVisibility(View.INVISIBLE);
                            messageSending=true;

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    voicenoteFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String voiceNoteURI=uri.toString();
                            CloudMessage cloudMessage = new CloudMessage(message.getMessageTYPE(),
                                    new UserInformation(PostVideoViewer.this).getMainUserID(),
                                    message.getMessageTIME(),
                                    message.getMessageCONTENT(), voiceNoteURI, MessageState.SENT);
                            DatabaseReference thisPost = postFOLDER.child(post.getPostID());
                            DatabaseReference thisPostComments = thisPost.child("comments").push();
                            thisPostComments.setValue(cloudMessage);
                            profileImage.setVisibility(View.VISIBLE);
                            MainFunctions.createNotification(post.getOwnerID(),cloudMessage.getMessageOwnerID(),post.getPostID(),thisPostComments.getKey());

                            voiceNoteSendProgress.setVisibility(View.GONE);
                            messageSending=false;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostVideoViewer.this, "Sending failed", Toast.LENGTH_SHORT).show();
                            profileImage.setVisibility(View.VISIBLE);
                            voiceNoteSendProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });

        }


    }


    private void voiceNoteFinishing() {

        commentPageMic.setOnClickListener(v -> {
            Toast.makeText(this, "long press to record", Toast.LENGTH_SHORT).show();
        });

        commentPageMic.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    if(!messageSending) {
                        write_comment.setVisibility(View.INVISIBLE);
                        commentVoiceNoteCounter.setVisibility(View.VISIBLE);
                        commentVoiceNoteCounter.setBase(SystemClock.elapsedRealtime());
                        commentVoiceNoteCounter.start();

                        YoYo.with(Techniques.Pulse).repeat(YoYo.INFINITE).playOn(micFloat);
                        commentPageEmoji.setVisibility(View.INVISIBLE);
                        micFloat.setVisibility(View.VISIBLE);

                        startRecording();
                    }else {
                        Toast.makeText(this, "You can't record while sending", Toast.LENGTH_SHORT).show();
                    }


                    break;
                case MotionEvent.ACTION_UP:
                    write_comment.setVisibility(View.VISIBLE);
                    commentVoiceNoteCounter.setVisibility(View.INVISIBLE);
                    commentVoiceNoteCounter.setBase(SystemClock.elapsedRealtime());
                    commentVoiceNoteCounter.stop();
                    commentPageEmoji.setVisibility(View.VISIBLE);

                    micFloat.setVisibility(View.INVISIBLE);

                    stopRecording();
                    break;

            }
            return true;
        });
    }

    DatabaseReference postFOLDER=FirebaseDatabase.getInstance().getReference("post");

    public void sendComment(View view) {

        String text = write_comment.getText().toString();
        long messageTime = System.currentTimeMillis();


        if (!text.isEmpty()) {
            CloudMessage comment = new CloudMessage(MessageType.TEXT, information.getMainUserID()
                    , messageTime,
                    text, "", MessageState.SENT);
            DatabaseReference thisPost = postFOLDER.child(post.getPostID());
            DatabaseReference thisPostComments = thisPost.child("comments").push();
            thisPostComments.setValue(comment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PostVideoViewer.this, "Sending comment", Toast.LENGTH_SHORT).show();
                        }
                    });
            MainFunctions.createNotification(post.getOwnerID(),comment.getMessageOwnerID(),post.getPostID(),thisPostComments.getKey());

        }

        write_comment.setText("");


    }


    private void handleView() {

        if (videoPlayContainer.getVisibility() == View.GONE) {

            videoPlayContainer.setVisibility(View.VISIBLE);
            //topAppbar.setVisibility(View.VISIBLE);

            showStatusBar();
        } else {

            videoPlayContainer.setVisibility(View.GONE);
            //topAppbar.setVisibility(View.GONE);
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
}
