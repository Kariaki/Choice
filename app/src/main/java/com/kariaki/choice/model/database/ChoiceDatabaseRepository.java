package com.kariaki.choice.model.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.Database.Entities.ChatList;
import com.kariaki.choice.model.Database.Entities.ChoiceUser;
import com.kariaki.choice.model.Database.Entities.Message;
import com.kariaki.choice.model.Database.Entities.MyChatChannel;
import com.kariaki.choice.model.Post;

import java.util.List;

public class ChoiceDatabaseRepository {

    private DatabaseDao dao;

    private LiveData<List<ChatList>>allChannels;
    private LiveData<List<ChoiceUser>>allChoiceUsers;
    private LiveData<List<ChatAndPostNotification>>allThisNotification;
    private LiveData<List<Message>>unSeenMessages;
    private LiveData<List<Post>>timeLine;
    private LiveData<List<MyChatChannel>>chatList;
    private LiveData<List<Message>>getSingleMessage;

    public ChoiceDatabaseRepository(Application application){
        ChoiceDatabase database=ChoiceDatabase.getInstance(application);
        dao=database.choiceDao();

        allChannels=dao.getAllChatChannels();
        allChoiceUsers=dao.getAllUsers();
        timeLine=dao.getAllPosts();

        chatList=dao.orderChats();


    }
    public List<Message>getChannelMessages(String channel){
        return dao.getAllChannelMessages(channel);
    }
    public LiveData<List<CloudPost>>getAllMyPost(){
        return dao.getAllMyPost();
    }

    public List<Message> getGetSingleMessage(String messageID) {
        return dao.getSingleMessage(messageID);

    }

    public LiveData<List<MyChatChannel>> getChatList() {
        return chatList;
    }

    public void deleteMessage(Message post){
        new deleteMessageAsyncTask(dao).execute(post);
    }


    public LiveData<List<Post>> getTimeLine() {
        return timeLine;
    }

    public List<Post> getPost(String id) {
        return dao.getPost(id);
    }

    public LiveData<List<Message>> getUnSeenMessages(String channel) {
        unSeenMessages=dao.unSeenMessages(MessageState.SENT,channel);
        return unSeenMessages;
    }

    public LiveData<List<ChatAndPostNotification>> getAllThisNotification(int type) {
        return dao.getAllThisNotification(type);
    }



    public LiveData<List<ChoiceUser>> getParticularUser(String phoneNumber) {

        return dao.getParticularUser(phoneNumber);

    }

    public LiveData<List<ChoiceUser>> getAllChoiceUsers() {
        return allChoiceUsers;
    }
    public void insertNotification(ChatAndPostNotification notification){
        new insertNotificationAsyncTask(dao).execute(notification);
    }

    public void deleteNotification(ChatAndPostNotification notification){
        new deleteNotificaionAsyncTask(dao).execute(notification);
    }

    public void insertChoiceUser(ChoiceUser user){
        new insertUserAsyncTask(dao).execute(user);
    }


    public LiveData<List<ChatList>> getAllChannels() {
        return allChannels;
    }

    public void insertMessage(Message message){
        new insertMessageAsyncTask(dao).execute(message);
    }

    // update message

    public void updateMessage(Message message){
        new updateMessageAsyncTask(dao).execute(message);
    }

    public void insertChat(MyChatChannel chatList){
        new insertChatAsyncTask(dao).execute(chatList);
    }

    public void updateChatList(MyChatChannel myChatChannel){
        new updateChatList(dao).execute(myChatChannel);
    }
    public LiveData<List<Message>> getAllChannelMessages(String channelID) {
        return dao.getAllChannelLiveMessages(channelID);
    }
    public void deleteMyPost(CloudPost cloudPost){
        new deleteMyPostAsyncTask(dao).execute(cloudPost);
    }


    public void insertMyPost(CloudPost cloudPost){
        new insertMyPostAsyncTask(dao).execute(cloudPost);
    }

    public void updateMyPost(CloudPost cloudPost){
        new updateMyPostAsyncTask(dao).execute(cloudPost);
    }

    //post async task

    public void updatePost(Post post){
        new updatePostAsyncTask(dao).execute(post);
    }
    public void insertPost(Post post){
        new insertPostAsyncTask(dao).execute(post);
    }

    public void deletePost(Post post){
        new deletePostAsyncTask(dao).execute(post);
    }



    private class insertMessageAsyncTask extends AsyncTask<Message,Void,Void>{

        private DatabaseDao DAO;
        public insertMessageAsyncTask(DatabaseDao databaseDao){
            this.DAO=databaseDao;
        }
        @Override
        protected Void doInBackground(Message... messages) {
             DAO.insertMessage(messages[0]);
             return null;
        }
    }


    private class insertUserAsyncTask extends AsyncTask<ChoiceUser,Void,Void>{

        DatabaseDao databaseDao;
        public insertUserAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(ChoiceUser... choiceUsers) {
            databaseDao.insertUser(choiceUsers[0]);

            return null;
        }
    }

    private class insertNotificationAsyncTask extends AsyncTask<ChatAndPostNotification,Void,Void>{
        private DatabaseDao databaseDao;
        public insertNotificationAsyncTask(DatabaseDao dao){
            this.databaseDao=dao;

        }

        @Override
        protected Void doInBackground(ChatAndPostNotification... chatAndPostNotifications) {
            databaseDao.insertNotification(chatAndPostNotifications[0]);

            return null;
        }
    }

    private class deleteNotificaionAsyncTask extends AsyncTask<ChatAndPostNotification,Void,Void>{
        private DatabaseDao databaseDao;
        public deleteNotificaionAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;

        }
        @Override
        protected Void doInBackground(ChatAndPostNotification... chatAndPostNotifications) {
            databaseDao.deleteNotification(chatAndPostNotifications[0]);
            return null;
        }
    }

    private class updateMessageAsyncTask extends AsyncTask<Message,Void,Void>{

        private DatabaseDao databaseDao;
        public updateMessageAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(Message... messages) {
            databaseDao.updateMessage(messages[0]);
            return null;
        }
    }


    private class insertPostAsyncTask extends AsyncTask<Post,Void,Void>{

        private DatabaseDao databaseDao;
        public insertPostAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(Post... posts) {
            databaseDao.insertPost(posts[0]);
            return null;
        }
    }


    private class updatePostAsyncTask extends AsyncTask<Post,Void,Void>{

        private DatabaseDao databaseDao;
        public updatePostAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(Post... posts) {
            databaseDao.updatePost(posts[0]);
            return null;
        }
    }

    private class updateChatList extends AsyncTask<MyChatChannel,Void,Void>{

        private DatabaseDao databaseDao;
        public updateChatList(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(MyChatChannel... posts) {
            databaseDao.updateChat(posts[0]);
            return null;
        }
    }
    private class deletePostAsyncTask extends AsyncTask<Post,Void,Void>{

        private DatabaseDao databaseDao;

        public deletePostAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(Post... posts) {
            databaseDao.deletePost(posts[0]);
            return null;
        }
    }


    private class deleteMessageAsyncTask extends AsyncTask<Message,Void,Void>{

        private DatabaseDao databaseDao;

        public deleteMessageAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(Message... posts) {
            databaseDao.deleteMessage(posts[0]);
            return null;
        }
    }

    private class insertChatAsyncTask extends AsyncTask<MyChatChannel,Void,Void>{

        private DatabaseDao databaseDao;

        public insertChatAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(MyChatChannel... posts) {
            databaseDao.insertChat(posts[0]);
            return null;
        }
    }

    private class insertMyPostAsyncTask extends AsyncTask<CloudPost,Void,Void>{

        private DatabaseDao databaseDao;

        public insertMyPostAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(CloudPost... posts) {
            databaseDao.insertMyPost(posts[0]);
            return null;
        }
    }

    private class updateMyPostAsyncTask extends AsyncTask<CloudPost,Void,Void>{

        private DatabaseDao databaseDao;

        public updateMyPostAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(CloudPost... posts) {
            databaseDao.updateMyPost(posts[0]);
            return null;
        }
    }

    private class deleteMyPostAsyncTask extends AsyncTask<CloudPost,Void,Void>{

        private DatabaseDao databaseDao;

        public deleteMyPostAsyncTask(DatabaseDao databaseDao){
            this.databaseDao=databaseDao;
        }
        @Override
        protected Void doInBackground(CloudPost... posts) {
            databaseDao.deleteMyPost(posts[0]);
            return null;
        }
    }

}
