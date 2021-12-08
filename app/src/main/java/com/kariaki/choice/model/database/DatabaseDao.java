package com.kariaki.choice.model.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.Database.Entities.ChatList;
import com.kariaki.choice.model.Database.Entities.ChoiceUser;
import com.kariaki.choice.model.Database.Entities.Message;
import com.kariaki.choice.model.Database.Entities.MyChatChannel;
import com.kariaki.choice.model.Post;

import java.util.List;

@Dao
public interface DatabaseDao {



    @Insert(onConflict = OnConflictStrategy.IGNORE)

    void insertMessage(Message message);

    @Query("select * from messagetable where channelID like:MessagechannelID order by messageTIME asc ")
    LiveData<List<Message>> getAllChannelLiveMessages(String MessagechannelID);
    @Query("select * from messagetable where channelID like:MessagechannelID order by messageTIME asc ")
    List<Message>getAllChannelMessages(String MessagechannelID);

    @Delete
    void deleteMessage(Message message);

    @Update
    void updateMessage(Message message);


    @Query("select * from messagetable where messageState like:messageState and channelID like:channel ")
    LiveData<List<Message>>unSeenMessages(int messageState, String channel);

    @Query("select * from messagetable where messageID like:messageID")
    List<Message>getSingleMessage(String messageID);




    @Query("select  channelID,messageTIME,channelType from messagetable group by channelID order by messageTIME desc" )

    LiveData<List<ChatList>>getAllChatChannels();


    // users table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(ChoiceUser choiceUser);

    @Query("select * from usertable where userPhoneNumber like:phoneNumber")

    LiveData<List<ChoiceUser>> getParticularUser(String phoneNumber);

    @Query("select * from usertable order by userContactName asc")
    LiveData<List<ChoiceUser>>getAllUsers();
    @Query("select * from my_post_table order by POST_TIME desc")
    LiveData<List<CloudPost>>getAllMyPost();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMyPost(CloudPost cloudPost);
    @Update
    void updateMyPost(CloudPost cloudPost);
    @Delete
    void deleteMyPost(CloudPost cloudPost);


    //Chat and post Notification queries

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNotification(ChatAndPostNotification notification);
    @Delete
    void deleteNotification(ChatAndPostNotification notification);

    @Query("select * from chatAndPostNotificationTable where notificationType like:type")

    LiveData<List<ChatAndPostNotification>>getAllThisNotification(int type);

    //post queries

    @Query("select * from post_table order by POST_TIME desc")
    LiveData<List<Post>>getAllPosts();

    @Query("select * from post_table where postID like:id")
    List<Post> getPost(String id);
    @Update
    void updatePost(Post post);
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertPost(Post post);
    @Delete
    void deletePost(Post post);

    // chat orders
    @Query("select * from channels order by lastMessageTime desc")
    LiveData<List<MyChatChannel>>orderChats();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChat(MyChatChannel chatList);
    @Update
    void updateChat(MyChatChannel chatList);
    @Delete
    void removeChat(MyChatChannel chatList);


}
