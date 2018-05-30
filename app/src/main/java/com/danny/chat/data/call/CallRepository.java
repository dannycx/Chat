package com.danny.chat.data.call;

/**
 * Created by danny on 3/31/18.
 */

public class CallRepository implements CallDataSource {
    private static CallRepository sInstance = null;
    private LocalCallDataSource mSource;

    private CallRepository(LocalCallDataSource dataSource) {
        mSource = dataSource;
    }

    public static CallRepository getInstance(LocalCallDataSource dataSource) {
        if (sInstance == null) {
            sInstance = new CallRepository(dataSource);
        }
        return sInstance;
    }

    @Override
    public void insertCall(MissedCall call, SuccessCallback callback) {
        mSource.insertCall(call, callback);
    }

    @Override
    public void queryAllCall(LoadCallCallback callback) {
        mSource.queryAllCall(callback);
    }

    @Override
    public void queryPointCall(String account, LoadPointCallCallback callback) {
        mSource.queryPointCall(account, callback);
    }

    @Override
    public void updateCallCount(int i, String account, SuccessCallback callback) {
        mSource.updateCallCount(i, account, callback);
    }

    @Override
    public void deleteCall(String account) {
        mSource.deleteCall(account);
    }

    @Override
    public void deleteCall() {
        mSource.deleteCall();
    }
}
