package com.danny.chat.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Io线程类
 * Created by danny on 3/30/18.
 */

public class IoExecutor implements Executor {
    private Executor mIoExecutor;

    public IoExecutor() {
        mIoExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        mIoExecutor.execute(runnable);
    }
}
