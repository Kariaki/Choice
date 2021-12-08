package com.kariaki.choice.ui.Chat;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.Theme;

import static android.content.Context.MODE_PRIVATE;

public class MessageOptions extends BottomSheetDialogFragment {


    boolean myComment = true;
    public void setMyComment(boolean myComment) {
        this.myComment = myComment;
    }

    public interface optionClickListeners {
        void onCopyMessage();

        void onReplyMessage();

        void onDeleteMessage();
        void onForwardMessage();
    }

    private MessageOptions.optionClickListeners optionClickListeners;

    public void setOptionClickListeners(MessageOptions.optionClickListeners optionClickListeners) {
        this.optionClickListeners = optionClickListeners;
    }

    Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    LinearLayout deleteComment, replyComment, copyComment, mainRoot,forwardmessage;
    TextView deleteCommentText, replyCommentText, copyCommentText,forwardMessageText;

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.chat_option, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        copyComment = contentView.findViewById(R.id.copyComment);
        replyComment = contentView.findViewById(R.id.replyComment);
        forwardMessageText=contentView.findViewById(R.id.forwardMessageText);
        deleteComment = contentView.findViewById(R.id.deleteComment);
        replyCommentText = contentView.findViewById(R.id.replyCommentText);
        deleteCommentText = contentView.findViewById(R.id.deleteCommentText);
        forwardmessage=contentView.findViewById(R.id.forwardmessage);
        mainRoot = contentView.findViewById(R.id.mainRoot);
        copyCommentText = contentView.findViewById(R.id.copyCommentText);

        if (myComment) {

            copyComment.setVisibility(View.VISIBLE);
        } else {
            copyComment.setVisibility(View.GONE);
        }

        copyComment.setOnClickListener(v -> {
            optionClickListeners.onCopyMessage();
            dismiss();
        });

        deleteComment.setOnClickListener(v -> {
            optionClickListeners.onDeleteMessage();
            dismiss();
        });
        replyComment.setOnClickListener(v -> {
            optionClickListeners.onReplyMessage();
            dismiss();
        });
        forwardmessage.setOnClickListener(v->{
            optionClickListeners.onForwardMessage();
            dismiss();
        });
        loadTheme();
    }


    public void setTextColors(TextView[] text, int currentTheme) {

        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textColor);

                //mainRoot.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                mainRoot.setBackground(getContext().getDrawable(R.drawable.bottom_design_light));

                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
               // mainRoot.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                mainRoot.setBackground(getContext().getDrawable(R.drawable.bottom_design_dark));


                break;

        }
    }


    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView[] textViews = {deleteCommentText, replyCommentText, copyCommentText,forwardMessageText};
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }
}
