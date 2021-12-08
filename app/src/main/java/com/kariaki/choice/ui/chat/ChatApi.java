package com.kariaki.choice.ui.Chat;

import android.app.Activity;

import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatApi {

     public static ChoiceViewModel choiceViewModel;
     private List<Message>messages=new ArrayList<>();
     Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public static ChatApi getInstance(Activity activity){
        choiceViewModel=ChoiceViewModel.getInstance(activity.getApplication());
        return new ChatApi();
    }

    private ChatApi(){

     }

}
