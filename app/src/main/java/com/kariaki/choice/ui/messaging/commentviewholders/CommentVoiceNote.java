package com.kariaki.choice.ui.messaging.commentviewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.messaging.chatviewholders.ChatMainViewHolder;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

import java.io.IOException;

public class CommentVoiceNote extends ChatMainViewHolder {
    ImageView senderImage,playCommentVoiceNote;
    ProgressBar commentCoiceNote;
    TextView progressDuration,replyClick,commentTime;
    EmojiTextView senderName;
    public CommentVoiceNote(@NonNull View itemView) {
        super(itemView);
        senderImage=itemView.findViewById(R.id.senderImage);
        playCommentVoiceNote=itemView.findViewById(R.id.playCommentVoiceNote);
        progressDuration=itemView.findViewById(R.id.progressDuration);
        replyClick=itemView.findViewById(R.id.replyClick);
        commentTime=itemView.findViewById(R.id.commentTime);
        senderName=itemView.findViewById(R.id.senderName);
        commentCoiceNote=itemView.findViewById(R.id.commentText);

    }
    ChatAdapter.OnclickListener LISTENER;
    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

        Message message=(Message)type;

        LISTENER = onclickListener;

        long timestamp = message.getMessageTIME();
        String time = TimeFactor.getDetailedDate(timestamp, System.currentTimeMillis());
        commentTime.setText(time);
        String messageSplit=message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0];
        progressDuration.setText(messageSplit);
        commentTime.setOnClickListener(v->{
            try {

                YoYo.with(Techniques.RollIn).duration(200).playOn(playCommentVoiceNote);

                onclickListener.playPauseButton(getLayoutPosition(), playCommentVoiceNote, commentCoiceNote, progressDuration, message.getMessageURL());



            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
