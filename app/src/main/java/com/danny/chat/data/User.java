package com.danny.chat.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * 用户表
 * Created by danny on 3/30/18.
 */
@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @NonNull
    public String id;
    public String account;
    public String alias;
    @ColumnInfo(name = "head_image")
    public int headImage;
}
