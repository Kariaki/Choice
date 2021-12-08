package com.kariaki.choice.ui.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.ui.util.Time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.io.IOException;

public class ChatSendVoiceNoteViewHolder extends ChatMainViewHolder {

    private ImageView playButton, retryButton, send_state, downloadVoceNote, repliedImagePreview;
    private TextView duration;
    private ProgressBar progress;
    private ProgressBar uploadVoiceNoteProgress;
    private TextView sendVoiceNoteTime, replied, recieveVoiceNoteSenderName;
    private RelativeLayout reciveCommentReplyContainter, replyBottomContainer;
    String outputFile;
    EmojiTextView messageContent;

    LinearLayout repliedholder;


    public ChatSendVoiceNoteViewHolder(@NonNull View itemView) {
        super(itemView);
        playButton = itemView.findViewById(R.id.sendVoiceNotePlay);


        duration = itemView.findViewById(R.id.sendVoiceNoteDuration);
        replyBottomContainer = itemView.findViewById(R.id.replyBottomContainer);
        reciveCommentReplyContainter = itemView.findViewById(R.id.reciveCommentReplyContainter);
        send_state = itemView.findViewById(R.id.send_state);
        recieveVoiceNoteSenderName = itemView.findViewById(R.id.recieveVoiceNoteSenderName);
        downloadVoceNote = itemView.findViewById(R.id.downloadVoiceNote);
        repliedImagePreview = itemView.findViewById(R.id.repliedImagePreview);
        sendVoiceNoteTime = itemView.findViewById(R.id.sendVoiceNoteTime);
        messageContent = itemView.findViewById(R.id.messageContent);
        progress = itemView.findViewById(R.id.sendVoiceNoteProgressBar);
        retryButton = itemView.findViewById(R.id.retryButton);
        uploadVoiceNoteProgress = itemView.findViewById(R.id.uploadVoiceNoteProgress);

    }

    private Context CONTEXT;
    Message MESSAGE;
    ChatAdapter.OnclickListener LISTENER;

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener,
                         int currentlyPlaying) {


        Message message = (Message) type;
        MESSAGE = message;
        LISTENER = onclickListener;
        this.CONTEXT = context;
        outputFile = message.getMessageURL();
        long timestamp = message.getMessageTIME();
        String time = TimeFactor.getDetailedDate(timestamp, "HH:mm");
        sendVoiceNoteTime.setText(time);


      /*  if (getTheme() == Theme.DEEP) {
            //top is bottom
            //recieveCommentTextMessageContainer.setBackground(context.getDrawable(R.drawable.recieve_message_text_dark));
            replyTopContainer.setBackground(context.getDrawable(R.drawable.recieve_top_background_dark));
            replyBottomContainer.setBackground(context.getDrawable(R.drawable.recieve_bottom_dark));
            //message.setTextColor(context.getResources().getColor(R.color.whiteGreen));
            replyContent.setTextColor(context.getResources().getColor(R.color.whiteGreen));

        }

       */
/*
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return false;
            }
        });

        uploadVoiceNoteProgress.setVisibility(View.INVISIBLE);

        onclickListener.messageIsSeen(getAdapterPosition(), send_state);
            playButton.setVisibility(View.VISIBLE);
        String retrieved = message.getMessageCONTENT();

        switch (message.getMessageTYPE()) {
            case MessageType.VOICE_NOTE:
                reciveCommentReplyContainter.setVisibility(View.GONE);
                replyBottomContainer.setVisibility(View.GONE);
                break;
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE:
                reciveCommentReplyContainter.setVisibility(View.VISIBLE);
                replyBottomContainer.setVisibility(View.VISIBLE);
                String[] newContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
                retrieved = newContent[0];
                messageContent.setText(newContent[1]);


                break;
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE:

                reciveCommentReplyContainter.setVisibility(View.VISIBLE);
                replyBottomContainer.setVisibility(View.VISIBLE);
                String[] voiceNoteContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
                String duration = TimeFactor.convertMillieToHMmSs(Long.parseLong(voiceNoteContent[1]));
                retrieved = voiceNoteContent[0];
                messageContent.setText(duration);
                String text = "voice note | " + duration;
                messageContent.setText(text);

                break;

            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE:
                repliedImagePreview.setVisibility(View.VISIBLE);
                reciveCommentReplyContainter.setVisibility(View.VISIBLE);
                messageContent.setVisibility(View.INVISIBLE);
                replyBottomContainer.setVisibility(View.VISIBLE);
                voiceNoteContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
                duration = voiceNoteContent[1];
                retrieved = voiceNoteContent[0];

                Glide.with(context).load(duration).into(repliedImagePreview);

                break;

        }

        File file = new File(outputFile);

        switch (message.getMessageState()) {
            case MessageState.SEEN:

                if (file.exists()) {
                    send_state.setImageResource(R.drawable.check);
                    playButton.setVisibility(View.VISIBLE);
                } else {
                    playButton.setVisibility(View.GONE);
                    downloadVoceNote.setVisibility(View.VISIBLE);
                }

                break;
            case MessageState.SENT:
                if (file.exists()) {
                    playButton.setVisibility(View.VISIBLE);
                } else {
                    playButton.setVisibility(View.GONE);
                    downloadVoceNote.setVisibility(View.VISIBLE);
                }
                send_state.setImageResource(R.drawable.not_seen_check);

                break;
            case MessageState.SENDING:
                send_state.setImageResource(R.drawable.check_white);
                onclickListener.uploadVoiceNote(getAdapterPosition(), uploadVoiceNoteProgress, playButton, send_state);
                break;
            case MessageState.FAILED:
                playButton.setVisibility(View.GONE);
                send_state.setImageResource(R.drawable.check_red);
                retryButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);

                downloadVoceNote.setVisibility(View.INVISIBLE);
                break;
            default:

                playButton.setVisibility(View.VISIBLE);
                send_state.setImageResource(R.drawable.not_seen_check);
                retryButton.setVisibility(View.INVISIBLE);

                break;

        }

        downloadVoceNote.setOnClickListener(v -> {
             // onclickListener.downloadVoiceNote(getAdapterPosition(), uploadVoiceNoteProgress, downloadButton, playButton);
        });

        retryButton.setOnClickListener(v -> {
            onclickListener.uploadVoiceNote(getAdapterPosition(), uploadVoiceNoteProgress, playButton, send_state);

        });
        long duration_time = Long.parseLong(retrieved);
        duration.setText(TimeFactor.convertMillieToHMmSs(duration_time));


        if (currentlyPlaying == getLayoutPosition()) {

            playButton.setImageResource(R.drawable.pause_filled);

        } else {

            playButton.setImageResource(R.drawable.play_filled);
            progress.setProgress(0);

        }


        progress.setOnClickListener(v -> {


            if(file.exists()) {

                try {

                    YoYo.with(Techniques.RollIn).duration(200).playOn(playButton);

                    onclickListener.playPauseButton(getLayoutPosition(), playButton, progress, duration, message.getMessageURL());


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                 onclickListener.downloadVoiceNote(getAdapterPosition(), uploadVoiceNoteProgress, downloadVoceNote, playButton);

            }



        });
        playButton.setOnClickListener(v -> {
            try {

                YoYo.with(Techniques.RollIn).duration(200).playOn(playButton);

                onclickListener.playPauseButton(getLayoutPosition(), playButton, progress, duration, message.getMessageURL());


            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }


}
