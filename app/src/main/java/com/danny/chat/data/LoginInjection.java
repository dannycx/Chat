package com.danny.chat.data;

import android.content.Context;

import com.danny.chat.utils.LoginExecutors;

/**
 * Created by danny on 3/31/18.
 */

public class LoginInjection {
    public static LoginRepository getInstance(Context context) {
        LoginDatabase database = LoginDatabase.getInstance(context);
        LoginRepository repository = LoginRepository.getInstance(LocalLoginDataSource.getInstance(new LoginExecutors(), database.uerDao()));
        return repository;
    }
}
