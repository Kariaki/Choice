package com.kariaki.choice.ui.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.ui.util.Time.TimeFactor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatSendImageViewHolder extends ChatMainViewHolder {

    private ImageView messageImage, imageTwo, imageThree, imageFour, send_state,
            imageSendRetry, videoMessage;
    private ProgressBar imageSendProgress;

    private TextView remainingImages, sendMessaageTextTime;
    private List<String> links = new ArrayList<>();
    private View split2, split;
    private TextView videoDuration;

    public ChatSendImageViewHolder(@NonNull View itemView) {
        super(itemView);
        imageSendProgress = itemView.findViewById(R.id.imageSendProgress);

        messageImage = itemView.findViewById(R.id.imageOne);
        remainingImages = itemView.findViewById(R.id.remainingImages);
        imageTwo = itemView.findViewById(R.id.imageTwo);
        videoDuration = itemView.findViewById(R.id.videoDuration);
        videoMessage = itemView.findViewById(R.id.videoMessage);
        send_state = itemView.findViewById(R.id.send_state);
        imageSendRetry = itemView.findViewById(R.id.imageSendRetry);
        sendMessaageTextTime = itemView.findViewById(R.id.sendMessaageTextTime);
        imageThree = itemView.findViewById(R.id.imageThree);
        split = itemView.findViewById(R.id.split);
        split2 = itemView.findViewById(R.id.split2);
        imageFour = itemView.findViewById(R.id.imageFour);
    }

    File file;

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener listener,
                         int currentlyPlaying) {

        Message theMessage = (Message) type;

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClickMessage(getAdapterPosition(),itemView);

                return false;
            }
        });

        if (theMessage.getMessageTYPE() == MessageType.VIDEO) {
            videoMessage.setVisibility(View.VISIBLE);
            videoDuration.setVisibility(View.VISIBLE);
            String[] contentsplit = theMessage.getMessageCONTENT().split(",");

            String duration = contentsplit[1];

            videoDuration.setText(duration);

        }

        String imageurl[] = theMessage.getMessageURL().split(",");

        long timeStamp = theMessage.getMessageTIME();

        sendMessaageTextTime.setText(TimeFactor.getDetailedDate(timeStamp, "HH:mm"));

        sendMessaageTextTime.setVisibility(View.VISIBLE);

        file = new File(imageurl[0]);

        setViews(imageurl.length, imageurl, context);
        ImageView playbutton = new ImageView(context);
        playbutton.setVisibility(View.GONE);


        listener.messageIsSeen(getAdapterPosition(), send_state);

        switch (theMessage.getMessageState()) {
            case MessageState.SEEN:

                if (file.exists()) {
                    imageSendProgress.setVisibility(View.INVISIBLE);
                    imageSendRetry.setVisibility(View.INVISIBLE);

                }
                /*else {
                    imageSendProgress.setVisibility(View.INVISIBLE);
                    imageSendRetry.setVisibility(View.INVISIBLE);
                    downloadFile.setVisibility(View.VISIBLE);
                }
                */

                send_state.setImageResource(R.drawable.check);
                break;
            case MessageState.SENT:

                send_state.setImageResource(R.drawable.not_seen_check);

                if (file.exists()) {
                    imageSendProgress.setVisibility(View.INVISIBLE);
                    imageSendRetry.setVisibility(View.INVISIBLE);
                } else {
                    imageSendRetry.setVisibility(View.INVISIBLE);
                    imageSendProgress.setVisibility(View.INVISIBLE);


                }

                break;
            case MessageState.SENDING:
                send_state.setImageResource(R.drawable.check_white);
                imageSendRetry.setVisibility(View.INVISIBLE);
                imageSendProgress.setVisibility(View.VISIBLE);
                listener.uploadFiles(getAdapterPosition(), imageSendProgress, playbutton, send_state);
                break;
            case MessageState.FAILED:
                imageSendProgress.setVisibility(View.INVISIBLE);
                imageSendRetry.setVisibility(View.VISIBLE);

                send_state.setImageResource(R.drawable.check_red);
                break;
            default:
                send_state.setImageResource(R.drawable.not_seen_check);

                imageSendProgress.setVisibility(View.INVISIBLE);
                imageSendRetry.setVisibility(View.INVISIBLE);
                break;


        }

        imageSendRetry.setOnClickListener(v -> {
            listener.uploadFiles(getAdapterPosition(), imageSendProgress, playbutton, send_state);

        });
        itemView.setOnClickListener(v -> {
            imageSendRetry.setVisibility(View.INVISIBLE);
            imageSendProgress.setVisibility(View.INVISIBLE);
            listener.onClickImageMessage(getAdapterPosition());
        });


    }

    private void setViews(int linkLength, String[] links, Context context) {
        switch (linkLength) {
            case 2:
                split.setVisibility(View.GONE);

                Glide.with(context.getApplicationContext()).load(links[0]).into(imageFour);
                Glide.with(context.getApplicationContext()).load(links[1]).into(imageThree);

                break;
            case 1:

                split.setVisibility(View.GONE);
                split2.setVisibility(View.GONE);


                Glide.with(context).load(links[0]).into(imageFour);
                break;
            case 3:
                imageTwo.setVisibility(View.GONE);
                Glide.with(context.getApplicationContext()).load(links[0]).into(imageFour);
                Glide.with(context.getApplicationContext()).load(links[1]).into(imageThree);
                Glide.with(context.getApplicationContext()).load(links[2]).into(messageImage);

                break;
            case 4:

                Glide.with(context.getApplicationContext()).load(links[0]).into(imageFour);
                Glide.with(context.getApplicationContext()).load(links[1]).into(imageThree);
                Glide.with(context.getApplicationContext()).load(links[2]).into(messageImage);
                Glide.with(context.getApplicationContext()).load(links[3]).into(imageThree);
                break;

        }
    }

    private void shareImages(String[] links, Context context) {
        ImageView[] imageViews = {imageThree, imageFour, messageImage, imageTwo};
        for (int i = 0; i < links.length; i++) {
            Glide.with(context).load(links[i]).into(imageViews[i]);
        }

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
