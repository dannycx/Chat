package com.danny.chat.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 应用线程管理类
 * Created by danny on 3/30/18.
 */

public class LoginExecutors {
    private static final int THREAD_COUNT = 3;
    private Executor mMainExecutor;
    private Executor mIoExecutor;
    private Executor mNetworkExecutor;

    LoginExecutors(Executor mainExecutor, Executor ioExecutor, Executor networkExecutor) {
        mMainExecutor = mainExecutor;
        mIoExecutor = ioExecutor;
        mNetworkExecutor = networkExecutor;
    }

    public LoginExecutors() {
        this(new MainExecutor(), new IoExecutor(), Executors.newFixedThreadPool(THREAD_COUNT));
    }

    public Executor getMainExecutor() {
        return mMainExecutor;
    }

    public Executor getIoExecutor() {
        return mIoExecutor;
    }

    public Executor getNetworkExecutor() {
        return mNetworkExecutor;
    }

    private static class MainExecutor implements Executor {
        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mHandler.post(runnable);
        }
    }
}
