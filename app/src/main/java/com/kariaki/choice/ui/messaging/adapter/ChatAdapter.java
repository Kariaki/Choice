package com.kariaki.choice.ui.Messaging.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatDefaultViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatInvite.GroupChatInvite;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatInvite.GroupChatInviteSent;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatInviteRequestViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatMainViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatMessageListHeader;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatReceiveTextReplyImageVideo;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatRecieveImageViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatRecieveTextViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatRecieveVoiceNoteViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatSendImageViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatSendTextViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatSendVoiceNoteViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatTextReplyVideoImage;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.GroupChatFirstMessageViewHolder;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.PrivateComment.ChatRecievePrivateCommentImageSingle;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.PrivateComment.ChatRecievePrivateCommentTextText;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.PrivateComment.ChatSendPrivateCommentImageSingle;
import com.kariaki.choice.ui.Messaging.ChatViewHolders.PrivateComment.ChatSendPrivateCommentTextText;
import com.kariaki.choice.ui.Messaging.CommentViewHolders.CommentText;
import com.kariaki.choice.ui.Messaging.CommentViewHolders.CommentVoiceNote;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatMainViewHolder> {

    List<Message> message = new ArrayList<>();
    Context CONTEXT;
    int messageState;
    int theme;

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public interface OnclickListener {
        void OnclickChatProfileImage(int position);

        int currentlyPlaying();

        void playPauseButton(int position, ImageView playPauseButton, ProgressBar voiceNoteProgress, TextView progressDuration, String path) throws IOException;

        boolean showName();

        void onClickPrivateCommentMessage(int position);

        void downloadFiles(int position, ProgressBar progressBar, ImageView downloadButton, ImageView imagePreview);

        void onClickImageMessage(int position);

        void nameChoiceUser(int position, TextView nameTextView, CircleImageView profileImageView);

        void downloadVoiceNote(int position, ProgressBar progressBar, ImageView downloadButton, ImageView replaceButton);

        void messageIsSeen(int position, ImageView indicator);

        void uploadVoiceNote(int position, ProgressBar uploadProgress, ImageView playButton, ImageView send_state);

        void uploadFiles(int position, ProgressBar uploadProgress, ImageView playButton, ImageView send_state);

        void setMessageToSeen(int position);

        void onLongClickMessage(int position, View itemView);

        void onSeekBarChange(int position, int progress);
        void sendMessageText(int position);
        void setMessageTime(int position,TextView view);

        void onMessageClickListener(int position,View itemView);
        void onReplyClick(int position);
        DatabaseReference replyCounter(int position);


    }


    private int location;


    public interface onProgressChangeListener {
        void onProgressChange(int position, int progress);
    }

    private onProgressChangeListener onProgressChangeListener;

    public void setOnProgressChangeListener(ChatAdapter.onProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    private OnclickListener onclickListener;
    private List<Integer> currentlyPlaying = new ArrayList<>();

    public ChatAdapter(Context context) {
        this.CONTEXT = context;

    }

    public void setCurrentlyPlaying(List<Integer> currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }

    public void setOnclickListener(OnclickListener onclickListener) {
        this.onclickListener = onclickListener;
    }

    private int stopPlaying = -1;

    public void setLocation(int location) {
        this.location = location;
    }

    public void setStopPlaying(int stopPlaying) {
        this.stopPlaying = stopPlaying;
    }

    @NonNull
    @Override
    public ChatMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        EmojiManager.install(new IosEmojiProvider());

        switch (viewType) {

            case MessageType.CHAT_INVITE_REQUEST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_invite_request, parent, false);
                return new ChatInviteRequestViewHolder(view);
            case MessageType.IMAGE:
            case MessageType.VIDEO:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_image, parent, false);
                return new ChatSendImageViewHolder(view);

            case MessageType.IMAGE + MessageType.RECIEVED:
            case MessageType.VIDEO + MessageType.RECIEVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieve_image, parent, false);
                return new ChatRecieveImageViewHolder(view);


            case MessageType.TEXT:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_message_text, parent, false);
                return new ChatSendTextViewHolder(view);

            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT:
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_text_reply_image_video, parent, false);

                return new ChatTextReplyVideoImage(view);

            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_text_reply_image_vide, parent, false);

                return new ChatReceiveTextReplyImageVideo(view);

            case MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieve_message_text, parent, false);
                return new ChatRecieveTextViewHolder(view);

            case MessageType.PRIVATE_COMMENT_IMAGE_SINGLE:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_private_comment_image, parent, false);

                return new ChatSendPrivateCommentImageSingle(view);
            case MessageType.PRIVATE_COMMENT_IMAGE_SINGLE + MessageType.RECIEVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieved_private_comment_single_image, parent, false);
                return new ChatRecievePrivateCommentImageSingle(view);


            case MessageType.PRIVATE_COMMENT_TEXT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_private_comment_text_post_text, parent, false);
                return new ChatSendPrivateCommentTextText(view);

            case MessageType.PRIVATE_COMMENT_TEXT + MessageType.RECIEVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieve_private_comment_text, parent, false);
                return new ChatRecievePrivateCommentTextText(view);
            case MessageType.HEADER:


                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_date_header, parent, false);
                return new ChatMessageListHeader(view);

            case MessageType.VOICE_NOTE:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_voice_note, parent, false);
                return new ChatSendVoiceNoteViewHolder(view);

            case MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:

            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieve_voice_note, parent, false);
                return new ChatRecieveVoiceNoteViewHolder(view);

            case MessageType.GROUP_CHAT_FIRST_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_group_chat_first_message, parent, false);
                return new GroupChatFirstMessageViewHolder(view);

            case MessageType.GROUP_CHAT_INVITE + MessageType.RECIEVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_invite_design, parent, false);
                return new GroupChatInviteSent(view);



            case MessageType.GROUP_CHAT_INVITE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_invite_sent, parent, false);
                return new GroupChatInvite(view);

            case MessageType.COMMENT_TEXT:
            case MessageType.COMMENT_TEXT +MessageType.COMMENT_TEXT+ MessageType.COMMENT_REPLY + MessageType.COMMENT_TEXT:
            case MessageType.COMMENT_TEXT +MessageType.COMMENT_TEXT+ MessageType.COMMENT_REPLY + MessageType.COMMENT_VOICE_NOTE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_text, parent, false);
                return new CommentText(view);

            case MessageType.COMMENT_VOICE_NOTE:
            case MessageType.COMMENT_VOICE_NOTE +MessageType.COMMENT_VOICE_NOTE+ MessageType.COMMENT_REPLY + MessageType.COMMENT_TEXT:
            case MessageType.COMMENT_VOICE_NOTE +MessageType.COMMENT_VOICE_NOTE + MessageType.COMMENT_REPLY + MessageType.COMMENT_VOICE_NOTE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_voice_note, parent, false);
                return new CommentVoiceNote(view);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_default_view_holder, parent, false);
                return new ChatDefaultViewHolder(view);
        }

    }

    public void setMessage(List<Message> message) {
        this.message = message;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMainViewHolder holder, int position) {
        MessageType type = message.get(position);
        holder.setTheme(theme);
        holder.bindType(type, CONTEXT, this.onclickListener, -1);


        holder.progressChange(onProgressChangeListener);

    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message absoluteMessage = message.get(position);
        messageState = absoluteMessage.getChannelType();

        return message.get(position).getMessageType();
    }
}
