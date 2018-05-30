package com.danny.chat.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * 好友表
 * Created by danny on 3/30/18.
 */
@Entity(tableName = "friend"
        , indices = @Index(value = {"user_id", "friend_id"}, unique = true)
        , foreignKeys = @ForeignKey(entity = User.class
        , parentColumns = "id"
        , childColumns = "user_id"))
public class Friend {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "friend_id")
    public int friendId;
    @ColumnInfo(name = "friend_account")
    public String friendAccount;
    public String alias;
    @ColumnInfo(name = "head_image")
    public int headImage;
    @ColumnInfo(name = "user_id")
    public String userId;

    @Override
    public String toString() {
        return "Friend{" +
                "friendId=" + friendId +
                ", friendAccount='" + friendAccount + '\'' +
                ", alias='" + alias + '\'' +
                ", headImage=" + headImage +
                ", userId='" + userId + '\'' +
                '}';
    }
}
