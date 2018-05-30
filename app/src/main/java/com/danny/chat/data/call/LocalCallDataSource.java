package com.danny.chat.data.call;


import com.danny.chat.utils.LoginExecutors;

import java.util.List;

/**
 * 数据库操作类实现
 * Created by danny on 3/30/18.
 */

public class LocalCallDataSource implements CallDataSource {
    private static volatile LocalCallDataSource sInstance;
    private LoginExecutors mNabooExecutors;
    private MissedCallDao mMissedCallDao;
    private List<MissedCall> mMissedCalls;
    private MissedCall mCall;

    private LocalCallDataSource(LoginExecutors nabooExecutors, MissedCallDao callDao) {
        mNabooExecutors = nabooExecutors;
        mMissedCallDao = callDao;
    }

    public static LocalCallDataSource getInstance(LoginExecutors loginExecutors, MissedCallDao callDao) {
        if (sInstance == null) {
            synchronized (LocalCallDataSource.class) {
                if (sInstance == null) {
                    sInstance = new LocalCallDataSource(loginExecutors, callDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void insertCall(final MissedCall call, final SuccessCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mMissedCallDao.insertCall(call);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllCall(final LoadCallCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mMissedCalls = mMissedCallDao.queryAllCall();
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mMissedCalls.isEmpty()) {
                            if (callback != null) {
                                callback.onSuccess(mMissedCalls);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryPointCall(final String account, final LoadPointCallCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mCall = mMissedCallDao.queryPointCall(account);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mCall != null) {
                            if (callback != null) {
                                callback.onSuccess(mCall);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void updateCallCount(final int i, final String account, final SuccessCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mMissedCallDao.updateCallCount(i, account);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteCall(final String account) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mMissedCallDao.deleteCall(account);
            }
        });
    }

    @Override
    public void deleteCall() {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mMissedCallDao.deleteCall();
            }
        });
    }
}
