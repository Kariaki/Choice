package com.kariaki.choice.UI.Comment;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.DialogBox.OptionBottomSheet;
import com.kariaki.choice.UI.Profiles.EditFromBottom;
import com.kariaki.choice.UI.util.Theme;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import static android.content.Context.MODE_PRIVATE;

public class PrivateCommentHelper extends BottomSheetDialogFragment {


    public interface onClickListeners{

        void onClickConfirm(String text);
        void onClickCancel();


    }

    private EditFromBottom.onClickListeners clickListeners;

    public void setClickListeners(EditFromBottom.onClickListeners clickListeners) {
        this.clickListeners = clickListeners;
    }

    int maxLength=150;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    int visibility= View.VISIBLE;
    String text="Edit info";

    public void setText(String text){
        this.text=text;
    }
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    private EmojiEditText ediAboutTextBox;
    private ImageView editAboutSwitchKeyboard;
    private ImageView editAboutCancel,editAboutFinish;
    private RelativeLayout curveHolder;
    TextView sendMarkedImages;

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View view=View.inflate(getContext(), R.layout.comment_privately,null);
        editAboutFinish=view.findViewById(R.id.editAboutFinish);
        editAboutCancel=view.findViewById(R.id.editAboutCancel);
        curveHolder=view.findViewById(R.id.curveHolder);
        editAboutSwitchKeyboard=view.findViewById(R.id.editAboutSwitchKeyboard);
        ediAboutTextBox=view.findViewById(R.id.ediAboutTextBox);
        dialog.setContentView(view);
        ( (View)view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(view.findViewById(R.id.editAboutRoot)).build(ediAboutTextBox);


        sendMarkedImages=view.findViewById(R.id.sendMarkedImages);

        sendMarkedImages.setText(text);

        editAboutSwitchKeyboard.setOnClickListener(v->{
            switchKeyBoard(emojiPopup);
        });
        editAboutCancel.setOnClickListener(v->{
            clickListeners.onClickCancel();
        });
        editAboutSwitchKeyboard.setVisibility(visibility);


        ediAboutTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().isEmpty()){
                    editAboutFinish.setEnabled(false);
                }else {
                    editAboutFinish.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    editAboutFinish.setEnabled(false);
                }else {
                    editAboutFinish.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    editAboutFinish.setEnabled(false);
                }else {
                    editAboutFinish.setEnabled(true);
                }
            }
        });
        editAboutFinish.setOnClickListener(v->{
            if(!ediAboutTextBox.getText().toString().isEmpty()) {

                clickListeners.onClickConfirm(ediAboutTextBox.getText().toString());
            }
        });


        loadTheme();

    }

    @Override
    public void onStart() {
        super.onStart();
        loadTheme();
    }

    private void switchKeyBoard(EmojiPopup emojiPopup){


        if(emojiPopup.isShowing()){
            emojiPopup.toggle();
            editAboutSwitchKeyboard.setImageResource(R.drawable.keyboard);
        }else {
            emojiPopup.toggle();
            editAboutSwitchKeyboard.setImageResource(R.drawable.emoji);
        }


    }


    public void setTextColors(int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                curveHolder.setBackground(getContext().getResources().getDrawable(R.drawable.bottom_design_light));
                ediAboutTextBox.setTextColor(getContext().getResources().getColor(R.color.textHeaderColor));
                ediAboutTextBox.setHintTextColor(getContext().getResources().getColor(R.color.textContext));

                break;
            case Theme.DEEP:
                curveHolder.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bottom_design_dark));

                ediAboutTextBox.setTextColor(getContext().getResources().getColor(R.color.whiteGreen));
                ediAboutTextBox.setHintTextColor(getContext().getResources().getColor(R.color.textContext));


                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.LIGHT);

        setTextColors(currentTheme);


    }


}
