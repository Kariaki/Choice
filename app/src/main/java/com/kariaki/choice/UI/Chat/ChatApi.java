package com.kariaki.choice.UI.Chat;

import android.app.Activity;

import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;

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
