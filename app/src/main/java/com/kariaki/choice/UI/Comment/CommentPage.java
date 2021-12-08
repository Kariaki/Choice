package com.kariaki.choice.UI.Comment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.Database.Entities.CloudMessage;
import com.kariaki.choice.Model.Database.Entities.ContactPost;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.Database.Entities.NotificationForPost;
import com.kariaki.choice.Model.Database.MessageState;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.Model.Post;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChannelTypes;
import com.kariaki.choice.UI.Chat.Channels;
import com.kariaki.choice.UI.Chat.ChatPage;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.UI.Messaging.SwipeReply;
import com.kariaki.choice.UI.Post.Adapter.MergedPostViewHolder;
import com.kariaki.choice.UI.Post.Adapter.PostAdapter;
import com.kariaki.choice.UI.Post.Adapter.PostMainViewHolder;
import com.kariaki.choice.UI.Post.Adapter.SinglePostViewHolder;
import com.kariaki.choice.UI.Post.Adapter.TextViewHolder;
import com.kariaki.choice.UI.Post.Adapter.VideoPostViewHolder;
import com.kariaki.choice.UI.Post.PostInfo;
import com.kariaki.choice.UI.Post.PostTypes;
import com.kariaki.choice.UI.Post.Viewer.PostImageViewer;
import com.kariaki.choice.UI.Post.Viewer.PostVideoViewer;
import com.kariaki.choice.UI.Profiles.PartialProfile;
import com.kariaki.choice.UI.Profiles.SettingsPage;
import com.kariaki.choice.UI.Profiles.SocialProfile;
import com.kariaki.choice.UI.util.FastBlur;
import com.kariaki.choice.UI.util.Functions;
import com.kariaki.choice.UI.util.LastCheck;
import com.kariaki.choice.UI.DialogBox.OptionBottomSheet;
import com.kariaki.choice.UI.util.PostLifeSpans;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.kariaki.choice.UI.util.UserNaming;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPage extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView allComments;
    private ImageButton addButton, sendButton, commentMic, cameraButton, commentPageBackButton;
    private RelativeLayout commentPageContainer;
    private TextView privateCommentNote;
    private Chronometer commentVoiceNoteCounter;
    private EmojiEditText writeMessage;
    private ImageView mic, camera, send, emoji, keyboard;
    private RecyclerView messageContainer;
    ChatAdapter adapter;
    List<Message> messages = new ArrayList<>();
    EmojiPopup popup;
    private String postFolder = "post";
    private String postComments = "comments";
    private List<String> commentIDs = new ArrayList<>();
    FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    UserInformation information;
    private DatabaseReference userToChatWith;
    private DatabaseReference chatChannels;
    private DatabaseReference myChats;
    DatabaseReference chatFolder;
    DatabaseReference otherChatFolder;
    DatabaseReference reference;
    StorageReference chatFiles;
    private static final String LIST_OF_USERS = "users";
    private PostAdapter postAdapter;
    private boolean willRepeat = true;
    private DatabaseReference postFOLDER = DATABASE.getReference(postFolder);
    DatabaseReference realtimeUser = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);
    LinearLayoutManager manager;
    TextView commentPageText;
    int currentLimit = 25;
    Message messageToReply = null;
    private Post cloudPost;
    ChoiceViewModel viewModel;
    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_comment_page);
        context = this;

        viewById();
        setSupportActionBar(toolbar);


        information = new UserInformation(this);
        emoji = findViewById(R.id.commentPageEmoji);
        keyboard = findViewById(R.id.commentPageKeyboard);
        commentPageContainer = findViewById(R.id.commentPageContainer);


        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(commentPageContainer).build(writeMessage);

        postAdapter = new PostAdapter(this);

        loadTheme();

        emoji.setOnClickListener(v -> {
            emoji.setVisibility(View.INVISIBLE);
            keyboard.setVisibility(View.VISIBLE);
            emojiPopup.toggle();

        });

        keyboard.setOnClickListener(v -> {
            emoji.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.INVISIBLE);
            emojiPopup.toggle();

        });

        send.setOnLongClickListener(v -> {
          /*  if (replyHolder.getVisibility() != View.VISIBLE) {
                sendPrivateComment(v);
            }

           */
            return true;
        });

        //checkOnline();

        writeMessage.setOnClickListener(v -> {
            if (emojiPopup.isShowing()) {
                emojiPopup.toggle();
                emoji.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.INVISIBLE);

            }
        });
        viewModel = ChoiceViewModel.getInstance(getApplication());
        writeMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    camera.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);

                } else {
                    camera.setVisibility(View.INVISIBLE);
                    mic.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    camera.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);

                } else {
                    camera.setVisibility(View.GONE);
                    mic.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        adapter = new ChatAdapter(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        switch (currentTheme) {
            case Theme.LIGHT:
                postAdapter.setColor(R.color.textColor);
                break;
            case Theme.DEEP:
                postAdapter.setColor(R.color.whiteGreen);
                break;
        }
        adapter.setTheme(currentTheme);
        adapterData();

        adapter.setMessage(messages);
        manager = new LinearLayoutManager(this);
        messageContainer.setHasFixedSize(true);
        messageContainer.setLayoutManager(manager);
        messageContainer.setNestedScrollingEnabled(true);
        messageContainer.setAdapter(adapter);

        postData();
        // privateMessageinstances();
        // voiceNoteFinishing();
        commentPageBackButton.setOnClickListener(v -> {
            onBackPressed();
        });

        camera.setOnClickListener(v -> {
            //startActivity(new Intent(this, ChoiceCamera.class));

        });


        loadTheme();

        SwipeReply swipeReply = new SwipeReply(this) {
            @Override
            public void swipeAction(int position) {
                Message message = messages.get(position);
                messageToReply = message;
                messageToReply2 = message;
//                messageOptionsHolder.setVisibility(View.GONE);
                processMessageToReply(message);

            }
        };
        replyCancelButton.setOnClickListener(v -> {
            replyHolder.setVisibility(View.GONE);
            generalMessageType = 0;
            messageToReply = null;
        });


        new ItemTouchHelper(swipeReply)

                .attachToRecyclerView(messageContainer);

        replyCancelButton.setOnClickListener(v -> {
            replyHolder.setVisibility(View.GONE);
            generalMessageType = 0;
            messageToReply = null;
        });

        voiceNoteFinishing();

        chatFiles = FirebaseStorage.getInstance().getReference("post").child(String.valueOf(cloudPost.getPOST_TIME()));

        Intent intent = getIntent();
        boolean notification = intent.getBooleanExtra("notification", false);
        String messageID = intent.getStringExtra("message id");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    if (notification) {
                        retrieveCommentsForNotification(messageID);

                        messageContainer.smoothScrollToPosition(messages.size());


                    } else {
                        retrieveComments(currentLimit);
                        addNewComments();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messageContainer.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = manager.findLastVisibleItemPosition();
                if (adapter.getItemCount() > 5) {
                    if (position == adapter.getItemCount()) {
                        currentLimit += constant;
                        retrieveComments(currentLimit);
                    }
                }
            }
        });

        privateMessageinstances();

        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (replyHolder.getVisibility() != View.VISIBLE) {
                    sendPrivateComment(v);
                }
                return false;
            }
        });


    }


    private void processMessageToReply(Message message) {
        if (message.getMessageState() != MessageState.SENT || message.getMessageState() != MessageState.SEEN) {
            String messageContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0];
            switch (message.getMessageTYPE()) {
                case MessageType.COMMENT_TEXT:
                case MessageType.COMMENT_TEXT + MessageType.COMMENT_TEXT + MessageType.COMMENT_REPLY + MessageType.COMMENT_TEXT:
                case MessageType.COMMENT_TEXT + MessageType.COMMENT_REPLY + MessageType.COMMENT_VOICE_NOTE:

                    replyPhotoPreview.setVisibility(View.GONE);
                    generalMessageType = MessageType.COMMENT_TEXT + MessageType.COMMENT_TEXT + MessageType.REPLY;

                    break;

                case MessageType.COMMENT_VOICE_NOTE:
                case MessageType.COMMENT_VOICE_NOTE + MessageType.COMMENT_VOICE_NOTE + MessageType.REPLY + MessageType.COMMENT_VOICE_NOTE:

                case MessageType.COMMENT_VOICE_NOTE + MessageType.COMMENT_VOICE_NOTE + MessageType.REPLY + MessageType.COMMENT_TEXT:

                    long duration = Long.parseLong(messageContent);
                    messageContent = "voice note |" + TimeFactor.convertMillieToHMmSs(duration);
                    generalMessageType = MessageType.COMMENT_VOICE_NOTE + MessageType.COMMENT_VOICE_NOTE + MessageType.REPLY;
                    replyPhotoPreview.setVisibility(View.GONE);

                    break;


            }
            replyMessageHolder.setText(messageContent);

            replyHolder.setVisibility(View.VISIBLE);
            replyContentHolder.setVisibility(View.VISIBLE);


        }
    }

    Context context;

    private void copyText(String textToCopy) {

        setClipboard(context, textToCopy);
    }

    private void deleteComment(Message comment, int position) {

        finishDelete(comment.getMessageID(), position);

    }

    String outputSourceFile;
    String outputFile = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/choice";
    MediaRecorder recorder;
    long startTime;
    long endTime = 0;
    public static final double maxRecordingDuration = 1.5;

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
            int maxDurationInMunite = 1000 * 60;
            recorder.setMaxDuration((int) (maxDurationInMunite * maxRecordingDuration));

            try {
                recorder.prepare();
                startTime = System.currentTimeMillis();
                recorder.start();
            } catch (IllegalStateException | IOException e) {

                e.printStackTrace();

            }

        }

    }

    public void stopRecording() {


        long duration = 0;
        if (endTime != 0) {

            duration = endTime;
            endTime = 0;
        } else {
            duration = System.currentTimeMillis() - startTime;
        }

        long messageTime = System.currentTimeMillis();

        String text = String.valueOf(duration);
        if (messageToReply != null) {
            String replyContent = messageToReply.getMessageCONTENT().split(String.valueOf(messageToReply.getMessageTIME()))[0];
            text = text + messageTime + replyContent + messageTime + messageToReply.getMessageOwnerID();
        }


        recorder.release();
        recorder = null;
        if ((duration / 1000) != 0) {

            DatabaseReference postDirectory = FirebaseDatabase.getInstance().getReference(postFolder);
            DatabaseReference thisPost = postDirectory.child(cloudPost.getPostID());
            DatabaseReference thisPostComments = thisPost.child(postComments);

            DatabaseReference thisPostInfo = thisPost.child("postData");
            DatabaseReference userComments = thisPostComments.push();
            String commentId = userComments.getKey();

            Message message = new Message(commentId, cloudPost.getPostID(), generalMessageType + MessageType.COMMENT_VOICE_NOTE,
                    information.getMainUserID(), messageTime,
                    text, outputSourceFile, MessageType.STATE_SEND, MessageState.SENDING);

            messages.add(message);
            adapter.notifyItemInserted(messages.size());
            messageContainer.smoothScrollToPosition(messages.size());


        }
        messageToReply = null;
        replyHolder.setVisibility(View.GONE);
        generalMessageType = 0;


    }

    CountDownTimer countDownTimer;

    private void voiceNoteFinishing() {

        mic.setOnClickListener(v -> {
            Toast.makeText(this, "long press to record", Toast.LENGTH_SHORT).show();
        });

        mic.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    writeMessage.setVisibility(View.INVISIBLE);
                    commentVoiceNoteCounter.setVisibility(View.VISIBLE);
                    commentVoiceNoteCounter.setBase(SystemClock.elapsedRealtime());
                    commentVoiceNoteCounter.start();

                    YoYo.with(Techniques.Pulse).repeat(YoYo.INFINITE).playOn(micFloat);
                    emoji.setVisibility(View.INVISIBLE);
                    micFloat.setVisibility(View.VISIBLE);

                    countDownTimer = new CountDownTimer((int) (maxRecordingDuration * 1000 * 60), 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {


                        }

                        @Override
                        public void onFinish() {
                            commentVoiceNoteCounter.stop();
                            commentVoiceNoteCounter.setVisibility(View.INVISIBLE);
                            endTime = (int) (maxRecordingDuration * 1000 * 60);

                            recorder.stop();

                        }
                    }.start();


                    startRecording();


                    break;
                case MotionEvent.ACTION_UP:
                    writeMessage.setVisibility(View.VISIBLE);
                    commentVoiceNoteCounter.setVisibility(View.INVISIBLE);
                    commentVoiceNoteCounter.setBase(SystemClock.elapsedRealtime());


                    commentVoiceNoteCounter.stop();
                    emoji.setVisibility(View.VISIBLE);
                    countDownTimer.cancel();

                    micFloat.setVisibility(View.INVISIBLE);

                    stopRecording();
                    break;

            }
            return true;
        });


    }

    private void finishDelete(String ID, int position) {

        DatabaseReference thisPostDocument = FirebaseDatabase.getInstance()
                .getReference(postFolder).child(cloudPost.getPostID());
        DatabaseReference thisPostComments = thisPostDocument.child(postComments);
        thisPostComments.child(ID)
                .removeValue()
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CommentPage.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                                messages.remove(position);
                                adapter.notifyItemRemoved(position);

                            }
                        }
                );

    }

    MediaPlayer m;


    public void setPauseProgress(ProgressBar progress, TextView durationCount, String duration) {
        int position = (int) (((float) m.getCurrentPosition() / (float) m.getDuration()) * 100);
        progress.setProgress(position);
        durationCount.setText(milliSecondsToTimer(getCurrentDuration((int) progress.getProgress()
                , Long.parseLong(duration) / 1000)));

    }

    private long getCurrentDuration(int position, long duration) {
        return (long) ((float) position * duration / 100);
    }

    private String milliSecondsToTimer(long milliseconds) {
        String timerString = "";
        String secondsString;

        int hours = (int) (milliseconds / (60 * 60));
        int minutes = (int) (milliseconds & (60 * 60)) / (60);
        int seconds = (int) milliseconds % 60;

        if (hours > 0) {
            timerString = hours + ":";
        }
        if (seconds > 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }


    private Timer timer;

    private void timerCounter(ProgressBar videoviewSeekbar, TextView videViewDuration
            , int audioDuration) {
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(videoviewSeekbar, videViewDuration, audioDuration);

                    }
                });
            }
        };

        timer.schedule(task, 0, 1000);
    }

    private void updateUI(ProgressBar videviewSeekbar, TextView videoViewDuration, int audioDuration) {
        if (videviewSeekbar.getProgress() >= 100) {
            timer.cancel();
        }
        int current = m.getCurrentPosition();
        videoViewDuration.setText(TimeFactor.convertMillieToHMmSs(current));
        int progress = current * 100 / audioDuration;
        videviewSeekbar.setProgress(progress);

    }

    public void streamAudio(String path, ProgressBar progressBar, ImageView playButton,
                            long audioDuration, TextView playDurationTextView) throws IOException {
        m = new MediaPlayer();
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)

                .build();
        m.setAudioAttributes(audioAttributes);

        m.setDataSource(path);
        playButton.setImageResource(R.drawable.pause_filled);


        m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                progressBar.setMax(100);
                timerCounter(progressBar, playDurationTextView, m.getDuration());

            }
        });
        m.prepareAsync();


        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                progressBar.setProgress(0);
                playButton.setImageResource(R.drawable.play_filled);
                //playDurationTextView.setText(milliSecondsToTimer(audioDuration));
                if (timer != null) {
                    timer.cancel();
                }
                m.release();
                m = null;

            }
        });


    }

    private Message messageToReply2;

    private void adapterData() {


        adapter.setOnclickListener(new ChatAdapter.OnclickListener() {

            @Override
            public DatabaseReference replyCounter(int position) {
                Message message = messages.get(position);
                String messageID = message.getMessageID();
                DatabaseReference thisPostDocument = FirebaseDatabase.getInstance()
                        .getReference(postFolder).child(cloudPost.getPostID());

                DatabaseReference thisPostComments = thisPostDocument.child(postComments);
                DatabaseReference repliesRef = thisPostComments.child(messageID).child("replies");
                return repliesRef;

            }

            @Override
            public void onReplyClick(int position) {
                Message message = messages.get(position);
                messageToReply = message;
                messageToReply2 = message;
//                messageOptionsHolder.setVisibility(View.GONE);
                processMessageToReply(message);
            }

            @Override
            public void onMessageClickListener(int position, View itemView) {


                Message message=messages.get(position);
                String []split=message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
                if(split.length>3) {


                    ShowReply showReply = new ShowReply();
                    commentPageContainer.setDrawingCacheEnabled(true);
                    Bitmap bitmap = commentPageContainer.getDrawingCache();
                    String messageID=split[3];
                    showReply.setClickedMessageReplyID(messageID);

                    showReply.setPost(cloudPost);
                    showReply.setClickedMessage(message);

                    bitmap = FastBlur.fastblur(bitmap, .7f, 20);
                    showReply.setBackgroundDrawable(bitmap);
                    showReply.setThemes(adapter.getTheme());
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.commentPageContainer, showReply).addToBackStack(null).commit();
                }

            }

            @Override
            public void setMessageTime(int position, TextView view) {

            }

            @Override


            public void sendMessageText(int position) {


            }

            @Override
            public void onSeekBarChange(int position, int progress) {
                if (m != null && m.isPlaying()) {

                    int duration = m.getDuration();
                    int currentposition = m.getCurrentPosition();
                    int seekTo = (duration * progress) / 100;
                    m.seekTo(seekTo);

                }


            }

            @Override
            public void onLongClickMessage(int position, View itemView) {

                Message message = messages.get(position);

                CommentOption commentOption = new CommentOption();
                boolean isMe = (message.getMessageOwnerID().equals(new UserInformation(CommentPage.this).getMainUserID()));
                commentOption.setMyComment(isMe);


                commentOption.setOptionClickListeners(new CommentOption.optionClickListeners() {
                    @Override
                    public void onCopyComment() {
                        String copiedText = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0];
                        copyText(copiedText);
                        commentOption.dismiss();


                    }

                    @Override
                    public void onReplyComment() {
                        Message message = messages.get(position);
                        messageToReply = message;
                        messageToReply2 = message;
//                messageOptionsHolder.setVisibility(View.GONE);
                        processMessageToReply(message);
                        commentOption.dismiss();

                    }

                    @Override
                    public void onDeleteComment() {

                        deleteComment(message, position);
                        commentOption.dismiss();
                    }
                });
                commentOption.show(getSupportFragmentManager(), "ctagtagcommentoption");

            }

            @Override
            public void setMessageToSeen(int position) {

            }

            @Override
            public void uploadFiles(int position, ProgressBar uploadProgress, ImageView playButton, ImageView send_state) {
                Message message = messages.get(position);

                if (message.getMessageState() != MessageState.SENT || message.getMessageState() != MessageState.SEEN) {


                    StorageReference voicenoteFile = chatFiles.child(System.currentTimeMillis() + "." + Functions.fileExtension((message.getMessageURL())));

                    // Uri uri=Uri.parse(message.getMessageURL());

                    voicenoteFile.putFile(Uri.fromFile(new File(message.getMessageURL())))
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    send_state.setImageResource(R.drawable.check_white);
                                    int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                                    uploadProgress.setProgress(progress);

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    message.setMessageState(MessageState.SENT);
                                    send_state.setImageResource(R.drawable.not_seen_check);

                                    // adapter.notifyItemChanged(position);
                                    voicenoteFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri recordUri) {
                                            CloudMessage cloudMessage = new CloudMessage(message.getMessageTYPE(),
                                                    new UserInformation(CommentPage.this).getMainUserID(),
                                                    message.getMessageTIME(),
                                                    message.getMessageCONTENT(), recordUri.toString(), MessageState.SENT);

                                           /* DatabaseReference sendMessageRef = chatChannels.child(CHANNEL_ID)
                                                    .child("messages").child(message.getMessageID());
                                            sendMessageRef.setValue(cloudMessage);

                                            */


                                            //createComment(cloudMessage, String.valueOf(cloudMessage.getMessageTIME()));

                                            uploadProgress.setVisibility(View.INVISIBLE);


                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    message.setMessageState(MessageState.FAILED);
                                    viewModel.updateMessage(message);
                                    send_state.setImageResource(R.drawable.check_red);
                                    uploadProgress.setProgress(0);

                                }
                            });


                }
            }

            @Override
            public void uploadVoiceNote(int position, ProgressBar uploadProgress,
                                        ImageView playButton,
                                        ImageView send_state) {
                Message message = messages.get(position);

                if (message.getMessageState() != MessageState.SENT) {

                    playButton.setVisibility(View.INVISIBLE);

                    StorageReference voicenoteFile = chatFiles.child(System.currentTimeMillis() + ".3gp");

                    voicenoteFile.putFile(Uri.fromFile(new File(message.getMessageURL())))
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    send_state.setImageResource(R.drawable.check_white);
                                    uploadProgress.setVisibility(View.VISIBLE);

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //Toast.makeText(ChatPage.this, "Upload success", Toast.LENGTH_SHORT).show();


                                    message.setMessageState(MessageState.SENT);
                                    send_state.setImageResource(R.drawable.not_seen_check);


                                    voicenoteFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri recordUri) {
                                            CloudMessage cloudMessage = new CloudMessage(message.getMessageTYPE(), information.getMainUserID(), message.getMessageTIME(),
                                                    message.getMessageCONTENT(), recordUri.toString(), MessageState.SENT);

                                            DatabaseReference postDirectory = FirebaseDatabase.getInstance().getReference(postFolder);
                                            DatabaseReference thisPost = postDirectory.child(cloudPost.getPostID());
                                            DatabaseReference thisPostComments = thisPost.child(postComments);

                                            DatabaseReference thisPostInfo = thisPost.child("postData");
                                            DatabaseReference userComments = thisPostComments.push();
                                            userComments.setValue(cloudMessage);
                                            message.setMessageState(cloudMessage.getMessageState());
                                            uploadProgress.setVisibility(View.INVISIBLE);
                                            playButton.setVisibility(View.VISIBLE);

                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    send_state.setImageResource(R.drawable.check_red);
                                    Toast toast = new Toast(context);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.setText("Sending failed");
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.show();


                                }
                            });


                }
            }

            @Override
            public void messageIsSeen(int position, ImageView indicator) {
                ///indicator.setImageResource(R.drawable.check_white);
            }

            @Override
            public void nameChoiceUser(int position, TextView nameTextView, CircleImageView profileImageView) {
                Message message = messages.get(position);
                // UserNaming userNaming = UserNaming.getInstance();
                //  userNaming.nameThisUser(CommentPage.this, getApplicationContext(), message.getMessageOwnerID(), nameTextView, profileImageView);

                realtimeUser.child(new UserInformation(CommentPage.this).getMainUserID()).child("people")
                        .child(message.getMessageOwnerID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    ChoiceUser contact = dataSnapshot.getValue(ChoiceUser.class);
                                    nameTextView.setText(contact.getUserContactName());
                                    Glide.with(getApplicationContext()).load(contact.getUserImageUrl()).thumbnail(0.4f).into(profileImageView);


                                } else {
                                    realtimeUser.child(message.getMessageOwnerID())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                                        nameTextView.setText(detail.getUsername());
                                                        Glide.with(getApplicationContext()).load(detail.getProfileURL()).thumbnail(.4f).into(profileImageView);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void downloadVoiceNote(int position, ProgressBar downloadProgress, ImageView downloadButton, ImageView replaceButton) {
                // file downloader dont need to be implemented

            }

            @Override
            public void OnclickChatProfileImage(int position) {

                Message message = (Message) messages.get(position);
                if (message.getMessageOwnerID().equals(new UserInformation(CommentPage.this).getMainUserID())) {

                    startActivity(new Intent(CommentPage.this, SettingsPage.class));

                } else {
                    PartialProfile partialProfile = PartialProfile.getInstance(message.getMessageOwnerID());
                    partialProfile.setOnclickMessageBox(() -> {
                        Intent intent = new Intent(CommentPage.this, ChatPage.class);

                        realtimeUser.child(message.getMessageOwnerID())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                                        intent.putExtra("name", userDetail.getUsername());
                                        intent.putExtra("profile picture", userDetail.getProfileURL());
                                        intent.putExtra("phone number", userDetail.getPhone());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    });

                    partialProfile.show(getSupportFragmentManager(), "partial profile");
                }
            }

            @Override
            public int currentlyPlaying() {

                return currentPlayingPosition;
            }

            File downloadedRecord;
            int currentPlayingPosition = -1;


            @Override
            public void playPauseButton(int position, ImageView playPauseButton, ProgressBar voiceNoteProgress,
                                        TextView playDuration, String path) throws IOException {


                Message message = messages.get(position);

                long myVoiceNoteDuration = Long.parseLong(message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0]);

                if (m == null) {
                    streamAudio(message.getMessageURL(), voiceNoteProgress, playPauseButton, myVoiceNoteDuration, playDuration);
                    currentPlayingPosition = position;


                } else {
                    if (currentPlayingPosition == position && m.isPlaying()) {

                        m.pause();

                        playPauseButton.setImageResource(R.drawable.play_filled);
                        setPauseProgress(voiceNoteProgress, playDuration, message.getMessageCONTENT());
                        currentPlayingPosition = position;

                    } else if (currentPlayingPosition == position && !m.isPlaying()) {

                        m.start();
                        playPauseButton.setImageResource(R.drawable.pause_filled);
                        currentPlayingPosition = position;

                    } else if (currentPlayingPosition != position) {
                        m.release();
                        timer.cancel();
                        streamAudio(message.getMessageURL(), voiceNoteProgress, playPauseButton, myVoiceNoteDuration, playDuration);

                        adapter.setStopPlaying(currentPlayingPosition);
                        adapter.notifyItemChanged(currentPlayingPosition);
                        currentPlayingPosition = position;


                    }
                }


            }


            @Override
            public boolean showName() {
                return true;
            }

            @Override
            public void onClickPrivateCommentMessage(int position) {

            }

            @Override
            public void downloadFiles(int position, ProgressBar progressBar, ImageView downloadButton,
                                      ImageView imagePreview) {

            }

            @Override
            public void onClickImageMessage(int position) {

            }
        });


    }

    RelativeLayout replyHolder, replyContentHolder, messageOptionsHolder;
    EmojiTextView replyMessageHolder;
    ImageView replyPhotoPreview, replyCancelButton, optionCancelButton;
    ProgressBar showLoading;
    RelativeLayout postHolder;
    FloatingActionButton micFloat;


    public void sendComment(View view) {

        String text = writeMessage.getText().toString();
        long messageTime = System.currentTimeMillis();
        String messageToReplyID = "";

        if (messageToReply != null) {

            String replyContent = messageToReply.getMessageCONTENT().split(String.valueOf(messageToReply.getMessageTIME()))[0];
            generalMessageType = MessageType.COMMENT_TEXT + MessageType.COMMENT_TEXT + MessageType.COMMENT_REPLY;
            messageToReplyID = messageToReply.getMessageID();
            text = text + messageTime + replyContent + messageTime + messageToReply.getMessageOwnerID() + messageTime + messageToReply.getMessageID();

        }


        if (!text.isEmpty()) {
            CloudMessage comment = new CloudMessage(generalMessageType + MessageType.COMMENT_TEXT, information.getMainUserID()
                    , messageTime,
                    text, "", MessageState.SENT);

            //  DatabaseReference thisPost = postFOLDER.child(cloudPost.getPostID());
            // DatabaseReference thisPostComments = thisPost.child(postComments).push();
            createComment(comment, messageToReplyID);

        }
        generalMessageType = 0;
        messageToReply = null;
        messageToReply2 = null;
        replyHolder.setVisibility(View.GONE);

        writeMessage.setText("");


    }


    private void createNotification(String userID, Message messageToReply, String messageID) {

        String postOwnerID = cloudPost.getOwnerID();
        if (!userID.equals(postOwnerID)) {
            if (messageToReply == null) {
                String tittle = "Commented ";
                NotificationForPost notificationForPost = new NotificationForPost(userID, tittle,
                        "on your post", System.currentTimeMillis(), messageID, cloudPost.getPostID(), "", false);
                notification.push().setValue(notificationForPost);

              /*  DatabaseReference pending=realtimeUser.child(cloudPost.getOwnerID()).child("pendingNotification")
                        .push();

                pending.setValue(notificationForPost);
               */


            } else {
                String repliedID = messageToReply.getMessageOwnerID();
                if (postOwnerID.equals(repliedID)) {

                    String tittle = "comment ";

                    NotificationForPost notificationForPost = new NotificationForPost(userID,
                            tittle, "replied your comment", System.currentTimeMillis(), messageID, cloudPost.getPostID(), repliedID, false);
                    notification.push().setValue(notificationForPost);

                    DatabaseReference pending = realtimeUser.child(cloudPost.getOwnerID()).child("pendingNotification")
                            .push();
                    pending.setValue(notificationForPost);


                    DatabaseReference notify = realtimeUser.child(cloudPost.getOwnerID()).child("notifications")
                            .push();
                    notify.setValue(notificationForPost);

                } else {
                    if (!repliedID.equals(new UserInformation(this).getMainUserID())) {
                        String tittle = "comment";
                        NotificationForPost notificationForPost = new NotificationForPost(userID,
                                tittle, " replied your comment", System.currentTimeMillis(), messageID, cloudPost.getPostID(), cloudPost.getOwnerID(), false);
                        // notification.push().setValue(notificationForPost);
                        realtimeUser.child(repliedID).child("pendingNotification").push().setValue(notificationForPost);
                        realtimeUser.child(repliedID).child("notifications").push().setValue(notificationForPost);
                        notification.push().setValue(notificationForPost);


                       /* tittle = "replied to ";
                        notificationForPost.setTittle(tittle);
                        notificationForPost.setSecondUser(repliedID);
                        notificationForPost.setBody("comment on your post");
                        notification.push().setValue(notificationForPost);

                        realtimeUser.child(cloudPost.getOwnerID()).child("notifications")
                                .push()
                                .setValue(notificationForPost);

                        */

                    }
                }


            }
        } else {
            if (messageToReply != null) {
                String repliedID = messageToReply.getMessageOwnerID();

                if (!repliedID.equals(userID)) {
                    String tittle = "replied to your comment ";
                    NotificationForPost notificationForPost = new NotificationForPost(userID,
                            tittle, "", System.currentTimeMillis(), messageID, cloudPost.getPostID(), cloudPost.getOwnerID(), false);

                    realtimeUser.child(repliedID).child("notifications").push().setValue(notificationForPost);

                    realtimeUser.child(repliedID).child("pendingNotification")
                            .push()
                            .setValue(notificationForPost);


                }
            }
        }


        messageToReply2 = null;

    }

    int messageState = MessageState.SENT;

    List<String> commentIDS = new ArrayList<>();

    private void addComment(CloudMessage comment, int state, String key) {
        Message message = new Message(key, cloudPost.getPostID(), comment.getMessageTYPE(), comment.getMessageOwnerID(),
                comment.getMessageTIME(),
                comment.getMessageCONTENT(), comment.getMessageURL(), state, state);
        messages.add(message);
        commentIDs.add(message.getMessageID());
        adapter.notifyItemInserted(messages.size());
        messageContainer.smoothScrollToPosition(messages.size());
        saveRepeat(cloudPost);


    }

    private void addCommenttoList(CloudMessage comment, int state, String key) {
        Message message = new Message(key, cloudPost.getPostID(), comment.getMessageTYPE(), comment.getMessageOwnerID(),
                comment.getMessageTIME(),
                comment.getMessageCONTENT(), comment.getMessageURL(), state, state);
        messages.add(message);
        adapter.notifyDataSetChanged();
        commentIDs.add(String.valueOf(comment.getMessageTIME()));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("post").child(cloudPost.getPostID());


    }


    private void retrieveComments(int limit) {
        DatabaseReference thisPostDocument = FirebaseDatabase.getInstance()
                .getReference(postFolder).child(cloudPost.getPostID());

        DatabaseReference thisPostComments = thisPostDocument.child(postComments);
        thisPostComments.keepSynced(false);


        thisPostComments
                .orderByChild("messageTIME")

                .limitToFirst(limit).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        showLoading.setVisibility(View.VISIBLE);
                        if (snapshot.exists()) {
                            CloudMessage comment = snapshot.getValue(CloudMessage.class);
                            if (!commentIDs.contains(snapshot.getKey())) {
                                commentIDs.add(snapshot.getKey());
                                addRetrievedMessage(comment, snapshot.getKey());

                            }


                        }
                        //load progress bar


                    }
                    showLoading.setVisibility(View.GONE);

                } else {
                    showLoading.setVisibility(View.GONE);
                }
                //stop loading and hide progress bar
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //snack error message .. cant load progress bar
                Toast.makeText(context, "Can't load comments at this time", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void retrieveCommentsForNotification(String messageID) {
        DatabaseReference thisPostDocument = FirebaseDatabase.getInstance()
                .getReference(postFolder).child(cloudPost.getPostID());

        DatabaseReference thisPostComments = thisPostDocument.child(postComments);
        thisPostComments.keepSynced(false);

        thisPostComments
                .orderByKey()
                .endAt(messageID)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                showLoading.setVisibility(View.VISIBLE);
                                if (snapshot.exists()) {
                                    CloudMessage comment = snapshot.getValue(CloudMessage.class);
                                    if (!commentIDs.contains(snapshot.getKey())) {
                                        commentIDs.add(snapshot.getKey());
                                        addRetrievedMessage(comment, snapshot.getKey());

                                    }
                                    if (adapter.getItemCount() == dataSnapshot.getChildrenCount()) {
                                        messageContainer.smoothScrollToPosition(adapter.getItemCount());
                                    }

                                }
                                //load progress bar

                                showLoading.setVisibility(View.GONE);


                            }

                        } else {
                            showLoading.setVisibility(View.GONE);
                        }
                        //stop loading and hide progress bar
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        //snack error message .. cant load progress bar
                        Toast.makeText(context, "Can't load comments at this time", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void addRetrievedMessage(CloudMessage message, String key) {
        CloudMessage cloudMessage = new CloudMessage(message.getMessageTYPE(), message.getMessageOwnerID(),
                message.getMessageTIME(), message.getMessageCONTENT(), message.getMessageURL(), MessageState.COMMENT);


        addCommenttoList(cloudMessage, MessageState.COMMENT, key);

    }

    private void addNewComments() {
        DatabaseReference thisPostDocument = FirebaseDatabase.getInstance()
                .getReference(postFolder).child(cloudPost.getPostID());

        DatabaseReference thisPostComments = thisPostDocument.child(postComments);
        thisPostComments.keepSynced(false);


        thisPostComments.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    CloudMessage comment = dataSnapshot.getValue(CloudMessage.class);
                    if (!commentIDs.contains(dataSnapshot.getKey()) && !comment.getMessageOwnerID().equals(new UserInformation(context).getMainUserID())) {
                        commentIDs.add(dataSnapshot.getKey());
                        addRetrievedMessage(comment, dataSnapshot.getKey());

                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void viewById() {

        privateCommentNote = findViewById(R.id.privateCommentNote);
        replyHolder = findViewById(R.id.replyHolder);
        replyMessageHolder = findViewById(R.id.replyMessageHolder);
        replyContentHolder = findViewById(R.id.replyContentHolder);
        micFloat = findViewById(R.id.micFloat);
        postHolder = findViewById(R.id.postHolder);
        showLoading = findViewById(R.id.showLoading);
        commentPageText = findViewById(R.id.commentPageText);
        replyPhotoPreview = findViewById(R.id.replyPhotoPreview);
        replyCancelButton = findViewById(R.id.replyCancelButton);
        writeMessage = findViewById(R.id.write_comment);
        mic = findViewById(R.id.commentPageMic);
        camera = findViewById(R.id.commentPageCamera);
        commentPageText = findViewById(R.id.commentPageText);
        messageContainer = findViewById(R.id.commentMessagesContainer);
        send = findViewById(R.id.commentPageSend);
        commentPageContainer = findViewById(R.id.commentPageContainer);
        commentPageBackButton = findViewById(R.id.commentPageBackButton);
        commentVoiceNoteCounter = findViewById(R.id.commentVoiceNoteCounter);
        //swipe_refresh = findViewById(R.id.swipe_refresh);
        micFloat = findViewById(R.id.micFloat);
        NestedScrollView nestedScrollView;


    }


    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView[] texttoChange = {commentPageText};

        setTextColors(texttoChange, currentTheme);


    }

    private void setClipboard(Context context, String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "text copied", Toast.LENGTH_SHORT).show();
    }

    Toolbar commentPageToolbar;

    public void setTextColors(TextView[] text, int currentTheme) {
        commentPageToolbar = findViewById(R.id.commentPageToolbar);
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textContext);

                commentPageContainer.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                writeMessage.setHintTextColor(getResources().getColor(R.color.textHeaderColor));
                writeMessage.setTextColor(getResources().getColor(R.color.textColor));
                postAdapter.setColor(R.color.textColor);
                postAdapter.notifyDataSetChanged();

                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                postAdapter.setColor(R.color.whiteGreen);
                postAdapter.notifyDataSetChanged();
                // commentPageToolbar.setBackgroundColor(getResources().getColor(R.color.grayGreen));

                commentPageContainer.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                writeMessage.setHintTextColor(getResources().getColor(R.color.textContext));
                writeMessage.setTextColor(getResources().getColor(R.color.whiteGreen));


                break;

        }
    }

    List<Post> posts = new ArrayList<>();

    DatabaseReference notification;

    public void createComment(CloudMessage message, String replyID) {


        DatabaseReference postDirectory = FirebaseDatabase.getInstance().getReference(postFolder);
        DatabaseReference thisPost = postDirectory.child(cloudPost.getPostID());
        DatabaseReference thisPostComments = thisPost.child(postComments);

        DatabaseReference thisPostInfo = thisPost.child("postData");
        DatabaseReference userComments = thisPostComments.push();
        String commentId = userComments.getKey();
        addComment(message, MessageType.STATE_SEND, thisPostComments.getKey());

        createNotification(new UserInformation(this).getMainUserID(), messageToReply, commentId);

        userComments.setValue(message);

        updateLifeSpan(thisPostComments, thisPostInfo, PostLifeSpans.COMMENTS);
        if (generalMessageType != 0) {

            thisPostComments.child(replyID).child("replies").child(commentId)
                    .setValue(commentId);

        }


    }

    int messagePosition;

    public void postData() {
        Intent postData = getIntent();
        cloudPost = postData.getParcelableExtra("postData");
        posts.add(cloudPost);
        //commentPagePostDataRecyclerView.setHasFixedSize(true);
        View contentView = null;
        PostMainViewHolder mainViewHolder = null;

        switch (cloudPost.getPostType()) {
            case PostTypes.SINGLE_POST:
                contentView = LayoutInflater.from(this).inflate(R.layout.singlepost, postHolder);
                mainViewHolder = new SinglePostViewHolder(contentView);
                break;
            case PostTypes.MERGED_POST:
                contentView = LayoutInflater.from(this).inflate(R.layout.mergepost, postHolder);
                mainViewHolder = new MergedPostViewHolder(contentView);
                break;
            case PostTypes.VIDEO_POST:
                contentView = LayoutInflater.from(this).inflate(R.layout.video_post, postHolder);
                mainViewHolder = new VideoPostViewHolder(contentView);
                break;
            case PostTypes.TEXT:
                contentView = LayoutInflater.from(this).inflate(R.layout.text_post, postHolder);
                mainViewHolder = new TextViewHolder(contentView);
                break;

        }

        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        int color = R.color.textColor;
        switch (currentTheme) {
            case Theme.LIGHT:
                color = R.color.textColor;
                break;
            case Theme.DEEP:
                color = R.color.whiteGreen;
                break;
        }

        mainViewHolder.bindPostType(cloudPost, this, color);

        PostAdapter.OnclickListeners listeners = new PostAdapter.OnclickListeners() {

            @Override
            public void openSocialProfile(int position) {
                Post post = cloudPost;
                String id = post.getOwnerID();
                realtimeUser.child(id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                    Intent intent = new Intent(CommentPage.this, SocialProfile.class);
                                    intent.putExtra("user", detail);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void postHelper(int position, LinearLayout iconsHolder, TextView text, ImageView icon) {

                iconsHolder.setVisibility(View.GONE);


            }

            @Override
            public void updateComments(int position, TextView commentText) {

            }

            @Override
            public void updateLikes(int position, TextView likeText, int type) {

            }

            @Override
            public void nameChoiceUser(int position, TextView nameView, CircleImageView profileImageView) {

                Post post = cloudPost;
                String myPhoneNumber = new UserInformation(context).getMainUserID();
                if (post.getOwnerID().equals(myPhoneNumber)) {
                    nameView.setText("Your post");
                    realtimeUser.child(myPhoneNumber)
                            .addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Glide.with(context.getApplicationContext()).load(detail.getProfileURL()).thumbnail(0.4f).into(profileImageView);

                                                    }
                                                });
                                                    }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    }
                            );
                } else {
                    UserNaming userNaming = UserNaming.getInstance();
                    userNaming.nameThisUser(CommentPage.this, getApplicationContext()
                            , post.getOwnerID(),
                            nameView, profileImageView);
                }
            }

            @Override
            public void onClickShare(int position) {
                Post post = cloudPost;
                if (post.getOwnerID().equals(new UserInformation(CommentPage.this).getMainUserID())) {

                } else {
                    DatabaseReference folder = postFOLDER.child(post.getPostID());
                    OptionBottomSheet bottomSheet = new OptionBottomSheet();
                    bottomSheet.setShare(true);
                    bottomSheet.setPost(post);

                    folder.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                bottomSheet.setListener(new OptionBottomSheet.OptionClickListener() {
                                    @Override
                                    public void commentPrivately() {

                                    }

                                    @Override
                                    public void onCopyText() {

                                    }

                                    @Override
                                    public void onCancelRepeat() {

                                    }

                                    @Override
                                    public void onHidePost() {

                                    }

                                    @Override
                                    public void onRepost() {
                                        repost(post, folder);
                                    }

                                    @Override
                                    public void onMutePost() {

                                    }
                                });


                                bottomSheet.show(getSupportFragmentManager(), "Dialog Bottom Sheet");
                            } else {
                                Toast.makeText(CommentPage.this, "This post no longer exist", Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onClickLoop(int position) {
                Post post = cloudPost;
                post.setRepeat(false);
                viewModel.updatePost(post);


            }

            @Override
            public void onClickItem(int position, View view) {
                Post post = cloudPost;
                if (post.getPostType() == PostTypes.SINGLE_POST || post.getPostType() == PostTypes.MERGED_POST) {
                    //Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();

                    Intent intent1 = new Intent(CommentPage.this, PostImageViewer.class);
                    intent1.putExtra("view images", post);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent1);

                } else if (post.getPostType() == PostTypes.VIDEO_POST) {
                    Intent intent1 = new Intent(CommentPage.this, PostVideoViewer.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent1.putExtra("view images", post);
                    startActivity(intent1);
                }
            }

            @Override
            public void onClickOption(int position) {


                Post post = cloudPost;
                post.setAllowPrivateComment(false);
                if (post.getOwnerID().equals(new UserInformation(CommentPage.this).getMainUserID())) {


                } else {
                    DatabaseReference folder = postFOLDER.child(post.getPostID());

                    OptionBottomSheet bottomSheet = OptionBottomSheet.getInstance();
                    bottomSheet.setPost(post);
                    //privateMessageinstances(post);

                    boolean isMuted;

                    bottomSheet.setShare(false);
                    bottomSheet.setListener(new OptionBottomSheet.OptionClickListener() {
                        @Override
                        public void commentPrivately() {


                        }

                        @Override
                        public void onCopyText() {
                            switch (post.getPostType()) {
                                case PostTypes.TEXT:
                                    String text = post.getPOST_CAPTION();
                                    setClipboard(context, text);
                                    Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show();

                                    break;
                                case PostTypes.VIDEO_POST:
                                    text = post.getPOST_URL();
                                    setClipboard(context, text);

                                    Toast.makeText(context, "link copied", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(context, "no text to copy", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            bottomSheet.dismiss();

                        }

                        @Override
                        public void onCancelRepeat() {
                            Post post = cloudPost;

                            post.setRepeat(false);
                            viewModel.updatePost(post);
                            bottomSheet.dismiss();

                        }

                        @Override
                        public void onHidePost() {
                            Post post1 = cloudPost;
                            post1.setPOST_TYPE(PostTypes.DEFAULT);

                            viewModel.updatePost(post1);
                            Toast.makeText(context, "Post have been removed from your timeline", Toast.LENGTH_SHORT).show();

                            bottomSheet.dismiss();
                        }

                        @Override
                        public void onRepost() {

                            repost(post);
                            bottomSheet.dismiss();
                            Toast.makeText(context, "Post will be visible to your contacts", Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onMutePost() {

                            mutePostFromUser(post.getFromUserID());
                            bottomSheet.dismiss();

                        }
                    });

                    bottomSheet.show(getSupportFragmentManager(), String.valueOf(System.currentTimeMillis()));

                }
            }

            @Override
            public void onClickComment(int position) {
                writeMessage.setFocusable(true);

            }

            @Override
            public void onCheckChangeMergeLikeOne(int position, boolean isChecked) {
                Post post = cloudPost;
                DatabaseReference reference = postFOLDER.child(post.getPostID());
                DatabaseReference likeList = reference.child("like_one");


                if (isChecked) {
                    DatabaseReference mylike = likeList.child(information.getMainUserID());
                    mylike.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ndataSnapshot) {
                                    if (!ndataSnapshot.exists()) {

                                        mylike.setValue(information.getMainUserID());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                    if (willRepeat) {
                        saveRepeat((Post) cloudPost);
                    }
                } else {
                    DatabaseReference myLike = likeList.child(information.getMainUserID());
                    myLike.removeValue();


                }


            }

            @Override
            public void onCheckChangeMergeLikeTwo(int position, boolean isChecked) {

                Post post = cloudPost;
                DatabaseReference reference = postFOLDER.child(post.getPostID());
                DatabaseReference likeList = reference.child("like_two");

                if (isChecked) {

                    DatabaseReference mylike = likeList.child(information.getMainUserID());
                    mylike.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {

                                        mylike.setValue(information.getMainUserID());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                    if (willRepeat) {
                        saveRepeat(cloudPost);
                    }
                } else {
                    DatabaseReference myLike = likeList.child(information.getMainUserID());
                    myLike.removeValue();

                }

            }


            @Override
            public int onCheckChangeSingles(int position, boolean isChecked) {
                Post post = cloudPost;
                DatabaseReference reference = postFOLDER.child(post.getPostID());
                DatabaseReference likeList = reference.child("likes");


                if (isChecked) {
                    if (willRepeat) {
                        saveRepeat(post);

                    }
                    DatabaseReference mylike = likeList.child(information.getMainUserID());
                    mylike.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ndataSnapshot) {
                                    if (!ndataSnapshot.exists()) {

                                        mylike.setValue(information.getMainUserID());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );


                } else {
                    DatabaseReference myLike = likeList.child(information.getMainUserID());
                    myLike.removeValue();


                }


                return 0;
            }

        };

        mainViewHolder.listeners(listeners);


        notification = postFOLDER.child(cloudPost.getPostID()).child("notification");


    }

    DatabaseReference muteUsersPost = FirebaseDatabase.getInstance().getReference("users");


    public void repost(Post cloudPost) {

        if (cloudPost.isRepeat()) {
            DatabaseReference myrepeatPost = realtimeUser.child(information.getMainUserID())
                    .child("post").child(cloudPost.getPostID());
            myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

        }


    }

    private void mutePostFromUser(String userID) {

        muteUsersPost = muteUsersPost.child(information.getMainUserID()).child("people").child(userID);
        muteUsersPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ContactPost contactPost = dataSnapshot.getValue(ContactPost.class);

                    Map<String, Object> update = new HashMap<>();
                    if (contactPost.isMute()) {

                        update.put("mute", false);

                    } else {
                        update.put("mute", true);
                        viewModel.getChoiceUser(userID)
                                .observe(CommentPage.this, new Observer<List<ChoiceUser>>() {
                                    @Override
                                    public void onChanged(List<ChoiceUser> choiceUsers) {
                                        if (!choiceUsers.isEmpty()) {
                                            String text = "You muted " + choiceUsers.get(0).getUserContactName();
                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    muteUsersPost.updateChildren(update);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void changeTextColors(TextView[] textViews, int color) {
        if (textViews != null) {
            for (TextView text : textViews) {
                text.setTextColor(getResources().getColor(color));
            }
        }

    }

    int generalMessageType = 0;

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveRepeat(Post cloudPost) {
        if (!cloudPost.getOwnerID().equals(information.getMainUserID())) {

            if (cloudPost.isRepeat()) {
                DatabaseReference myrepeatPost = realtimeUser.child(information.getMainUserID())
                        .child("post").child(cloudPost.getPostID());
                myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

            }


        }
    }

    int constant = 20;

    private Handler handler = new Handler();

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void repost(Post cloudPost, DatabaseReference reference) {

        DatabaseReference postData = reference.child("postData");
        postData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PostInfo info = dataSnapshot.getValue(PostInfo.class);
                    if (info.getPostIsOnRepeat()) {
                        DatabaseReference myrepeatPost = realtimeUser.child(information.getMainUserID())
                                .child("post").child(cloudPost.getPostID());
                        myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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


    public void privateMessageinstances() {

        realtimeUser = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);
        userToChatWith = realtimeUser.child(cloudPost.getOwnerID());
        chatChannels = FirebaseDatabase.getInstance().getReference("ChatChannels");
        myChats = realtimeUser.child(new UserInformation(this).getMainUserID());
        chatFolder = myChats.child("chats");
        otherChatFolder = userToChatWith.child("chats");
        reference = chatFolder.child(cloudPost.getOwnerID());
        chatFiles = FirebaseStorage.getInstance().getReference("ChatFiles");

    }

    public void createPrivateComment(CloudMessage message) {

        information = new UserInformation(this);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //sending comment as first message to a user
                if (!dataSnapshot.exists()) {
                    DatabaseReference ourChat = chatChannels.push();
                    DatabaseReference sendMessage = ourChat.child("messages");
                    String channelID = ourChat.getKey();
                    DatabaseReference myChatWithYou = chatFolder.child(cloudPost.getOwnerID());
                    myChatWithYou.setValue(new Channels(channelID, message.getMessageTIME(), ChannelTypes.ONE_TO_ONE_CHAT, false, false));

                    otherChatFolder.child(new UserInformation(CommentPage.this).getMainUserID())
                            .setValue(new Channels(channelID, message.getMessageTIME(), ChannelTypes.ONE_TO_ONE_CHAT, false, false));

                    DatabaseReference messageRef = sendMessage.push();


                    messageRef.setValue(message).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    myChatWithYou.child("messages")
                                            .child(messageRef.getKey())
                                            .setValue(message);
                                    Toast.makeText(CommentPage.this, "Sending comment", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                } else {
                    //sending as a normal message to user

                    createMessage(message);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int lastSeenMessageCount;


    public void createMessage(CloudMessage message) {


        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Channels value = dataSnapshot.getValue(Channels.class);
                    String ID = value.getChatChannelID();
                    DatabaseReference sendMessageRef = chatChannels.child(ID)
                            .child("messages");

                    DatabaseReference messageRef = sendMessageRef.push();

                    messageRef.setValue(message).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CommentPage.this, "Sending comment", Toast.LENGTH_SHORT).show();

                                    reference.child("messages")
                                            .child(messageRef.getKey()).setValue(message);

                                }
                            }
                    );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void sendPrivateComment(View view) {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connect = dataSnapshot.getValue(Boolean.class);
                if (connect) {
                    if (messageToReply == null) {
                        privateCommentSender();
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void privateCommentSender() {
        String text = writeMessage.getText().toString();
        long messageTime = System.currentTimeMillis();
        postFOLDER.child(cloudPost.getPostID()).child("postData")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        PostInfo info = dataSnapshot.getValue(PostInfo.class);

                        if (info.isAllowPrivateComment()) {
                            if (!text.isEmpty() && cloudPost.getPostType() != PostTypes.MERGED_POST
                            ) {

                                if (!information.getMainUserID().equals(cloudPost.getOwnerID())) {
                                    String makeText = text + messageTime + cloudPost.getPOST_CAPTION() +
                                            messageTime + cloudPost.getPostID()
                                            + messageTime + cloudPost.getOwnerID() + messageTime;
                                    int privateCommentType = 0;
                                    CloudMessage comment;
                                    switch (cloudPost.getPOST_TYPE()) {
                                        case PostTypes.TEXT:
                                            privateCommentType = MessageType.PRIVATE_COMMENT_TEXT;

                                            break;
                                        case PostTypes.SINGLE_POST:
                                        case PostTypes.VIDEO_POST:

                                            privateCommentType = MessageType.PRIVATE_COMMENT_IMAGE_SINGLE;

                                            break;


                                    }


                                    String urls[] = cloudPost.getPOST_URL().split(",");
                                    comment = new CloudMessage(privateCommentType, information.getMainUserID()
                                            , messageTime,
                                            makeText, urls[0], MessageState.SENT);

                                    createPrivateComment(comment);


                                } else {
                                    Toast.makeText(CommentPage.this, "You can't send a private comment in this post", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(CommentPage.this, "You can't send a private comment", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CommentPage.this, "Private comment is not allowed", Toast.LENGTH_SHORT).show();
                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        writeMessage.setText("");

    }

}
