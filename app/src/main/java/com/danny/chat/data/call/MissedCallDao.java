package com.danny.chat.data.call;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * 未接电话表操作接口
 * Created by danny on 5/7/18.
 */

@Dao
public interface MissedCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCall(MissedCall call);

    @Query("SELECT * From call")
    List<MissedCall> queryAllCall();

    @Query("SELECT * From call WHERE call_account= :account")
    MissedCall queryPointCall(String account);

    @Query("UPDATE call SET count= :i+count WHERE call_account= :account")
    void updateCallCount(int i, String account);

    @Query("DELETE FROM call WHERE call_account= :account")
    void deleteCall(String account);

    @Query("DELETE FROM call")
    void deleteCall();
}
