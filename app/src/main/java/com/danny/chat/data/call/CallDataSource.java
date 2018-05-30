package com.danny.chat.data.call;

import java.util.List;

/**
 * 操作用户数据的接口
 * Created by danny on 3/30/18.
 */

public interface CallDataSource {
    void insertCall(MissedCall call, SuccessCallback callback);

    void queryAllCall(LoadCallCallback callback);

    void queryPointCall(String account, LoadPointCallCallback callback);

    void updateCallCount(int i, String account, SuccessCallback callback);

    void deleteCall(String account);

    void deleteCall();

    //加载所有未接电话
    interface LoadCallCallback {
        void onSuccess(List<MissedCall> calls);

        void onError();
    }

    //获取指定号码未接电话
    interface LoadPointCallCallback {
        void onSuccess(MissedCall call);

        void onError();
    }

    interface SuccessCallback {
        void onSuccess();
    }
}
