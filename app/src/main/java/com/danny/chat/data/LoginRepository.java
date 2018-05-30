package com.danny.chat.data;

/**
 * Created by danny on 3/31/18.
 */

public class LoginRepository implements LoginDataSource {
    private static LoginRepository sInstance = null;
    private LocalLoginDataSource mSource;

    private LoginRepository(LocalLoginDataSource dataSource) {
        mSource = dataSource;
    }

    public static LoginRepository getInstance(LocalLoginDataSource dataSource) {
        if (sInstance == null) {
            sInstance = new LoginRepository(dataSource);
        }
        return sInstance;
    }

    @Override
    public void insertUser(User user) {
        mSource.insertUser(user);
    }

    @Override
    public void queryUser(String account, LoadUserCallback callback) {
        mSource.queryUser(account, callback);
    }

    @Override
    public void queryAllUser(LoadAllUserCallback callback) {
        mSource.queryAllUser(callback);
    }

    @Override
    public void deleteUser() {
        mSource.deleteUser();
    }

    @Override
    public void deleteUser(String account) {
        mSource.deleteUser(account);
    }

    @Override
    public void insertFriend(Friend friend, AddFriendSuccessCallback callback) {
        mSource.insertFriend(friend, callback);
    }

    @Override
    public void queryFriend(String friendAccount, LoadFriendCallback callback) {
        mSource.queryFriend(friendAccount, callback);
    }

    @Override
    public void queryAliasFriend(String alias, LoadFriendCallback callback) {
        mSource.queryAliasFriend(alias, callback);
    }

    @Override
    public void queryAllFriend(String userId, LoadAllFriendCallback callback) {
        mSource.queryAllFriend(userId, callback);
    }

    @Override
    public void queryAllFriendAccount(String userId, LoadAllFriendAccountCallback callback) {
        mSource.queryAllFriendAccount(userId, callback);
    }

    @Override
    public void deleteFriend() {
        mSource.deleteFriend();
    }

    @Override
    public void deleteFriend(String friendAccount, DeleteFriendSuccessCallback callback) {
        mSource.deleteFriend(friendAccount, callback);
    }

    @Override
    public void deleteAliasFriend(String alias, DeleteFriendSuccessCallback callback) {
        mSource.deleteAliasFriend(alias, callback);
    }
}
