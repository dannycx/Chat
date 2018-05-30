package com.danny.chat.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.danny.chat.data.call.MissedCall;
import com.danny.chat.data.call.MissedCallDao;

/**
 * 数据库
 * Created by danny on 3/30/18.
 */
@Database(entities = {User.class, Friend.class, MissedCall.class}, version = 1, exportSchema = false)
public abstract class LoginDatabase extends RoomDatabase {
    private static final Object sLock = new Object();
    private static LoginDatabase sInstance;

    public static LoginDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (sLock) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext()
                            , LoginDatabase.class, "login.db").build();
                }
            }
        }
        return sInstance;
    }

    public abstract UserDao uerDao();

    public abstract MissedCallDao missedCallDao();

    public void close() {
        if (sInstance != null) {
            sInstance.close();
        }
    }
}
