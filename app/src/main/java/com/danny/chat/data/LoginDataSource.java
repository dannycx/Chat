package com.danny.chat.data;

import java.util.List;

/**
 * 操作用户数据的接口
 * Created by danny on 3/30/18.
 */

public interface LoginDataSource {
    void insertUser(User user);

    void queryUser(String account, LoadUserCallback callback);

    void queryAllUser(LoadAllUserCallback callback);

    void deleteUser();

    void deleteUser(String account);

    void insertFriend(Friend friend, AddFriendSuccessCallback callback);

    void queryFriend(String friendAccount, LoadFriendCallback callback);

    void queryAliasFriend(String alias, LoadFriendCallback callback);

    void queryAllFriend(String userId, LoadAllFriendCallback callback);

    void queryAllFriendAccount(String userId, LoadAllFriendAccountCallback callback);

    void deleteFriend();

    void deleteFriend(String friendAccount, DeleteFriendSuccessCallback callback);

    void deleteAliasFriend(String alias, DeleteFriendSuccessCallback callback);

    //加载单个用户信息
    interface LoadUserCallback {
        void onSuccess(User user);

        void onError();
    }

    //加载所有用户信息
    interface LoadAllUserCallback {
        void onSuccess(List<User> user);

        void onError();
    }

    //加载单个好友信息
    interface LoadFriendCallback {
        void onSuccess(Friend friend);

        void onError();
    }

    //加载当前用户所有好友信息
    interface LoadAllFriendCallback {
        void onSuccess(List<Friend> friends);

        void onError();
    }

    //加载当前用户所有好友账号信息
    interface LoadAllFriendAccountCallback {
        void onSuccess(List<String> accounts);

        void onError();
    }

    interface DeleteFriendSuccessCallback {
        void onSuccess();
    }

    interface AddFriendSuccessCallback {
        void onSuccess();
    }
}
