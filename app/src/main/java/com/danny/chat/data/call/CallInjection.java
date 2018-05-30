package com.danny.chat.data.call;

import android.content.Context;

import com.danny.chat.data.LoginDatabase;
import com.danny.chat.utils.LoginExecutors;

/**
 * 通过该类拿到未接电话表操作对象-CallRepository
 * Created by danny on 3/31/18.
 */

public class CallInjection {
    public static CallRepository getInstance(Context context) {
        LoginDatabase database = LoginDatabase.getInstance(context);
        CallRepository repository = CallRepository.getInstance(LocalCallDataSource.getInstance(new LoginExecutors(), database.missedCallDao()));
        return repository;
    }
}
