package com.danny.chat.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * 表操作接口
 * Created by danny on 3/30/18.
 */
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("SELECT * From user WHERE account= :account")
    User queryUser(String account);

    @Query("SELECT * From user")
    List<User> queryAllUser();

    @Query("DELETE FROM user WHERE account= :account")
    void deleteUser(String account);

    @Query("DELETE FROM user")
    void deleteUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriend(Friend friend);

    @Query("SELECT * FROM friend WHERE friend_account= :friendAccount")
    Friend queryFriend(String friendAccount);

    @Query("SELECT * FROM friend WHERE alias= :alias")
    Friend queryAliasFriend(String alias);

    @Query("SELECT friend_account FROM friend WHERE user_id= :userId")
    List<String> queryAllFriendAccount(String userId);

    @Query("SELECT * FROM friend WHERE user_id= :userId")
    List<Friend> queryAllFriend(String userId);

    @Query("DELETE FROM friend")
    void deleteFriend();

    @Query("DELETE FROM friend WHERE friend_account= :friendAccount")
    void deleteFriend(String friendAccount);

    @Query("DELETE FROM friend WHERE alias= :alias")
    void deleteAliasFriend(String alias);
}
