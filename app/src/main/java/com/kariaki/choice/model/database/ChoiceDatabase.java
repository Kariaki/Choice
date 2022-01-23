package com.kariaki.choice.model.database;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.entities.MyChatChannel;
import com.kariaki.choice.model.Post;


@Database(entities = { ChoiceUser.class, Message.class,ChatAndPostNotification.class,
        Post.class, MyChatChannel.class, CloudPost.class},version = 8)
public  abstract class ChoiceDatabase extends RoomDatabase {

    private static ChoiceDatabase database;
    public abstract DatabaseDao choiceDao();

    public static synchronized ChoiceDatabase getInstance(Application application){

        if(database==null){

            database= Room.databaseBuilder(application.getApplicationContext(), ChoiceDatabase.class,
                    "ChoiceDatabase").fallbackToDestructiveMigration()

                    .build();

        }
        return database;



    }


}
