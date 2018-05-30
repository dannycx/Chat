package com.danny.chat.data.call;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * 未接电话表
 * Created by danny on 5/7/18.
 */

@Entity(tableName = "call")
public class MissedCall {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "call_id")
    public int callId;
    @ColumnInfo(name = "call_account")
    public String callAccount;
    @ColumnInfo(name = "call_name")
    public String callName;
    public int count;

    @Override
    public String toString() {
        return "MissedCall{" +
                "callId=" + callId +
                ", callAccount='" + callAccount + '\'' +
                ", callName='" + callName + '\'' +
                ", count=" + count +
                '}';
    }
}
