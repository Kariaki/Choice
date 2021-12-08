package com.kariaki.choice.model.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.Database.Entities.ChatList;
import com.kariaki.choice.model.Database.Entities.ChoiceUser;
import com.kariaki.choice.model.Database.Entities.Message;
import com.kariaki.choice.model.Database.Entities.MyChatChannel;
import com.kariaki.choice.model.Post;

import java.util.ArrayList;
import java.util.List;

public class ChoiceViewModel extends AndroidViewModel {

    private ChoiceDatabaseRepository repository;

    private LiveData<List<ChatList>>allChannels;
    private LiveData<List<ChoiceUser>>allChoiceUser;

    private List<Post>post=new ArrayList<>();



    private LiveData<List<Message>>getChannelMessages;
    private ChoiceViewModel(@NonNull Application application) {
        super(application);
        repository = new ChoiceDatabaseRepository(application);

        allChannels=repository.getAllChannels();
        allChoiceUser=repository.getAllChoiceUsers();


    }
    public LiveData<List<CloudPost>>getAllMyPost(){
        return repository.getAllMyPost();
    }
    public void insertMyPost(CloudPost cloudPost){
        repository.insertMyPost(cloudPost);
    }

    public void deleteMyPost(CloudPost cloudPost){
        repository.deleteMyPost(cloudPost);
    }
    public void updateMyPost(CloudPost cloudPost){
        repository.updateMyPost(cloudPost);
    }


    public List<Message>getChannelMessage(String id){
       return repository.getChannelMessages(id);
    }
    public List<Message> getSingleMessage(String messageID) {
        return repository.getGetSingleMessage(messageID);
    }

    public void insertChat(MyChatChannel chatList){
        repository.insertChat(chatList);
    }
    public LiveData<List<MyChatChannel>>getChatList(){
        return repository.getChatList();
    }

    public List<Post> getPost(String id) {
        return repository.getPost(id);
    }

    public LiveData<List<Post>> getAllPost() {
        return  repository.getTimeLine();
    }

    public LiveData<List<Message>>getUnSeenMessages(String channel){
       return repository.getUnSeenMessages(channel);

    }
    public void updateChatList(MyChatChannel myChatChannel){
        repository.updateChatList(myChatChannel);
    }

    public void deletMessage(Message message){
        repository.deleteMessage(message);

    }

    public void insertPost(Post post){
        repository.insertPost(post);
    }

    public void deletePost(Post post){
        repository.deletePost(post);
    }

    public void updatePost(Post post){
        repository.updatePost(post);
    }
    private static ChoiceViewModel choiceViewModel;

    public static ChoiceViewModel getInstance(Application application){
        if(choiceViewModel==null){
            choiceViewModel=new ChoiceViewModel(application);
        }
        return choiceViewModel;
    }



    public LiveData<List<ChatAndPostNotification>>getThisNotification(int type){

        return repository.getAllThisNotification(type);

    }
    public void insertNotification(ChatAndPostNotification notification){
        repository.insertNotification(notification);
    }

    public void deleteNotification(ChatAndPostNotification notification){
        repository.deleteNotification(notification);

    }
    public LiveData<List<ChoiceUser>> getAllChoiceUser() {
        return allChoiceUser;
    }

    public LiveData<List<ChoiceUser>>getChoiceUser(String phone){
        return repository.getParticularUser(phone);
    }

    public void insertChoiceUserToLocaldb(ChoiceUser choiceUser){
        repository.insertChoiceUser(choiceUser);

    }

    public void updateMessage(Message message){
        repository.updateMessage(message);
    }

    public LiveData<List<ChatList>> getAllChannels() {
        return allChannels;
    }

    public LiveData<List<Message>> getGetChannelMessages(String channelID) {
        return repository.getAllChannelMessages(channelID);
    }

    public void insertMessage(Message message){

        repository.insertMessage(message);

    }

}
