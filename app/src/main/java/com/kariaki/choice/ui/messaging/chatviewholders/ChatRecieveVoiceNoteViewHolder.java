package com.kariaki.choice.ui.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.Time.TimeFactor;
import com.kariaki.choice.ui.util.UserNaming;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecieveVoiceNoteViewHolder extends ChatMainViewHolder {

    Context CONTEXT;
    private ImageView playButton, repliedImagePreview;
    private TextView playDuration, voiceNoteTime;
    private ProgressBar progress;
    private CircleImageView senderImage;
    String outputFile;
    Message MESSAGE;
    TextView recieveVoiceNoteSenderName, replied;
    RelativeLayout recieveVoiceNoteParent, reciveCommentReplyContainter, recieveCommentTextMessageContainer, replyBottomContainer;
    CircleImageView recieveVoiceNoteSenderImage;
    private ProgressBar voiceNoteDownloadProgressBar;
    EmojiTextView messageContent;
    ImageButton fileDownloadButton;
    LinearLayout repliedholder;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    public ChatRecieveVoiceNoteViewHolder(@NonNull View itemView) {
        super(itemView);
        playButton = itemView.findViewById(R.id.recieveVoiceNotePlay);

        playDuration = itemView.findViewById(R.id.recieveVoiceNoteDuration);
        repliedImagePreview = itemView.findViewById(R.id.repliedImagePreview);
        reciveCommentReplyContainter = itemView.findViewById(R.id.reciveCommentReplyContainter);
        replyBottomContainer = itemView.findViewById(R.id.replyBottomContainer);
        recieveVoiceNoteSenderImage = itemView.findViewById(R.id.recieveVoiceNoteSenderImage);
        progress = itemView.findViewById(R.id.recieveVoiceNoteProgressBar);
        messageContent = itemView.findViewById(R.id.messageContent);
        recieveCommentTextMessageContainer = itemView.findViewById(R.id.recieveCommentTextMessageContainer);
        senderImage = itemView.findViewById(R.id.recieveVoiceNoteSenderImage);
        repliedholder = itemView.findViewById(R.id.repliedholder);
        replied = itemView.findViewById(R.id.replied);
        recieveVoiceNoteSenderName = itemView.findViewById(R.id.recieveVoiceNoteSenderName);
        voiceNoteDownloadProgressBar = itemView.findViewById(R.id.voiceNoteDownloadProgressBar);
        fileDownloadButton = itemView.findViewById(R.id.fileDownloadButton);
        voiceNoteTime = itemView.findViewById(R.id.voiceNoteTime);
        playButton.setVisibility(View.VISIBLE);
        recieveVoiceNoteParent = itemView.findViewById(R.id.recieveVoiceNoteParent);

    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener
            , int currentlyPlaying) {
        CONTEXT = context;
        progress.setProgress(0);


        if (getTheme() == Theme.DEEP) {

            //top is bottom
            recieveCommentTextMessageContainer.setBackground(context.getDrawable(R.drawable.recieve_message_text_dark));
            replyBottomContainer.setBackground(context.getDrawable(R.drawable.recieve_bottom_dark));
            reciveCommentReplyContainter.setBackground(context.getDrawable(R.drawable.recieve_top_background_dark));
            //message.setTextColor(context.getResources().getColor(R.color.whiteGreen));
            messageContent.setTextColor(context.getResources().getColor(R.color.whiteGreen));

        }
        if (getTheme() == Theme.LIGHT) {
            messageContent.setTextColor(context.getResources().getColor(R.color.textColor));
            playDuration.setTextColor(context.getResources().getColor(R.color.textColor));
        }


      /*  progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onclickListener.onSeekBarChange(getAdapterPosition(),progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


       */
        if (onclickListener.showName()) {
            recieveVoiceNoteSenderName.setVisibility(View.VISIBLE);
            repliedholder.setVisibility(View.VISIBLE);


        } else {
            recieveVoiceNoteSenderName.setVisibility(View.GONE);
            repliedholder.setVisibility(View.GONE);
            senderImage.setVisibility(View.GONE);
        }

        Message message = (Message) type;

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return false;
            }
        });

        onclickListener.nameChoiceUser(getAdapterPosition(), recieveVoiceNoteSenderName, senderImage);

        onclickListener.setMessageToSeen(getAdapterPosition());
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return false;
            }
        });

        long timestamp = message.getMessageTIME();
        String time = TimeFactor.getDetailedDate(timestamp, "HH:mm");
        voiceNoteTime.setText(time);

        String retrieved = message.getMessageCONTENT();

        switch (message.getMessageTYPE()) {
            case MessageType.VOICE_NOTE + MessageType.RECIEVED:
                reciveCommentReplyContainter.setVisibility(View.GONE);
                replyBottomContainer.setVisibility(View.GONE);
                repliedholder.setVisibility(View.GONE);
                break;
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
                reciveCommentReplyContainter.setVisibility(View.VISIBLE);
                replyBottomContainer.setVisibility(View.VISIBLE);
                String[] newContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
                retrieved = newContent[0];
                messageContent.setText(newContent[1]);


                if (onclickListener.showName()) {
                    String userID = newContent[2];
                    if (userID.equals(new UserInformation(context).getMainUserID())) {
                        replied.setText("You");
                    } else {

                        UserNaming.getUserName(userID, replied);
                    }
                    String repliedText = "replied " + replied.getText().toString();
                    replied.setText(repliedText);
                }

                break;
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:

                reciveCommentReplyContainter.setVisibility(View.VISIBLE);
                replyBottomContainer.setVisibility(View.VISIBLE);
                String[] voiceNoteContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
                String duration = TimeFactor.convertMillieToHMmSs(Long.parseLong(voiceNoteContent[1]));
                retrieved = voiceNoteContent[0];
                String text = "voice note | " + duration;
                messageContent.setText(text);


                if (onclickListener.showName()) {
                    String userID = voiceNoteContent[2];
                    if (userID.equals(new UserInformation(context).getMainUserID())) {
                        replied.setText("You");
                    } else {

                        UserNaming.getUserName(userID, replied);
                    }
                    String repliedText = "replied " + replied.getText().toString();
                    replied.setText(repliedText);
                }

                break;
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
                voiceNoteContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
                String url = voiceNoteContent[1];
                retrieved = voiceNoteContent[0];
                Glide.with(context.getApplicationContext()).load(url).into(repliedImagePreview);
                messageContent.setVisibility(View.INVISIBLE);
                repliedImagePreview.setVisibility(View.VISIBLE);
                reciveCommentReplyContainter.setVisibility(View.VISIBLE);
                replyBottomContainer.setVisibility(View.VISIBLE);


                if (onclickListener.showName()) {
                    String userID = voiceNoteContent[2];
                    if (userID.equals(new UserInformation(context).getMainUserID())) {
                        replied.setText("You");
                    } else {

                        UserNaming.getUserName(userID, replied);
                    }
                    String repliedText = "replied " + replied.getText().toString();
                    replied.setText(repliedText);
                }

                break;


        }

        long voiceNoteDuration = Long.parseLong(retrieved);
        playDuration.setText(TimeFactor.convertMillieToHMmSs(voiceNoteDuration));


        File file = new File(message.getMessageURL());

        switch (message.getMessageState()) {
            case MessageState
                    .SEEN:

                if (file.exists()) {

                    fileDownloadButton.setVisibility(View.INVISIBLE);
                    playButton.setVisibility(View.VISIBLE);
                } else {
                    fileDownloadButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.INVISIBLE);
                    progress.setProgress(0);
                }


                break;
            case MessageState.RECEIVED:
            case MessageState.SENT:

                // playButton.setVisibility(View.VISIBLE);

                //onclickListener.downloadVoiceNote(getAdapterPosition(), voiceNoteDownloadProgressBar, fileDownloadButton, playButton);

                fileDownloadButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);


                break;
            default:
                fileDownloadButton.setVisibility(View.INVISIBLE);
                voiceNoteDownloadProgressBar.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);
                break;
            case MessageState.FAILED:
                fileDownloadButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.INVISIBLE);
                break;


        }


        fileDownloadButton.setOnClickListener(v -> {

            fileDownloadButton.setVisibility(View.INVISIBLE);
            onclickListener.downloadVoiceNote(getAdapterPosition(), voiceNoteDownloadProgressBar, fileDownloadButton, playButton);

        });


        recieveVoiceNoteSenderImage.setOnClickListener(v -> {
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });

        if (onclickListener.showName()) {
            recieveVoiceNoteSenderName.setVisibility(View.VISIBLE);
        } else {
            recieveVoiceNoteSenderName.setVisibility(View.GONE);
        }
        MESSAGE = message;
        progress.setProgress(0);

        senderImage.setOnClickListener(v -> {
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });

        progress.setOnClickListener(v -> {
            try {

                if (file.exists()) {
                    YoYo.with(Techniques.RollIn).duration(200).playOn(playButton);

                    onclickListener.playPauseButton(getAdapterPosition(), playButton, progress, playDuration, message.getMessageURL().split(",")[0]);

                } else {
                    fileDownloadButton.setVisibility(View.INVISIBLE);
                    onclickListener.downloadVoiceNote(getAdapterPosition(), voiceNoteDownloadProgressBar, fileDownloadButton, playButton);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (currentlyPlaying == getLayoutPosition()) {
            playButton.setImageResource(R.drawable.pause_filled);

        } else {
            playButton.setImageResource(R.drawable.play_filled);
            progress.setProgress(0);

        }

        playButton.setOnClickListener(v -> {
            try {


                YoYo.with(Techniques.RollIn).duration(200).playOn(playButton);

                onclickListener.playPauseButton(getAdapterPosition(), playButton, progress, playDuration, message.getMessageURL().split(",")[0]);


            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }


}
