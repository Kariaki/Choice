package com.kariaki.choice.ui.messaging.chatviewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.util.time.TimeFactor;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecieveImageViewHolder extends ChatMainViewHolder {

    private TextView recieveImageSenderName, remainingImages, sendMessaageTextTime;
    private CircleImageView recieveImageSenderImage;
    private RelativeLayout imageNameHolder;
    private ImageView messageImage, imageTwo, imageThree, imageFour, videoMessage;
    private ProgressBar imageDownloadProgress;
    private TextView videoDuration;

    private View split2, split;
    private static final String LIST_OF_USERS = "users";

    public ChatRecieveImageViewHolder(@NonNull View itemView) {
        super(itemView);
        recieveImageSenderImage = itemView.findViewById(R.id.recieveImageSenderImage);
        recieveImageSenderName = itemView.findViewById(R.id.recieveImageSenderName);
        imageThree = itemView.findViewById(R.id.imageThree);
        videoMessage = itemView.findViewById(R.id.videoMessage);
        remainingImages = itemView.findViewById(R.id.remainingImages);
        videoDuration = itemView.findViewById(R.id.videoDuration);
        imageFour = itemView.findViewById(R.id.imageFour);
        sendMessaageTextTime = itemView.findViewById(R.id.sendMessaageTextTime);
        imageDownloadProgress = itemView.findViewById(R.id.imageDownloadProgress);
        split = itemView.findViewById(R.id.split);
        split2 = itemView.findViewById(R.id.split2);
        imageTwo = itemView.findViewById(R.id.imageTwo);
        messageImage = itemView.findViewById(R.id.imageOne);
        imageNameHolder = itemView.findViewById(R.id.imageNameHolder);
    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

        Message message = (Message) type;

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return false;
            }
        });

        if (message.getMessageTYPE() == MessageType.VIDEO + MessageType.RECIEVED) {
            videoMessage.setVisibility(View.VISIBLE);

            videoMessage.setVisibility(View.VISIBLE);
            videoDuration.setVisibility(View.VISIBLE);
            String[] contentsplit = message.getMessageCONTENT().split(",");
            String duration = contentsplit[1];

            videoDuration.setText(duration);


        }

        String[] filePathSplit = message.getMessageURL().split(",");
        long timeStamp = message.getMessageTIME();

        sendMessaageTextTime.setText(TimeFactor.getDetailedDate(timeStamp, "HH:mm"));

        onclickListener.setMessageToSeen(getAdapterPosition());
        sendMessaageTextTime.setVisibility(View.VISIBLE);
        setViews(filePathSplit.length);
        onclickListener.setMessageToSeen(getAdapterPosition());
        File file = new File(filePathSplit[0]);

        switch (message.getMessageState()) {
            case MessageState
                    .SEEN:


                Glide.with(context.getApplicationContext()).

                        load(filePathSplit[0])
                        .

                                thumbnail(0.4f).

                        into(imageThree);


                break;
            case MessageState.RECEIVED:

            case MessageState.SENT:

                Glide.with(context.getApplicationContext()).

                        load(R.drawable.square_place_holder).

                        into(imageThree);


                break;

            case MessageState.FAILED:

                Glide.with(context.getApplicationContext()).load(R.drawable.square_place_holder).into(imageThree);

                imageDownloadProgress.setVisibility(View.INVISIBLE);
                break;


        }

        itemView.setOnClickListener(v -> {
            onclickListener.onClickImageMessage(getAdapterPosition());
        });

        if (onclickListener.showName()) {
            imageNameHolder.setVisibility(View.VISIBLE);
        } else {
            imageNameHolder.setVisibility(View.GONE);
            recieveImageSenderImage.setVisibility(View.GONE);
        }

        onclickListener.nameChoiceUser(getAdapterPosition(), recieveImageSenderName, recieveImageSenderImage);

        recieveImageSenderImage.setOnClickListener(v -> {
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });


    }

    private void setViews(int linkLength) {
        switch (linkLength) {
            case 2:
                split.setVisibility(View.GONE);
                break;
            case 1:
                split2.setVisibility(View.GONE);
                split.setVisibility(View.GONE);
                break;
            case 3:
                imageTwo.setVisibility(View.GONE);
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
