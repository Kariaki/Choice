package com.kariaki.choice.ui.comment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.Theme;

import static android.content.Context.MODE_PRIVATE;

public class CommentOption extends BottomSheetDialogFragment {


    boolean myComment=true;

    public void setMyComment(boolean myComment) {
        this.myComment = myComment;
    }

    public interface optionClickListeners{
        void onCopyComment();
        void onReplyComment();
        void onDeleteComment();
    }
    private optionClickListeners optionClickListeners;

    public void setOptionClickListeners(CommentOption.optionClickListeners optionClickListeners) {
        this.optionClickListeners = optionClickListeners;
    }

    LinearLayout deleteComment,replyComment,copyComment,mainRoot;
    TextView deleteCommentText,replyCommentText,copyCommentText;
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView=View.inflate(getContext(), R.layout.comment_option_design,null);
        dialog.setContentView(contentView);
        ( (View)contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        copyComment=contentView.findViewById(R.id.copyComment);
        replyComment=contentView.findViewById(R.id.replyComment);
        deleteComment=contentView.findViewById(R.id.deleteComment);
        replyCommentText=contentView.findViewById(R.id.replyCommentText);
        deleteCommentText=contentView.findViewById(R.id.deleteCommentText);
        mainRoot=contentView.findViewById(R.id.mainRoot);
        copyCommentText=contentView.findViewById(R.id.copyCommentText);

        if(myComment){

            deleteComment.setVisibility(View.VISIBLE);
        }else {
            deleteComment.setVisibility(View.GONE);
        }

        copyComment.setOnClickListener(v->{
            optionClickListeners.onCopyComment();
        });

        deleteComment.setOnClickListener(v->{
            optionClickListeners.onDeleteComment();
        });
        replyComment.setOnClickListener(v->{
            optionClickListeners.onReplyComment();
        });
        loadTheme();
    }


    public void setTextColors(TextView[] text, int currentTheme) {

        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textColor);

               // mainRoot.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                mainRoot.setBackground(getContext().getDrawable(R.drawable.bottom_design_light));

                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                //mainRoot.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                mainRoot.setBackground(getContext().getDrawable(R.drawable.bottom_design_dark));


                break;

        }
    }


    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView[] textViews = {deleteCommentText,replyCommentText,copyCommentText};
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }

}
